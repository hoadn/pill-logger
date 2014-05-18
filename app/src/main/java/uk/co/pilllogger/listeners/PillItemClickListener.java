package uk.co.pilllogger.listeners;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by nick on 23/10/13.
 */
public class PillItemClickListener implements ListView.OnItemClickListener {

    Activity _activity;

    public PillItemClickListener(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Logger.v("AddConsumptionPillItemClickListener", "testingWidth, this is being called");
        PillRepository dbHelper = PillRepository.getSingleton(_activity);
        List<Pill> pills = dbHelper.getAll();
        onItemSelected(pills.get(i).getId());
    }

    public void onItemSelected(int id) {
//            Bundle arguments = new Bundle();
//            arguments.putInt(PillDetailFragment.ARG_ITEM_ID, id);
//            PillDetailFragment fragment = new PillDetailFragment();
//            fragment.setArguments(arguments);
//            _context.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(fragment.toString())
//                    .commit();
    }


}
