package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.animations.FadeBackgroundPageTransformer;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.GraphFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.views.MyViewPager;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity implements GetPillsTask.ITaskComplete {

    private static final String TAG = "MainActivity";
    private MyViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;
    private int _colour1 = Color.argb(120, 0, 233, 255);
    private int _colour2 = Color.argb(120, 204, 51, 153);
    private int _colour3 = Color.argb(120, 0, 106, 255);
    View _colourBackground;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");
        _colourBackground = findViewById(R.id.colour_background);

        State.getSingleton().setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf"));

        Fragment fragment = new ConsumptionListFragment();
        Fragment fragment2 = new PillListFragment();
        Fragment fragment3 = new GraphFragment();

        _fragmentPager = (MyViewPager)findViewById(R.id.fragment_pager);

        _fragmentPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            setBackgroundColour();
                        }
                    }
                });

        _fragmentPager.setPageTransformer(true, new FadeBackgroundPageTransformer(_colourBackground, this));
        _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager(),
                fragment,
                fragment2,
                fragment3);

        _fragmentPager.setAdapter(_fragmentPagerAdapter);

        if(savedInstanceState != null) {
            _fragmentPager.setCurrentItem(savedInstanceState.getInt("item"));
        }

        setupChrome();

        setBackgroundColour();

        new GetPillsTask(this, this).execute();
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
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(false);
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

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
}