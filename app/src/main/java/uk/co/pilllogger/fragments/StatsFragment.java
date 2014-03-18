package uk.co.pilllogger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.stats.PillAmount;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 18/03/14.
 */
public class StatsFragment extends PillLoggerFragmentBase implements GetPillsTask.ITaskComplete, GetConsumptionsTask.ITaskComplete {

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
    View _pill2;
    View _pill3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.stats_fragment, container, false);

        _medicineMostTakenIndicator1st = (ColourIndicator) v.findViewById(R.id.stats_most_taken_indicator_1st);
        _medicineMostTakenIndicator2nd = (ColourIndicator) v.findViewById(R.id.stats_most_taken_indicator_2nd);
        _medicineMostTakenIndicator3rd = (ColourIndicator) v.findViewById(R.id.stats_most_taken_indicator_3rd);

        _medicineMostTaken1st = (TextView) v.findViewById(R.id.stats_most_taken_medicine_1st);
        _medicineMostTaken2nd = (TextView) v.findViewById(R.id.stats_most_taken_medicine_2nd);
        _medicineMostTaken3rd = (TextView) v.findViewById(R.id.stats_most_taken_medicine_3rd);

        _medicineMostTakenCount1st = (TextView) v.findViewById(R.id.stats_most_taken_count_1st);
        _medicineMostTakenCount2nd = (TextView) v.findViewById(R.id.stats_most_taken_count_2nd);
        _medicineMostTakenCount3rd = (TextView) v.findViewById(R.id.stats_most_taken_count_3rd);

        _medicineMostTakenGraph = (PieGraph) v.findViewById(R.id.stats_most_taken_graph);

        _dayMostTaken = (TextView) v.findViewById(R.id.stats_day_most_consumptions);
        _hourMostTaken = (TextView) v.findViewById(R.id.stats_hour_most_consumptions);
        _averageTimeBetween = (TextView) v.findViewById(R.id.stats_average_between_consumption);
        _longestTimeBetween = (TextView) v.findViewById(R.id.stats_longest_between_consumption);

        _pill2 = v.findViewById(R.id.stats_most_taken_2nd);
        _pill3 = v.findViewById(R.id.stats_most_taken_3rd);

        new GetPillsTask(getActivity(), this).execute();
        return v;
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        new GetConsumptionsTask(getActivity(), this, false).execute();

        if (pills.size() == 1) {
            _pill2.setVisibility(View.GONE);
            _pill3.setVisibility(View.GONE);
        }
        else if (pills.size() == 2) {
            _pill3.setVisibility(View.GONE);
        }
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if (consumptions != null && consumptions.size() > 0) {
            handleMostTaken(consumptions);
            _dayMostTaken.setText(Statistics.getDayWithMostConsumptions(consumptions));
            _hourMostTaken.setText(Statistics.getHourWithMostConsumptions(consumptions) + ":00");
            _averageTimeBetween.setText(Statistics.getAverageTimeBetweenConsumptions(consumptions, getActivity()));
            _longestTimeBetween.setText(Statistics.getLongestTimeBetweenConsumptions(consumptions, getActivity()));
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
