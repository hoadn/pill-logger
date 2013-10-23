package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.activities.PillDetailActivity;
import uk.co.cntwo.pilllogger.fragments.PillDetailFragment;
import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 23/10/13.
 */
public class PillItemClickListener implements ListView.OnItemClickListener {

    FragmentActivity _activity;

    public PillItemClickListener(FragmentActivity activity) {
        _activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
        List<Pill> pills = dbHelper.getAllPills();
        onItemSelected(pills.get(i).getId());
    }

    public void onItemSelected(int id) {
            Bundle arguments = new Bundle();
            arguments.putInt(PillDetailFragment.ARG_ITEM_ID, id);
            PillDetailFragment fragment = new PillDetailFragment();
            fragment.setArguments(arguments);
            _activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();

    }


}
