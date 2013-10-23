package uk.co.cntwo.pilllogger.activities;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.LeftDrawerAdapter;
import uk.co.cntwo.pilllogger.fragments.MainFragment;
import uk.co.cntwo.pilllogger.listeners.DrawerItemClickListener;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends FragmentActivity {

    private List<String> _navigationItems = new ArrayList<String>();
    private DrawerLayout _drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        setUpDrawerNavigation();

        CharSequence drawerTitle;
        CharSequence title = drawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, _drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        _drawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar bar = getActionBar();
        if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            Fragment fragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void setUpDrawerNavigation() {
        _navigationItems.add(this.getResources().getString(R.string.drawer_consumption));
        _navigationItems.add(this.getResources().getString(R.string.drawer_pills));
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new LeftDrawerAdapter(this, _navigationItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener(this, _navigationItems, _drawerLayout, drawerList));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }




}