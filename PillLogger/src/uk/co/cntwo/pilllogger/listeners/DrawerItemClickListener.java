package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.activities.MainActivity;
import uk.co.cntwo.pilllogger.activities.PillListActivity;
import uk.co.cntwo.pilllogger.fragments.PillListFragment;

/**
 * Created by nick on 23/10/13.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    Activity _activity;
    List<String> _nacvigationItems;

    public DrawerItemClickListener(Activity activity, List<String> nacvigationItems) {
        _activity = activity;
        _nacvigationItems = nacvigationItems;
    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {
        if (_nacvigationItems.get(position).equals(_activity.getResources().getString(R.string.drawer_home))) {
            Intent intent = new Intent(_activity, MainActivity.class);
            _activity.startActivity(intent);
        }
        else if (_nacvigationItems.get(position).equals(_activity.getResources().getString(R.string.drawer_pills))) {
            ListFragment fragment = new PillListFragment();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = ((FragmentActivity)_activity).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_activity_top_layout, fragment)
                    .commit();
        }

    }
}
