package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import java.util.Date;
import java.util.HashMap;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.animations.FadeBackgroundPageTransformer;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.GraphFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.StatsFragment;
import uk.co.pilllogger.helpers.FeedbackHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetFavouritePillsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.GetTutorialSeenTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tutorial.ConsumptionListTutorialPage;
import uk.co.pilllogger.tutorial.PillsListTutorialPage;
import uk.co.pilllogger.tutorial.TutorialPage;
import uk.co.pilllogger.tutorial.TutorialService;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.MyViewPager;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends PillLoggerActivityBase implements
        GetPillsTask.ITaskComplete,
        Observer.IPillsUpdated,
        GetFavouritePillsTask.ITaskComplete,
        GetTutorialSeenTask.ITaskComplete{

    private static final String TAG = "MainActivity";
    private MyViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;
    private int _colour1 = Color.argb(120, 0, 233, 255);
    private int _colour2 = Color.argb(120, 204, 51, 153);
    private int _colour3 = Color.argb(120, 81, 81, 81);
    View _colourBackground;
    private Menu _menu;
    Fragment _consumptionFragment;
    private TutorialService _tutorialService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

        if(!isDebuggable)
            Crashlytics.start(this);

        ViewGroup wrapper = setContentViewWithWrapper(R.layout.activity_main);
        this.setTitle("Consumption");
        _colourBackground = findViewById(R.id.colour_background);

        // Typeface ttf = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");
        Typeface ttf = Typeface.create("sans-serif-condensed", Typeface.NORMAL);

        State.getSingleton().setTypeface(ttf);

        _consumptionFragment = new ConsumptionListFragment();
        final Fragment fragment2 = new PillListFragment();
        final Fragment fragment3 = new StatsFragment();

        _fragmentPager = (MyViewPager)findViewById(R.id.fragment_pager);

        final MainActivity activity = this;

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

        _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager(),
                _consumptionFragment,
                fragment2,
                fragment3);

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

        _fragmentPager.setPageTransformer(true, new FadeBackgroundPageTransformer(_colourBackground, this));

        Observer.getSingleton().registerPillsUpdatedObserver(this);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            int version = pInfo.versionCode;
            int seenVersion = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.seenVersionKey), 0);

            if(version > seenVersion)
                showChangesDialog();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostResume() {
        new GetPillsTask(this, this).execute();
        SetupTutorial();
        super.onPostResume();
    }

    private void showChangesDialog(){
        Intent composeIntent = new Intent(this, WebViewActivity.class);
        composeIntent.putExtra(getString(R.string.key_show_feedback_button), true);
        composeIntent.putExtra(getString(R.string.key_web_address), "file:///android_asset/html/changelog.html");

        startActivity(composeIntent);
    }

    private void setBackgroundColour(){
        int page = _fragmentPager.getCurrentItem();

        Log.d(TAG, "Set currentItem to " + page);
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
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            //actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);

            // Specify that tabs should be displayed in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // Create a tab listener that is called when the user changes tabs.
            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    _fragmentPager.setCurrentItem(tab.getPosition());
                }

                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // hide the given tab
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

            // force embedded tabs
//            try {
//                final Method setHasEmbeddedTabsMethod = actionBar.getClass()
//                        .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
//                setHasEmbeddedTabsMethod.setAccessible(true);
//                setHasEmbeddedTabsMethod.invoke(actionBar, true);
//            }
//            catch(final Exception e) {
//                // Handle issues as needed: log, warn user, fallback etc
//                // Alternatively, ignore this and default tab behaviour will apply.
//            }

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
        new GetFavouritePillsTask(this, this).execute();
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
        //
        // here we need to decide how we are going to launch new activities.
        // I think we need to replace the whole fragment (view pager included), not sure how to do that atm.
        //
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
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void pillsReceived(List<Pill> pills) {
        if(pills.size() == 0)
        {
            startAddConsumptionActivity();
        }
    }

    public void updateMenuWithFavouritePills(List<Pill> favouritePills) {

    }

    private void addPillToMenu(Pill pill){
        MenuItem item = _menu.findItem(pill.getId());
        if(item == null)
            item = _menu.add(Menu.NONE, pill.getId(), Menu.NONE, "Take " + pill.getName());

        item.setTitleCondensed(pill.getName().substring(0,1));
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
        new InsertConsumptionTask(MainActivity.this, consumption).execute();

        TrackerHelper.addConsumptionEvent(MainActivity.this, "FavouriteMenu");

        //if (_consumptionFragment.isResumed())
        //    new GetConsumptionsTask(MainActivity.this, (GetConsumptionsTask.ITaskComplete)_consumptionFragment, true).execute();
        Toast.makeText(MainActivity.this, "Added consumption of " + pill.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void pillsUpdated(final Pill pill) {
        if(_menu == null || pill == null)
            return;

        runOnUiThread(new Runnable(){
            public void run(){
                if(pill.isFavourite()){
                    addPillToMenu(pill);
                }
                else{
                    _menu.removeItem(pill.getId());
                }
            }
        });
    }

    @Override
    public void favouritePillsReceived(List<Pill> pills) {
        if(_menu == null)
            return;

        for (Pill pill : pills) {
            if (_menu.findItem(pill.getId()) == null) {
                addPillToMenu(pill);
            }
        }
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

}