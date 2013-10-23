package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.activities.MainActivity;
import uk.co.cntwo.pilllogger.activities.PillListActivity;
import uk.co.cntwo.pilllogger.fragments.MainFragment;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;

/**
 * Created by nick on 23/10/13.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    Activity _activity;
    List<String> _navigationItems;
    private DrawerLayout _drawerLayout;
    private ListView _drawerList;

    public DrawerItemClickListener(Activity activity, List<String> navigationItems, DrawerLayout drawerLayout, ListView drawerList) {
        _activity = activity;
        _navigationItems = navigationItems;
        _drawerLayout = drawerLayout;
        _drawerList = drawerList;

    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {
        String title = _activity.getResources().getString(R.string.app_name);
        String consumption = _activity.getResources().getString(R.string.drawer_consumption);
        String pills = _activity.getResources().getString(R.string.drawer_pills);
        if (_navigationItems.get(position).equals(consumption)) {
            Fragment fragment = new MainFragment();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = ((FragmentActivity)_activity).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();
            title = consumption;

        }
        else if (_navigationItems.get(position).equals(pills)) {
            Fragment fragment = new PillListFragment();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = ((FragmentActivity)_activity).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .commit();
            title = pills;
        }

        _drawerList.setItemChecked(position, true);
        _drawerLayout.closeDrawer(_drawerList);

        _activity.getActionBar().setTitle(title);

    }
}
