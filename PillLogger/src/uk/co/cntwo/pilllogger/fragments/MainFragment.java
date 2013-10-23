package uk.co.cntwo.pilllogger.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 23/10/13.
 */
public class MainFragment extends Fragment {

    List<Consumption> _consumptions = new ArrayList<Consumption>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        insertData(); //Doing this to test - will not be needed when working fully

        ListView list = (ListView) v.findViewById(R.id.main_consumption_list);
        list.setAdapter(new ConsumptionListAdapter(getActivity(), R.layout.pill_list_item, _consumptions));

        return v;
    }

    private void insertData() {
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(getActivity());
        List<Pill> pills = dbHelper.getAllPills();

        if (pills.size() == 0) { //This will insert 2 pills as test data if your database doens't have any in
            Pill pill1 = new Pill("Paracetamol", 400);
            Pill pill2 = new Pill("Ibuprofen", 200);
            dbHelper.insertPill(pill1);
            dbHelper.insertPill(pill2);
        }

        List<Consumption> consumptions = dbHelper.getAllConsumptions();

        if (consumptions.size() == 0) { //This will insert some consumptions as test data if your data doesn't have any in
            for (int i = 0; i < 10; i ++) { //I am only doing this to see what a list of consumption would look like
                Pill pill = dbHelper.getPill(1);
                Consumption consumption = new Consumption(pill, new Date());
                dbHelper.insertConsumption(consumption);
            }
        }

        _consumptions = dbHelper.getAllConsumptions();
    }

}
