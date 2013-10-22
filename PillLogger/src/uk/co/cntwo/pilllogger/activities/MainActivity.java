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
import uk.co.cntwo.pilllogger.models.Consumption;

/**
 * Created by nick on 22/10/13.
 */
public class MainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Consumption");

        List<Consumption> consumptions = new ArrayList<Consumption>();
        for (int i = 0; i < 15; i ++) {
            Consumption consumption = new Consumption("Paracetamol", new Date());
            consumptions.add(consumption);
        }

        ListView list = (ListView) findViewById(R.id.main_consumption_list);
        list.setAdapter(new ConsumptionListAdapter(this, R.layout.pill_list_item, consumptions));
    }
}