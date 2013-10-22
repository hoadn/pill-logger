package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Consumption;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        Consumption consumption = new Consumption("Paracetamol", new Date());
        Consumption consumption2 = new Consumption("Ibuprofen", new Date());

        List<Consumption> consumptions = new ArrayList<Consumption>();
        consumptions.add(consumption);
        consumptions.add(consumption2);

        ListView list = (ListView) findViewById(R.id.main_consumption_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line);
        list.setAdapter(adapter);

        adapter.add(consumption.get_pillName());
        adapter.add(consumption2.get_pillName());
    }
}