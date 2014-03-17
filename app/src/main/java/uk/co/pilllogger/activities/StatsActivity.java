package uk.co.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;

public class StatsActivity extends Activity implements GetPillsTask.ITaskComplete, GetConsumptionsTask.ITaskComplete {

    TextView _medicineMostTaken;
    TextView _dayMostTaken;
    TextView _hourMostTaken;
    TextView _averageTimeBetween;
    TextView _longestTimeBetween;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stats);

        _medicineMostTaken = (TextView) findViewById(R.id.stats_most_taken_medicine);
        _dayMostTaken = (TextView) findViewById(R.id.stats_day_most_consumptions);
        _hourMostTaken = (TextView) findViewById(R.id.stats_hour_most_consumptions);
        _averageTimeBetween = (TextView) findViewById(R.id.stats_average_between_consumption);
        _longestTimeBetween = (TextView) findViewById(R.id.stats_longest_between_consumption);

        new GetPillsTask(this, this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        new GetConsumptionsTask(this, this, false).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if (consumptions != null && consumptions.size() > 0) {
            _medicineMostTaken.setText(Statistics.getMostTakenPill(consumptions).getName());
            _dayMostTaken.setText(Statistics.getDayWithMostConsumptions(consumptions));
            _hourMostTaken.setText(Statistics.getHourWithMostConsumptions(consumptions) + ":00");
            _averageTimeBetween.setText(Statistics.getAverageTimeBetweenConsumptions(consumptions, this));
            _longestTimeBetween.setText(Statistics.getLongestTimeBetweenConsumptions(consumptions, this));
        }
    }
}
