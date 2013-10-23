package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.adapters.LeftDrawerAdapter;
import uk.co.cntwo.pilllogger.fragments.PillDetailFragment;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;
import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.listeners.DrawerItemClickListener;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends FragmentActivity {

    private List<String> _navigationItems = new ArrayList<String>();
    private DrawerLayout _drawerLayout;
    private ListView _drawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        setUpDrawerNavigation();

        mTitle = mDrawerTitle = getTitle();
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

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpDrawerNavigation() {
        _navigationItems.add(this.getResources().getString(R.string.drawer_consumption));
        _navigationItems.add(this.getResources().getString(R.string.drawer_pills));
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerList = (ListView) findViewById(R.id.left_drawer);

        _drawerList.setAdapter(new LeftDrawerAdapter(this, _navigationItems));
        _drawerList.setOnItemClickListener(new DrawerItemClickListener(this, _navigationItems, _drawerLayout, _drawerList));
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