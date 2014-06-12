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
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectTimeFragment extends ExportFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private TextView _startTimeView;
    private TextView _endTimeView;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;

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

            setTypeface();
            loadTimes();

            final Activity activity = getActivity();

            View startTimeLayout = view.findViewById(R.id.export_start_time_layout);
            View endTimeLayout = view.findViewById(R.id.export_end_time_layout);

            startTimeLayout.setOnClickListener(new View.OnClickListener() { //Start time picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    LocalTime startTime = _exportService.getExportSettings().getStartTime();

                    if(startTime == null) startTime = new LocalTime();

                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 _exportService.getExportSettings().setStartTime(lt);
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time));
                                                 _startTimeView.setText(timeString);
                                                 _startTimeView.setVisibility(View.VISIBLE);
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
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 _exportService.getExportSettings().setEndTime(lt);
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time));
                                                 _endTimeView.setText(timeString);
                                                 _endTimeView.setVisibility(View.VISIBLE);
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

        return view;
    }

    private void loadTimes() {
        LocalTime startTime = _exportService.getExportSettings().getStartTime();
        if (startTime != null) {
            String timeString = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getStartTime().toDateTimeToday());
            _startTimeView.setText(timeString);
            _startTimeView.setVisibility(View.VISIBLE);
            _startTimeTitle.setText(getActivity().getResources().getString(R.string.export_start_time));
        }

        LocalTime endTime = _exportService.getExportSettings().getEndTime();
        if (endTime != null) {
            String timeString = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getEndTime().toDateTimeToday());
            _endTimeView.setText(timeString);
            _endTimeView.setVisibility(View.VISIBLE);
            _endTimeTitle.setText(getActivity().getResources().getString(R.string.export_end_time));
        }

        if (startTime != null && endTime != null) {
            validateTimes(startTime, endTime);
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

    private void validateTimes(LocalTime startTime, LocalTime endTime) {
        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        if (endTime.isBefore(startTime)) {
            _endTimeView.setTextColor(Color.RED);
            _startTimeView.setTextColor(Color.RED);
            Toast.makeText(activity, "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show();
        }
        else {
            _endTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
        }
    }

}
