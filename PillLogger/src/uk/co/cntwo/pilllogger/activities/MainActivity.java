package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {

    List<Consumption> _consumptions = new ArrayList<Consumption>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        insertData(); //Doing this to test - will not be needed when working fully

        ListView list = (ListView) findViewById(R.id.main_consumption_list);
        list.setAdapter(new ConsumptionListAdapter(this, R.layout.pill_list_item, _consumptions));


    }

    private void insertData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
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