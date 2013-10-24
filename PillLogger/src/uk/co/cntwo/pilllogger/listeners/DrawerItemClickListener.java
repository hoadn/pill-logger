package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.fragments.MainFragment;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;
import uk.co.cntwo.pilllogger.helpers.Logger;

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
        Logger.d(TAG, "selectItem, position: " + position);

        Fragment fragment = null;
        if (_navigationItems.get(position).equals(consumption)) {
            fragment = new MainFragment();
            title = consumption;

        }
        else if (_navigationItems.get(position).equals(pills)) {
            fragment = new PillListFragment();
            title = pills;
        }

        if(fragment != null){
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = ((FragmentActivity)_activity).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragment.toString())
                    .commit();
        }

        _drawerList.setItemChecked(position, true);
        int selectedItem = _drawerList.getCheckedItemPosition();
        Logger.v("DrawerItemClickListener", "selectedItem = " + selectedItem);
        _drawerLayout.closeDrawer(_drawerList);

        _activity.getActionBar().setTitle(title);

    }
}
