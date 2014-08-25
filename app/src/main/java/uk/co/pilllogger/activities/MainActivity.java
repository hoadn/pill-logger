package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.UiModule;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.animations.FadeBackgroundPageTransformer;
import uk.co.pilllogger.billing.IabHelper;
import uk.co.pilllogger.billing.IabResult;
import uk.co.pilllogger.billing.Inventory;
import uk.co.pilllogger.billing.SkuDetails;
import uk.co.pilllogger.dialogs.ThemeChoiceDialog;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.StatsFragment;
import uk.co.pilllogger.helpers.CrashlyticsTree;
import uk.co.pilllogger.helpers.ExportHelper;
import uk.co.pilllogger.helpers.FeedbackHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.InsertConsumptionJob;
import uk.co.pilllogger.jobs.LoadPillsJob;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.FeatureType;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetTutorialSeenTask;
import uk.co.pilllogger.themes.ITheme;
import uk.co.pilllogger.themes.ProfessionalTheme;
import uk.co.pilllogger.themes.RainbowTheme;
import uk.co.pilllogger.tutorial.ConsumptionListTutorialPage;
import uk.co.pilllogger.tutorial.PillsListTutorialPage;
import uk.co.pilllogger.tutorial.TutorialPage;
import uk.co.pilllogger.tutorial.TutorialService;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.MyViewPager;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends PillLoggerActivityBase implements
        GetTutorialSeenTask.ITaskComplete,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    PillRepository _pillRepository;

    private static final String TAG = "MainActivity";
    private MyViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;
    private int _colour1 = Color.argb(120, 0, 233, 255);
    private int _colour2 = Color.argb(120, 204, 51, 153);
    private int _colour3 = Color.argb(120, 81, 81, 81);
    View _colourBackground;
    private Menu _menu;
    private TutorialService _tutorialService;
    private boolean _themeChanged;
    private IabHelper _billingHelper;
    private boolean _dialogShown = false;
    @Inject JobManager _jobManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDebuggable = 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        State.getSingleton().setIsDebuggable(isDebuggable);

        _themeChanged = false;

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        updateTheme(getString(R.string.pref_key_theme_list));

        ViewGroup wrapper = setContentViewWithWrapper(R.layout.activity_main);
        this.setTitle("Consumption");
        _colourBackground = findViewById(R.id.colour_background);

        Typeface ttf = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        State.getSingleton().setTypeface(ttf);
        State.getSingleton().setRobotoTypeface(roboto);

        final MainActivity activity = this;

        _fragmentPager = (MyViewPager)findViewById(R.id.fragment_pager);

        _fragmentPager.setOffscreenPageLimit(2);
        _fragmentPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            setBackgroundColour();
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);

                        String fragment = "";
                        switch (position) {
                            case 0:
                                fragment = ConsumptionListFragment.TAG;
                            break;
                            case 1:
                                fragment = PillListFragment.TAG;
                                break;
                        }

                        new GetTutorialSeenTask(MainActivity.this, fragment, activity).execute();
                    }
                });

        _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager());

        _fragmentPager.setAdapter(_fragmentPagerAdapter);

        if(savedInstanceState != null) {
            Log.d(TAG, "Loading instance");
            _fragmentPager.setCurrentItem(savedInstanceState.getInt("item"));
            setBackgroundColour();
        }

        View tutorial = findViewById(R.id.tutorial_layout);
        ViewGroup parent = (ViewGroup) tutorial.getParent();

        if(parent != null){
            parent.removeView(tutorial);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        tutorial.setLayoutParams(params);

        wrapper.addView(tutorial);

        setupChrome();

        int tabMaskColour = getResources().getColor(State.getSingleton().getTheme().getTabMaskColourResourceId());

        _fragmentPager.setPageTransformer(true, new FadeBackgroundPageTransformer(_colourBackground, this, tabMaskColour));

        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Timber.d("Is PillRepository cached: " + _pillRepository.isCached());
        if(_pillRepository.isCached() == false) {
            _jobManager.addJobInBackground(new LoadPillsJob());
        }

        Integer gradientBackgroundResourceId = State.getSingleton().getTheme().getWindowBackgroundResourceId();
        Drawable background = gradientBackgroundResourceId == null ? null : getResources().getDrawable(gradientBackgroundResourceId);
        getWindow().setBackgroundDrawable(background);

        TrackerHelper.launchEvent(this);
        setupBilling();
    }

    private void setupBilling(){

        String billingKey = getString(R.string.billingKey);

        _billingHelper = new IabHelper(this, billingKey);
        _billingHelper.enableDebugLogging(false, TAG);

        _billingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Timber.d("Problem setting up In-app Billing: " + result);
                }
                else {
                    State.getSingleton().setIabHelper(_billingHelper);
                    // Hooray, IAB is fully set up!
                    final List<String> features = new ArrayList<String>();
                    for (FeatureType featureType : FeatureType.values()) {
                        features.add(featureType.toString());
                    }
                    try {
                        _billingHelper.queryInventoryAsync(true, features, new IabHelper.QueryInventoryFinishedListener() {
                            @Override
                            public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
                                if (result.isFailure() || inv == null) {
                                    Timber.e("Querying billing inventory failed: " + result.getMessage());
                                } else {
                                    for (FeatureType feature : FeatureType.values()) {
                                        SkuDetails skuDetails = inv.getSkuDetails(feature.toString());
                                        if (skuDetails == null) {
                                            continue;
                                        }
                                        State.getSingleton().getAvailableFeatures().put(feature, skuDetails);

                                        if (inv.hasPurchase(feature.toString())) {
                                            State.getSingleton().getEnabledFeatures().add(feature);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    catch(IllegalStateException ise){
                        Timber.e(ise, "Problem setting up In-app Billing");
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if(State.getSingleton().getIabHelper() == null) {
            return;
        }

        // Pass on the activity result to the helper for handling
        if (!State.getSingleton().getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onDestroy(){
        if (_billingHelper != null) {
            _billingHelper.dispose();
        }
        _billingHelper = null;

        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(_themeChanged){
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onPostResume() {
        SetupTutorial();
        super.onPostResume();
    }

    private void showChangesDialog(){
        if(_dialogShown){
            return;
        }

        _dialogShown = true;
        Intent composeIntent = new Intent(this, WebViewActivity.class);
        composeIntent.putExtra(getString(R.string.key_show_feedback_button), true);
        composeIntent.putExtra(getString(R.string.key_web_address), "file:///android_asset/html/changelog.html");

        startActivity(composeIntent);
    }

    private void setBackgroundColour(){
        int page = _fragmentPager.getCurrentItem();

        // TODO: This code will break when colours change in fragments, needs to be updated
        switch(page) {
            case 0:
                _colourBackground.setBackgroundColor(_colour1);
                break;
            case 1:
                _colourBackground.setBackgroundColor(_colour2);
                break;
            case 2:
                _colourBackground.setBackgroundColor(_colour3);
                break;
        }
    }

    private void setupChrome(){
        final ActionBar actionBar = getActionBar();
        if(actionBar != null){
            //actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);

            // Specify that tabs should be displayed in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            final ActionBar actionBar1 = actionBar;
            // Create a tab listener that is called when the user changes tabs.
            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    _fragmentPager.setCurrentItem(tab.getPosition());
                }

                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }

                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // probably ignore this event
                }
            };


            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(R.layout.tab_icon_consumptions)
                            .setTabListener(tabListener));
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(R.layout.tab_icon_pills)
                            .setTabListener(tabListener));
            actionBar.addTab(
                    actionBar.newTab()
                            .setCustomView(R.layout.tab_icon_charts)
                            .setTabListener(tabListener));

            View view = findViewById(R.id.main_gradient);
            if(view != null)
                view.setBackgroundResource(State.getSingleton().getTheme().getGradientBackgroundResourceId());
        }
    }

    private ViewGroup setContentViewWithWrapper(int resContent) {
        ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);

        // Removing decorChild, we'll add it back soon
        decorView.removeAllViews();

        ViewGroup wrapperView = new FrameLayout(this);

        // You should set some ID, if you'll want to reference this wrapper in that manner later
        //
        // The ID, such as "R.id.ACTIVITY_LAYOUT_WRAPPER" can be set at a resource file, such as:
        //  <resources xmlns:android="http://schemas.android.com/apk/res/android">
        //      <item type="id" name="ACTIVITY_LAYOUT_WRAPPER"/>
        //  </resources>
        //
        wrapperView.setId(R.id.activity_layout_wrapper);

        // Now we are rebuilding the DecorView, but this time we
        // have our wrapper view to stand between the real content and the decor
        decorView.addView(wrapperView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        wrapperView.addView(decorChild, decorChild.getLayoutParams());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            LayoutInflater.from(this).inflate(resContent, (ViewGroup)((FrameLayout)wrapperView.getChildAt(0)).getChildAt(0), true);}
        //This is for KitKat and Jelly 4.3
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            LayoutInflater.from(this).inflate(resContent, (ViewGroup) (((ViewGroup) wrapperView.getChildAt(0)).getChildAt(0)), true);}

        return wrapperView;
    }

    private void SetupTutorial(){
        HashMap<String, TutorialPage> pages = new HashMap<String, TutorialPage>();
        View tutorialLayout = findViewById(R.id.tutorial_layout);

        TutorialPage consumptionTutorial = new ConsumptionListTutorialPage(this, tutorialLayout);
        TutorialPage pillsTutorial = new PillsListTutorialPage(this, tutorialLayout);

        pages.put(ConsumptionListFragment.TAG, consumptionTutorial);
        pages.put(PillListFragment.TAG, pillsTutorial);

        _tutorialService = new TutorialService(pages);

        new GetTutorialSeenTask(MainActivity.this, ConsumptionListFragment.TAG, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        _menu = menu;
        //new GetFavouritePillsTask(this, this).execute();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving instance");
        outState.putInt("item", _fragmentPager.getCurrentItem());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_consumption:
                startAddConsumptionActivity();
                return true;
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            case R.id.action_feedback:
                sendFeedbackIntent();
                return true;
            case R.id.action_export:
                startExport();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startExport() {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendFeedbackIntent(){
        FeedbackHelper.sendFeedbackIntent(this);
    }

    private void startAddConsumptionActivity(){
        Intent intent = new Intent(this, AddConsumptionActivity.class);
        this.startActivity(intent);
    }

    @Subscribe @DebugLog
    public void pillsReceived(LoadedPillsEvent event) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            int version = pInfo.versionCode;

            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Timber.d(event.getPills().size() + "");
            if(event.getPills().size() > 0) { // if they've setup a pill (ie. they are using the app). Show recent changes
                int seenVersion = defaultSharedPreferences.getInt(getString(R.string.seenVersionKey), 0);

                Timber.d("version:" + version);
                Timber.d("seenVersion:" + seenVersion);
                if (version > seenVersion)
                    showChangesDialog();

                if(defaultSharedPreferences.getString(getString(R.string.pref_key_theme_list), "").equals("")) {
                    new ThemeChoiceDialog().show(getFragmentManager(), "ThemeChoiceDialog");
                }
            }
            else{
                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                editor.putInt(getString(R.string.seenVersionKey), version);
                editor.putString(getString(R.string.pref_key_theme_list), getString(R.string.professionalTheme));

                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addPillToMenu(Pill pill){
        MenuItem item = _menu.findItem(pill.getId());
        if(item == null)
            item = _menu.add(Menu.NONE, pill.getId(), Menu.NONE, "Take " + pill.getName());

        item.setTitleCondensed(pill.getName().substring(0, 1));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = item.getActionView();
        if(v == null)
            v = layoutInflater.inflate(R.layout.favourite_pill, null);

        if(pill.getName().length() > 0){
            ColourIndicator letter = (ColourIndicator) v.findViewById(R.id.colour);
            letter.setColour(pill.getColour());

            item.setActionView(v);

            final Pill p = pill;
            letter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View item) {
                    addConsumption(p);
                }
            });
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    addConsumption(p);
                    return false;
                }
            });
        }
    }

    private void addConsumption(Pill pill){
        Consumption consumption = new Consumption(pill, new Date());
        _jobManager.addJobInBackground(new InsertConsumptionJob(consumption));

        TrackerHelper.addConsumptionEvent(MainActivity.this, "FavouriteMenu");

        Toast.makeText(MainActivity.this, "Added consumption of " + pill.getName(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void pillsUpdated(final UpdatedPillEvent event) {
        if(_menu == null || event.getPill() == null)
            return;

        runOnUiThread(new Runnable(){
            public void run(){
                if(event.getPill().isFavourite()){
                    addPillToMenu(event.getPill());
                }
                else{
                    _menu.removeItem(event.getPill().getId());
                }
            }
        });

        Intent intent = new Intent(this, MyAppWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void startTutorial(String tag) {

        final TutorialPage page = _tutorialService.getTutorialPage(tag);
        if(page == null) {
            return; // no tutorial available for this page
        }

        if(page.getLayout() == null || page.getTutorialText() == null) return; // we can't be tutorialling if the views aren't there!

        if(page.isFinished()){
            return;
        }
        page.resetPage();
        page.getLayout().setAlpha(0f);
        page.getLayout().setVisibility(View.VISIBLE);
        page.getLayout().animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);

        page.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page.nextHint();
            }
        });
    }

    @Override
    public void isTutorialSeen(Boolean seen, String tag) {
        if(!seen) // comment this line out to force tutorial
            startTutorial(tag);
    }

    private boolean updateTheme(String key){
        if(key.equals(getString(R.string.pref_key_theme_list))) {

            String themeKey = PreferenceManager.getDefaultSharedPreferences(this).getString(key, getString(R.string.professionalTheme));
            ITheme theme = new ProfessionalTheme();

            if (themeKey.equals(getString(R.string.rainbowTheme))) {
                theme = new RainbowTheme();
            }

            if (themeKey.equals(getString(R.string.professionalTheme))) {
                theme = new ProfessionalTheme();
            }

            State.getSingleton().setTheme(theme);

            _colour1 = getResources().getColor(theme.getConsumptionListBackgroundResourceId());
            _colour2 = getResources().getColor(theme.getPillListBackgroundResourceId());
            _colour3 = getResources().getColor(theme.getStatsBackgroundResourceId());

            int styleResourceId = State.getSingleton().getTheme().getStyleResourceId();
            setTheme(styleResourceId);
            _context.setTheme(styleResourceId);

            return true;
        }

        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        _themeChanged = updateTheme(key);
    }
}
