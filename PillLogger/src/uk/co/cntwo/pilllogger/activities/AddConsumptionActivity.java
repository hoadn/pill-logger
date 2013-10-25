package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.cntwo.pilllogger.adapters.PillsListAdapter;
import uk.co.cntwo.pilllogger.animations.AddPillToConsumptionAnimation;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.cntwo.pilllogger.listeners.PillItemClickListener;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.GetPillsTask;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionActivity extends Activity implements GetPillsTask.ITaskComplete {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_consumption_activity);

        new GetPillsTask(this, this).execute();
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        ListView pillsList = (ListView)findViewById(R.id.add_consumption_pill_list);
        pillsList.setAdapter(new AddConsumptionPillListAdapter(this, R.layout.add_consumption_pill_list, pills));
        pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this));
    }

    public void cancel(View view) {
        finish();
    }
}