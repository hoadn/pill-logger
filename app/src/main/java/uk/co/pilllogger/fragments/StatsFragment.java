package uk.co.pilllogger.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.PillAmount;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.HourOfDayView;

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
    TextView _averageTimeBetween;
    TextView _longestTimeBetween;
    private ColourIndicator _medicineMostTakenIndicator1st;
    private ColourIndicator _medicineMostTakenIndicator2nd;
    private ColourIndicator _medicineMostTakenIndicator3rd;
    private PieGraph _medicineMostTakenGraph;
    private HourOfDayView _hourOfDayView;
    View _pill2;
    View _pill3;
    TextView _medicineMostTakenTitle;
    TextView _dayMostTakenTitle;
    TextView _hourMostTakenTitle;
    TextView _averageTimeBetweenTitle;
    TextView _longestTimeBetweenTitle;
    TextView _statsTitle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.stats_fragment, container, false);
        v.setTag(R.id.tag_page_colour, Color.argb(120, 81, 81, 81));
        v.setTag(R.id.tag_tab_icon_position, 2);

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
        _hourOfDayView = (HourOfDayView) v.findViewById(R.id.stats_hour_most_graph);

        _dayMostTaken = (TextView) v.findViewById(R.id.stats_day_most_consumptions);
        _averageTimeBetween = (TextView) v.findViewById(R.id.stats_average_between_consumption);
        _longestTimeBetween = (TextView) v.findViewById(R.id.stats_longest_between_consumption);
        _pill2 = v.findViewById(R.id.stats_most_taken_2nd);
        _pill3 = v.findViewById(R.id.stats_most_taken_3rd);

        _medicineMostTakenTitle = (TextView) v.findViewById(R.id.stats_most_taken_medicine_title);
        _dayMostTakenTitle = (TextView) v.findViewById(R.id.stats_day_most_consumptions_title);
        _hourMostTakenTitle = (TextView) v.findViewById(R.id.stats_hour_most_consumptions_title);
        _averageTimeBetweenTitle = (TextView) v.findViewById(R.id.stats_average_between_consumption_title);
        _longestTimeBetweenTitle = (TextView) v.findViewById(R.id.stats_longest_between_consumption_title);
        _statsTitle = (TextView) v.findViewById(R.id.stats_fragment_title);

        new GetPillsTask(getActivity(), this).execute();
        setFont();
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
            handleMostTakenHour(consumptions);
            _dayMostTaken.setText(Statistics.getDayWithMostConsumptions(this.getActivity(), consumptions));
            _averageTimeBetween.setText(Statistics.getAverageTimeBetweenConsumptions(consumptions, getActivity()));
            _longestTimeBetween.setText(Statistics.getLongestTimeBetweenConsumptions(consumptions, getActivity()));
        }
    }

    private void handleMostTakenHour(List<Consumption> consumptions){
        Map<Integer, Integer> hours = Statistics.getHoursWithAmounts(consumptions);
        int hour = Statistics.getHourWithMostConsumptions(consumptions);

        DateTime dateTime = new DateTime().withHourOfDay(hour).withMinuteOfHour(0);
        DateTime nextHour = dateTime.plusHours(1);

        String hourMostTaken = String.format("%s - %s", DateHelper.getTime(this.getActivity(), dateTime), DateHelper.getTime(this.getActivity(), nextHour));

        _hourOfDayView.setData(hours, hour, hourMostTaken);
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

    private void setFont() {
        _medicineMostTaken1st.setTypeface(State.getSingleton().getTypeface());
        _medicineMostTaken2nd.setTypeface(State.getSingleton().getTypeface());
        _medicineMostTaken3rd.setTypeface(State.getSingleton().getTypeface());
        _dayMostTaken.setTypeface(State.getSingleton().getTypeface());
        _averageTimeBetween.setTypeface(State.getSingleton().getTypeface());
        _longestTimeBetween.setTypeface(State.getSingleton().getTypeface());
        _medicineMostTakenTitle.setTypeface(State.getSingleton().getTypeface());
        _dayMostTakenTitle.setTypeface(State.getSingleton().getTypeface());
        _hourMostTakenTitle.setTypeface(State.getSingleton().getTypeface());
        _averageTimeBetweenTitle.setTypeface(State.getSingleton().getTypeface());
        _longestTimeBetweenTitle.setTypeface(State.getSingleton().getTypeface());
        _statsTitle.setTypeface(State.getSingleton().getTypeface());
    }
}
