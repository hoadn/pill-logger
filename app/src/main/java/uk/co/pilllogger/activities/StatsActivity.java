package uk.co.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.stats.PillAmount;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.views.ColourIndicator;

public class StatsActivity extends Activity implements GetPillsTask.ITaskComplete, GetConsumptionsTask.ITaskComplete {

    TextView _medicineMostTaken1st;
    TextView _medicineMostTaken2nd;
    TextView _medicineMostTaken3rd;
    TextView _medicineMostTakenCount1st;
    TextView _medicineMostTakenCount2nd;
    TextView _medicineMostTakenCount3rd;
    TextView _dayMostTaken;
    TextView _hourMostTaken;
    TextView _averageTimeBetween;
    TextView _longestTimeBetween;
    private ColourIndicator _medicineMostTakenIndicator1st;
    private ColourIndicator _medicineMostTakenIndicator2nd;
    private ColourIndicator _medicineMostTakenIndicator3rd;
    private PieGraph _medicineMostTakenGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stats);


        _medicineMostTakenIndicator1st = (ColourIndicator) findViewById(R.id.stats_most_taken_indicator_1st);
        _medicineMostTakenIndicator2nd = (ColourIndicator) findViewById(R.id.stats_most_taken_indicator_2nd);
        _medicineMostTakenIndicator3rd = (ColourIndicator) findViewById(R.id.stats_most_taken_indicator_3rd);

        _medicineMostTaken1st = (TextView) findViewById(R.id.stats_most_taken_medicine_1st);
        _medicineMostTaken2nd = (TextView) findViewById(R.id.stats_most_taken_medicine_2nd);
        _medicineMostTaken3rd = (TextView) findViewById(R.id.stats_most_taken_medicine_3rd);

        _medicineMostTakenCount1st = (TextView) findViewById(R.id.stats_most_taken_count_1st);
        _medicineMostTakenCount2nd = (TextView) findViewById(R.id.stats_most_taken_count_2nd);
        _medicineMostTakenCount3rd = (TextView) findViewById(R.id.stats_most_taken_count_3rd);

        _medicineMostTakenGraph = (PieGraph) findViewById(R.id.stats_most_taken_graph);

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
        View pill2 = findViewById(R.id.stats_most_taken_2nd);
        View pill3 = findViewById(R.id.stats_most_taken_3rd);
        if (pills.size() == 1) {
            pill2.setVisibility(View.GONE);
            pill3.setVisibility(View.GONE);
        }
        else if (pills.size() == 2) {
            pill3.setVisibility(View.GONE);
        }

    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if (consumptions != null && consumptions.size() > 0) {
            handleMostTaken(consumptions);
            _dayMostTaken.setText(Statistics.getDayWithMostConsumptions(consumptions));
            _hourMostTaken.setText(Statistics.getHourWithMostConsumptions(consumptions) + ":00");
            _averageTimeBetween.setText(Statistics.getAverageTimeBetweenConsumptions(consumptions, this));
            _longestTimeBetween.setText(Statistics.getLongestTimeBetweenConsumptions(consumptions, this));
        }
    }

    private void handleMostTaken(List<Consumption> consumptions){
        List<PillAmount> pills = Statistics.getPillsWithAmounts(consumptions);

        String countFormat = "(%d)";

        if(pills.size() > 0){
            PillAmount pa = pills.get(0);
            _medicineMostTakenIndicator1st.setColour(pa.getPill().getColour());
            _medicineMostTaken1st.setText(pa.getPill().getName());
            _medicineMostTakenCount1st.setText(String.format(countFormat, pa.getAmount()));
        }
        if(pills.size() > 1){
            PillAmount pa = pills.get(1);
            _medicineMostTakenIndicator2nd.setColour(pa.getPill().getColour());
            _medicineMostTaken2nd.setText(pa.getPill().getName());
            _medicineMostTakenCount2nd.setText(String.format(countFormat, pa.getAmount()));
        }
        if(pills.size() > 2){
            PillAmount pa = pills.get(2);
            _medicineMostTakenIndicator3rd.setColour(pa.getPill().getColour());
            _medicineMostTaken3rd.setText(pa.getPill().getName());
            _medicineMostTakenCount3rd.setText(String.format(countFormat, pa.getAmount()));
        }

        GraphHelper.plotPieChart(pills, _medicineMostTakenGraph);
    }
}
