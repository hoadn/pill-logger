package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionGroupEvent;
import uk.co.pilllogger.events.LoadedConsumptionsEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.events.UpdatedStatisticsEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.jobs.LoadConsumptionsJob;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.PillAmount;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.themes.ITheme;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.DayOfWeekView;
import uk.co.pilllogger.views.HourOfDayView;

/**
 * Created by nick on 18/03/14.
 */
public class StatsFragment extends PillLoggerFragmentBase{

    @InjectView(R.id.stats_most_taken_medicine_1st) TextView _medicineMostTaken1st;
    @InjectView(R.id.stats_most_taken_medicine_2nd) TextView _medicineMostTaken2nd;
    @InjectView(R.id.stats_most_taken_medicine_3rd) TextView _medicineMostTaken3rd;
    @InjectView(R.id.stats_most_taken_count_1st) TextView _medicineMostTakenCount1st;
    @InjectView(R.id.stats_most_taken_count_2nd) TextView _medicineMostTakenCount2nd;
    @InjectView(R.id.stats_most_taken_count_3rd) TextView _medicineMostTakenCount3rd;
    @InjectView(R.id.stats_most_taken_indicator_1st) ColourIndicator _medicineMostTakenIndicator1st;
    @InjectView(R.id.stats_most_taken_indicator_2nd) ColourIndicator _medicineMostTakenIndicator2nd;
    @InjectView(R.id.stats_most_taken_indicator_3rd) ColourIndicator _medicineMostTakenIndicator3rd;
    @InjectView(R.id.stats_day_most_consumptions) TextView _dayMostTaken;
    @InjectView(R.id.stats_average_between_consumption) TextView _averageTimeBetween;
    @InjectView(R.id.stats_longest_between_consumption) TextView _longestTimeBetween;
    @InjectView(R.id.stats_most_taken_graph) PieGraph _medicineMostTakenGraph;
    @InjectView(R.id.stats_hour_most_graph) HourOfDayView _hourOfDayView;
    @InjectView(R.id.stats_most_taken_2nd) View _pill2;
    @InjectView(R.id.stats_most_taken_3rd) View _pill3;
    @InjectView(R.id.stats_most_taken_medicine_title) TextView _medicineMostTakenTitle;
    @InjectView(R.id.stats_day_most_consumptions_title) TextView _dayMostTakenTitle;
    @InjectView(R.id.stats_hour_most_consumptions_title) TextView _hourMostTakenTitle;
    @InjectView(R.id.stats_average_between_consumption_title) TextView _averageTimeBetweenTitle;
    @InjectView(R.id.stats_longest_between_consumption_title) TextView _longestTimeBetweenTitle;
    @InjectView(R.id.stats_fragment_title) TextView _statsTitle;
    @InjectView(R.id.stats_day_most_consumptions_view) DayOfWeekView _dayOfWeekView;
    @InjectView(R.id.stats_total_consumption) TextView _totalConsumptions;
    @InjectView(R.id.stats_longest_streak) TextView _longestStreak;
    @InjectView(R.id.stats_current_streak) TextView _currentStreak;
    @InjectView(R.id.stats_no_information) TextView _noInformation;
    @InjectView(R.id.stats_outer) View _statsOuter;

    private Context _context;
    @Inject
    Statistics _statistics;
    @Inject JobManager _jobManager;

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

        ButterKnife.inject(this, v);

        setFont();

        return v;
    }

    @OnClick(R.id.stats_total)
    void onTotalClicked(View total){
        Toast.makeText(_context, getResources().getString(R.string.stats_total_explanation), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.stats_longest_consecutive)
    void onLongestConsecutiveClicked(View longestConsecutive){
        Toast.makeText(_context, getResources().getString(R.string.stats_longest_consecutive_explanation), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.stats_current_consecutive)
    void onCurrentConsecutiveClicked(View currentConsecutive){
        Toast.makeText(_context, getResources().getString(R.string.stats_current_consecutive_explanation), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        _context = getActivity();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Subscribe
    public void statisticsUpdated(UpdatedStatisticsEvent event) {
        if(!isAdded())
            return;

        List<Consumption> consumptions = event.getConsumptions();

        if (consumptions != null && consumptions.size() > 0) {
            setNoInformation(false);
            handleMostTaken(consumptions);
            handleMostTakenHour(consumptions);
            handleMostTakenDay(consumptions);

            Context context = getActivity();
            _averageTimeBetween.setText(_statistics.getAverageTimeBetweenConsumptions(consumptions, context));
            _longestTimeBetween.setText(_statistics.getLongestTimeBetweenConsumptions(consumptions));

            _totalConsumptions.setText(String.valueOf(_statistics.getTotalConsumptions(consumptions)));
            _longestStreak.setText(String.valueOf(_statistics.getLongestStreak(consumptions)));
            _currentStreak.setText(String.valueOf(_statistics.getCurrentStreak(consumptions)));
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
        Map<Integer, Map<Integer, Integer>> hours = _statistics.getDaysWithHourAmounts(consumptions);
        int day = _statistics.getDayWithMostConsumptions(consumptions);

        DateTime dateTime = new DateTime().withHourOfDay(day).withMinuteOfHour(0);
        DateTime nextHour = dateTime.plusHours(1);

        //String hourMostTaken = String.format("%s - %s", DateHelper.getTime(this.getActivity(), dateTime), DateHelper.getTime(this.getActivity(), nextHour));

        _dayOfWeekView.setData(hours, day);
    }

    private void handleMostTakenHour(List<Consumption> consumptions){
        Map<Integer, Integer> hours = _statistics.getHoursWithAmounts(consumptions);
        int hour = _statistics.getHourWithMostConsumptions(consumptions);

        DateTime dateTime = new DateTime().withHourOfDay(hour).withMinuteOfHour(0);
        DateTime nextHour = dateTime.plusHours(1);

        String hourMostTaken = String.format("%s - %s", DateHelper.getTime(this.getActivity(), dateTime), DateHelper.getTime(this.getActivity(), nextHour));

        _hourOfDayView.setSmallMode(false);
        _hourOfDayView.setData(hours, hour, hourMostTaken);
    }

    private void handleMostTaken(List<Consumption> consumptions){
        List<PillAmount> pills = _statistics.getPillsWithAmounts(consumptions);

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

    public static Fragment newInstance(int num) {
        StatsFragment f = new StatsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
}
