package uk.co.pilllogger.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.SlidePagerAdapter;
import uk.co.pilllogger.fragments.MainFragment;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {

    private ViewPager _fragmentPager;
    private PagerAdapter _fragmentPagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");


        if(savedInstanceState == null){
            Fragment fragment = new MainFragment();
            Fragment fragment2 = new MainFragment();
            Fragment fragment3 = new MainFragment();

            _fragmentPager = (ViewPager)findViewById(R.id.fragment_pager);

            _fragmentPager.setOnPageChangeListener(
                    new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            // When swiping between pages, select the
                            // corresponding tab.
                            getActionBar().setSelectedNavigationItem(position);
                        }
                    });


            _fragmentPagerAdapter = new SlidePagerAdapter(getFragmentManager(),
                    fragment,
                    fragment2,
                    fragment3);

            _fragmentPager.setAdapter(_fragmentPagerAdapter);

            setupChrome();
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

            // Add 3 tabs, specifying the tab's text and TabListener
            for (int i = 0; i < 3; i++) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setText("Tab " + (i + 1))
                                .setTabListener(tabListener));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
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
        return super.onOptionsItemSelected(item);
    }
}