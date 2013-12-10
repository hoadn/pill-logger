package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.animations.FadeBackgroundPageTransformer;
import uk.co.pilllogger.fragments.GraphFragment;
import uk.co.pilllogger.fragments.MainFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {

    private ViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;
    private int[] _colour1 = {255, 242, 0};
    private int[] _colour2 = {0, 233, 255};
    private int[] _colour3 = {178, 255, 0};
    private int[] _fadeFrom = _colour1;
    private int[] _fadeToForward = _colour2;
    private int[] _fadeToBackward = _colour3;
    View _colourBackground;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");
        _colourBackground = findViewById(R.id.colour_background);

        State.getSingleton().setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf"));

        if(savedInstanceState == null){
            Fragment fragment = new MainFragment();
            Fragment fragment2 = new PillListFragment();
            Fragment fragment3 = new GraphFragment();

            _fragmentPager = (ViewPager)findViewById(R.id.fragment_pager);


            _fragmentPager.setOnPageChangeListener(
                    new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            // When swiping between pages, select the
                            // corresponding tab.
                            getActionBar().setSelectedNavigationItem(position);
                            switch (position) {
                                case 0:
                                    _fadeFrom = _colour1;
                                    _fadeToForward = _colour2;
                                    _fadeToBackward = _colour2;
                                    //_colourBackground.setBackgroundColor(Color.argb(120, _colour1[0], _colour1[1], _colour1[2]));
                                    Logger.v("Test", "COLOUR CHANGE 0");
                                    getActionBar().getTabAt(position).setIcon(R.drawable.list);
                                    getActionBar().getTabAt(position + 1).setIcon(R.drawable.medkit_grey);
                                    getActionBar().getTabAt(position + 2).setIcon(R.drawable.bar_chart_grey);
                                    break;
                                case 1:
                                    _fadeFrom = _colour2;
                                    _fadeToForward = _colour3;
                                    _fadeToBackward = _colour1;
                                    //_colourBackground.setBackgroundColor(Color.argb(120, _colour2[0], _colour2[1], _colour2[2]));
                                    Logger.v("Test", "COLOUR CHANGE 1");
                                    getActionBar().getTabAt(position).setIcon(R.drawable.medkit);
                                    getActionBar().getTabAt(position - 1).setIcon(R.drawable.list_grey);
                                    getActionBar().getTabAt(position + 1).setIcon(R.drawable.bar_chart_grey);
                                    break;
                                case 2:
                                    _fadeFrom = _colour3;
                                    _fadeToForward = _colour3;
                                    _fadeToBackward = _colour2;
                                    //_colourBackground.setBackgroundColor(Color.argb(120, _colour3[0], _colour3[1], _colour3[2]));
                                    Logger.v("Test", "COLOUR CHANGE 2");
                                    getActionBar().getTabAt(position).setIcon(R.drawable.bar_chart);
                                    getActionBar().getTabAt(position - 1).setIcon(R.drawable.medkit_grey);
                                    getActionBar().getTabAt(position - 2).setIcon(R.drawable.list_grey);
                                    break;
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                            super.onPageScrollStateChanged(state);
                            if (state == ViewPager.SCROLL_STATE_IDLE) {
                                int page = _fragmentPager.getCurrentItem();
                                switch(page) {
                                    case 0:
                                        _colourBackground.setBackgroundColor(Color.argb(120, _colour1[0], _colour1[1], _colour1[2]));
                                        break;
                                    case 1:
                                        _colourBackground.setBackgroundColor(Color.argb(120, _colour2[0], _colour2[1], _colour2[2]));
                                        break;
                                    case 2:
                                        _colourBackground.setBackgroundColor(Color.argb(120, _colour3[0], _colour3[1], _colour3[2]));
                                        break;
                                }
                            }
                        }
                    });

            _fragmentPager.setPageTransformer(true, new FadeBackgroundPageTransformer(this));
            _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager(),
                    fragment,
                    fragment2,
                    fragment3);

            _fragmentPager.setAdapter(_fragmentPagerAdapter);

            setupChrome();

            View colourBackground = findViewById(R.id.colour_background);
            colourBackground.setBackgroundColor(Color.argb(120, 255, 209, 0));
        }
    }

    public int[] getFadeFrom() {
        return _fadeFrom;
    }

    public int[] getFadeToForward() {
        return _fadeToForward;
    }

    public int[] getFadeToBackward() {
        return _fadeToBackward;
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
                            .setIcon(R.drawable.list)
                            .setTabListener(tabListener));
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(R.drawable.medkit_grey)
                            .setTabListener(tabListener));
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(R.drawable.bar_chart_grey)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        //
        // here we need to decide how we are going to launch new activities.
        // I think we need to replace the whole fragment (view pager included), not sure how to do that atm.
        //
        switch (item.getItemId()) {
            case R.id.add_consumption:
                Intent intent = new Intent(this, AddConsumptionActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}