package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.LocalTime;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectTimeFragment extends ExportFragmentBase {

    private TextView _startTimeView;
    private TextView _endTimeView;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;
    private TextView _exportTimeWarning;
    private View _clearStartTime;
    private View _clearEndTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_export_select_time, container, false);

        if(view != null) {
            _startTimeTitle = (TextView) view.findViewById(R.id.export_start_time_title);
            _endTimeTitle = (TextView) view.findViewById(R.id.export_end_time_title);
            _done = (TextView) view.findViewById(R.id.export_pills_done);

            _startTimeView = (TextView) view.findViewById(R.id.export_start_time);
            _endTimeView = (TextView) view.findViewById(R.id.export_end_time);
            _exportTimeWarning = (TextView) view.findViewById(R.id.export_time_warning);
            _clearStartTime = view.findViewById(R.id.export_start_time_clear);
            _clearEndTime = view.findViewById(R.id.export_end_time_clear);

            setTypeface();
            loadTimes();

            final Activity activity = getActivity();

            View startTimeLayout = view.findViewById(R.id.export_start_time_layout);
            View endTimeLayout = view.findViewById(R.id.export_end_time_layout);

            _clearStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _exportService.getExportSettings().setStartTime(null);
                    _startTimeView.setVisibility(View.GONE);
                    _clearStartTime.setVisibility(View.GONE);
                    _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time_select));
                    updateSummaryText(_exportService.getTimeSummary());
                }
            });

            _clearEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _exportService.getExportSettings().setEndTime(null);
                    _endTimeView.setVisibility(View.GONE);
                    _clearEndTime.setVisibility(View.GONE);
                    _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time_select));
                    updateSummaryText(_exportService.getTimeSummary());
                }
            });

            startTimeLayout.setOnClickListener(new View.OnClickListener() { //Start time picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    LocalTime startTime = _exportService.getExportSettings().getStartTime();

                    if(startTime == null) startTime = new LocalTime();

                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 if(validateTimes(lt, _exportService.getExportSettings().getEndTime())) {
                                                     _exportService.getExportSettings().setStartTime(lt);
                                                     updateSummaryText(_exportService.getTimeSummary());
                                                 }
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time));
                                                 _startTimeView.setText(timeString);
                                                 _startTimeView.setVisibility(View.VISIBLE);
                                                 _clearStartTime.setVisibility(View.VISIBLE);
                                             }
                                         }, startTime.getHourOfDay(), startTime.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity)
                            );

                    timePickerDialog.show(fm, "Start time picker");
                }
            });

            endTimeLayout.setOnClickListener(new View.OnClickListener() { //End time picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    LocalTime endTime = _exportService.getExportSettings().getEndTime();
                    if(endTime == null) endTime = new LocalTime();

                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 if(validateTimes(_exportService.getExportSettings().getStartTime(), lt)) {
                                                     _exportService.getExportSettings().setEndTime(lt);
                                                     updateSummaryText(_exportService.getTimeSummary());
                                                 }
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time));
                                                 _endTimeView.setText(timeString);
                                                 _endTimeView.setVisibility(View.VISIBLE);
                                                 _clearEndTime.setVisibility(View.VISIBLE);
                                             }
                                         }, endTime.getHourOfDay(), endTime.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity)
                            );

                    timePickerDialog.show(fm, "End time picker");
                }
            });


            View doneLayout = view.findViewById(R.id.export_pills_list_layout);
            doneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getFragmentManager().popBackStack();
                }
            });
        }

        updateSummaryText(_exportService.getTimeSummary());

        return view;
    }

    private void loadTimes() {
        LocalTime startTime = _exportService.getExportSettings().getStartTime();
        if (startTime != null) {
            String timeString = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getStartTime().toDateTimeToday());
            _startTimeView.setText(timeString);
            _startTimeView.setVisibility(View.VISIBLE);
            _clearStartTime.setVisibility(View.VISIBLE);
            _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time));
        }

        LocalTime endTime = _exportService.getExportSettings().getEndTime();
        if (endTime != null) {
            String timeString = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getEndTime().toDateTimeToday());
            _endTimeView.setText(timeString);
            _endTimeView.setVisibility(View.VISIBLE);
            _clearEndTime.setVisibility(View.VISIBLE);
            _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time));
        }
    }

    private void setTypeface() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _done.setTypeface(typeface);
        _startTimeTitle.setTypeface(typeface);
        _endTimeTitle.setTypeface(typeface);
        _startTimeView.setTypeface(typeface);
        _endTimeView.setTypeface(typeface);
    }

    private boolean validateTimes(LocalTime startTime, LocalTime endTime) {
        Activity activity = getActivity();

        if (activity == null) {
            return true;
        }

        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            _endTimeView.setTextColor(Color.RED);
            _startTimeView.setTextColor(Color.RED);
            _exportTimeWarning.setVisibility(View.VISIBLE);
            _done.setText(activity.getString(R.string.discard));
            return false;
        }
        else {
            _endTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _exportTimeWarning.setVisibility(View.GONE);
            _done.setText(activity.getString(R.string.done_label));
            return true;
        }
    }
}
