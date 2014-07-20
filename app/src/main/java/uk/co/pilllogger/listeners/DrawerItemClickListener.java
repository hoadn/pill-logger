package uk.co.pilllogger.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.fragments.GraphFragment;
import uk.co.pilllogger.fragments.PillListFragment;
import uk.co.pilllogger.fragments.SettingsFragment;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by nick on 23/10/13.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {
    String TAG = "DrawerItemClickListener";
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
        String settings = _activity.getResources().getString(R.string.drawer_settings);
        String charts = _activity.getString(R.string.drawer_charts);
        Logger.d(TAG, "selectItem, position: " + position);

        Fragment fragment = null;
        String selectedItem = _navigationItems.get(position);

        if (selectedItem.equals(consumption)) {
            fragment = new ConsumptionListFragment();
            title = consumption;

        }
        else if (selectedItem.equals(pills)) {
            fragment = new PillListFragment();
            title = pills;
        }
        else if(selectedItem.equals(settings)){
            fragment = new SettingsFragment();
            title = settings;
        }

        if(selectedItem.equals(charts)){
            fragment = new GraphFragment();
            title = charts;
        }

        if(fragment != null){
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = _activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragment.toString())
                    .commit();
        }

        _drawerList.setItemChecked(position, true);
        int selectedPos = _drawerList.getCheckedItemPosition();
        Logger.v("DrawerItemClickListener", "selectedPos = " + selectedPos);
        _drawerLayout.closeDrawer(_drawerList);

        _activity.getActionBar().setTitle(title);

    }
}
