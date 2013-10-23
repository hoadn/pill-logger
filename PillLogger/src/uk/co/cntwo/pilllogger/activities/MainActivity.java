package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.adapters.LeftDrawerAdapter;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        setUpDrawerNavigation();

    }

    private void setUpDrawerNavigation() {
        _navigationItems.add("Home");
        _navigationItems.add("Pills");
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerList = (ListView) findViewById(R.id.left_drawer);

        _drawerList.setAdapter(new LeftDrawerAdapter(this, _navigationItems));
        _drawerList.setOnItemClickListener(new DrawerItemClickListener(this, _navigationItems));
    }


}