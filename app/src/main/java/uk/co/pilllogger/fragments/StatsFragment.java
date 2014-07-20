package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.PillAmount;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.themes.ITheme;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.DayOfWeekView;
import uk.co.pilllogger.views.HourOfDayView;

/**
 * Created by nick on 18/03/14.
 */
public class StatsFragment extends PillLoggerFragmentBase implements
        GetConsumptionsTask.ITaskComplete,
        Observer.IConsumptionAdded,
        Observer.IConsumptionDeleted,
        Observer.IPillsUpdated{

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
    private DayOfWeekView _dayOfWeekView;
    private TextView _totalConsumptions;
    private TextView _longestStreak;
    private TextView _currentStreak;
    private TextView _noInformation;
    private View _statsOuter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.stats_fragment, container, false);
        if(getActivity() == null || v == null){
            return null;
        }
        int color = getActivity().getResources().getColor(State.getSingleton().getTheme().getStatsBackgroundResourceId());
        v.setTag(R.id.tag_page_colour, color);
        v.setTag(R.id.tag_tab_icon_position, 2);

        _noInformation = (TextView) v.findViewById(R.id.stats_no_information);
        _statsOuter = v.findViewById(R.id.stats_outer);

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
        _dayOfWeekView = (DayOfWeekView) v.findViewById(R.id.stats_day_most_consumptions_view);

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


        _totalConsumptions = (TextView)v.findViewById(R.id.stats_total_consumption);
        _longestStreak = (TextView) v.findViewById(R.id.stats_longest_streak);
        _currentStreak = (TextView) v.findViewById(R.id.stats_current_streak);

        View total = v.findViewById(R.id.stats_total);
        View longestConsecutive = v.findViewById(R.id.stats_longest_consecutive);
        View currentConsecutive = v.findViewById(R.id.stats_current_consecutive);
        final Activity activity = getActivity();
        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, activity.getResources().getString(R.string.stats_total_explanation), Toast.LENGTH_LONG).show();
            }
        });
        longestConsecutive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, activity.getResources().getString(R.string.stats_longest_consecutive_explanation), Toast.LENGTH_LONG).show();
            }
        });
        currentConsecutive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, activity.getResources().getString(R.string.stats_current_consecutive_explanation), Toast.LENGTH_LONG).show();
            }
        });

        setFont();

        Observer.getSingleton().registerPillsUpdatedObserver(this);
        Observer.getSingleton().registerConsumptionAddedObserver(this);
        Observer.getSingleton().registerConsumptionDeletedObserver(this); 

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        Observer.getSingleton().unregisterPillsUpdatedObserver(this);
        Observer.getSingleton().unregisterConsumptionAddedObserver(this);
        Observer.getSingleton().unregisterConsumptionDeletedObserver(this);
    }

    @Subscribe
    public void pillsLoaded(LoadedPillsEvent event) {
        new GetConsumptionsTask(getActivity(), this, true).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if(!isAdded())
            return;

        if (consumptions != null && consumptions.size() > 0) {
            setNoInformation(false);
            handleMostTaken(consumptions);
            handleMostTakenHour(consumptions);
            handleMostTakenDay(consumptions);

            Context context = getActivity();
            _averageTimeBetween.setText(Statistics.getInstance(context).getAverageTimeBetweenConsumptions(consumptions, context));
            _longestTimeBetween.setText(Statistics.getInstance(context).getLongestTimeBetweenConsumptions(consumptions));

            _totalConsumptions.setText(String.valueOf(Statistics.getInstance(context).getTotalConsumptions(consumptions)));
            _longestStreak.setText(String.valueOf(Statistics.getInstance(context).getLongestStreak(consumptions)));
            _currentStreak.setText(String.valueOf(Statistics.getInstance(context).getCurrentStreak(consumptions)));
        }
        else {
            setNoInformation(true);
        }
    }

    private void setNoInformation(boolean noInformation) {
        if (noInformation) {
            _statsOuter.setVisibility(View.GONE);
            _noInformation.setVisibility(View.VISIBLE);
        }
        else {
            _statsOuter.setVisibility(View.VISIBLE);
            _noInformation.setVisibility(View.GONE);
        }
    }

    private void handleMostTakenDay(List<Consumption> consumptions){
        Activity activity = getActivity();
        Map<Integer, Map<Integer, Integer>> hours = Statistics.getInstance(activity).getDaysWithHourAmounts(consumptions);
        int day = Statistics.getInstance(activity).getDayWithMostConsumptions(consumptions);

        DateTime dateTime = new DateTime().withHourOfDay(day).withMinuteOfHour(0);
        DateTime nextHour = dateTime.plusHours(1);

        //String hourMostTaken = String.format("%s - %s", DateHelper.getTime(this.getActivity(), dateTime), DateHelper.getTime(this.getActivity(), nextHour));

        _dayOfWeekView.setData(hours, day);
    }

    private void handleMostTakenHour(List<Consumption> consumptions){
        Map<Integer, Integer> hours = Statistics.getInstance(getActivity()).getHoursWithAmounts(consumptions);
        int hour = Statistics.getInstance(getActivity()).getHourWithMostConsumptions(consumptions);

        DateTime dateTime = new DateTime().withHourOfDay(hour).withMinuteOfHour(0);
        DateTime nextHour = dateTime.plusHours(1);

        String hourMostTaken = String.format("%s - %s", DateHelper.getTime(this.getActivity(), dateTime), DateHelper.getTime(this.getActivity(), nextHour));

        _hourOfDayView.setSmallMode(false);
        _hourOfDayView.setData(hours, hour, hourMostTaken);
    }

    private void handleMostTaken(List<Consumption> consumptions){
        List<PillAmount> pills = Statistics.getInstance(getActivity()).getPillsWithAmounts(consumptions);

        String countFormat = "(%d)";

        _pill2.setVisibility(View.GONE);
        _pill3.setVisibility(View.GONE);

        boolean lighten = State.getSingleton().getTheme().getGraphHighlightMode() == ITheme.GraphHighlightMode.Lighten;

        if(pills.size() > 0){
            PillAmount pa = pills.get(0);
            if(pa.getPill() != null) {
                _medicineMostTakenIndicator1st.setColour(pa.getPill().getColour(), lighten);
                _medicineMostTaken1st.setText(pa.getPill().getName());
                _medicineMostTakenCount1st.setText(String.format(countFormat, pa.getAmount()));
            }
        }
        if(pills.size() > 1){
            PillAmount pa = pills.get(1);
            if(pa.getPill() != null) {
                _medicineMostTakenIndicator2nd.setColour(pa.getPill().getColour(), lighten);
                _medicineMostTaken2nd.setText(pa.getPill().getName());
                _medicineMostTakenCount2nd.setText(String.format(countFormat, pa.getAmount()));
                _pill2.setVisibility(View.VISIBLE);
            }
        }
        if(pills.size() > 2){
            PillAmount pa = pills.get(2);
            if(pa.getPill() != null) {
                _medicineMostTakenIndicator3rd.setColour(pa.getPill().getColour(), lighten);
                _medicineMostTaken3rd.setText(pa.getPill().getName());
                _medicineMostTakenCount3rd.setText(String.format(countFormat, pa.getAmount()));
                _pill3.setVisibility(View.VISIBLE);
            }
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
        _noInformation.setTypeface(State.getSingleton().getTypeface());
    }

    @Override
    public void consumptionAdded(Consumption consumption) {
        new GetConsumptionsTask(getActivity(), this, false).execute();
    }

    @Override
    public void consumptionDeleted(Consumption consumption) {
        new GetConsumptionsTask(getActivity(), this, false).execute();
    }

    @Override
    public void consumptionPillGroupDeleted(String group, int pillId) {
        new GetConsumptionsTask(getActivity(), this, false).execute();
    }

    @Override
    public void pillsUpdated(Pill pill) {
        new GetConsumptionsTask(getActivity(), this, false).execute();
    }
}
