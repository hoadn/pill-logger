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

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectDateFragment extends PillLoggerFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private String TIME_FORMAT = "kk:mm";
    private TextView _startDate, _startTime, _endDate, _endTime;
    private TextView _startDateTitle;
    private TextView _endDateTitle;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;
    private boolean _endDateSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_select_date, container, false);

        if(view != null) {
            _startDateTitle = (TextView) view.findViewById(R.id.export_start_date_title);
            _endDateTitle = (TextView) view.findViewById(R.id.export_end_date_title);
            _startTimeTitle = (TextView) view.findViewById(R.id.export_start_time_title);
            _endTimeTitle = (TextView) view.findViewById(R.id.export_end_time_title);
            _done = (TextView) view.findViewById(R.id.export_pills_done);
            _startDate = (TextView) view.findViewById(R.id.export_start_date);
            _startTime = (TextView) view.findViewById(R.id.export_start_time);
            _endDate = (TextView) view.findViewById(R.id.export_end_date);
            _endTime = (TextView) view.findViewById(R.id.export_end_time);

            setTypeface();

            final Activity activity = getActivity();

            View startDateLayout = view.findViewById(R.id.export_start_date_layout);
            View startTimeLayout = view.findViewById(R.id.export_start_time_layout);
            View endDateLayout = view.findViewById(R.id.export_end_date_layout);
            View endTimeLayout = view.findViewById(R.id.export_end_time_layout);

            final MutableDateTime startDate = new MutableDateTime();
            final MutableDateTime endDate = new MutableDateTime();


            startDateLayout.setOnClickListener(new View.OnClickListener() { //Start date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 startDate.setYear(year);
                                                 startDate.setMonthOfYear(monthOfYear + 1);
                                                 startDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
                                                 validateDates(startDate, endDate);
                                                 _startDate.setText(dateString);
                                                 _startDate.setVisibility(View.VISIBLE);
                                             }
                                         }, startDate.getYear(), (startDate.getMonthOfYear() - 1),
                                    startDate.getDayOfMonth()
                            );
                    calendarDatePickerDialog.show(fm, "Start Date Picker");
                }
            });

            startTimeLayout.setOnClickListener(new View.OnClickListener() { //Start time picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 Date dt = new DateTime(startDate)
                                                         .withHourOfDay(hourOfDay + 1)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 startDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, startDate.toDate().getTime()).toString();
                                                 validateDates(startDate, endDate);
                                                 _startTime.setText(dateString);
                                                 _startTime.setVisibility(View.VISIBLE);
                                             }
                                         }, startDate.getHourOfDay(), startDate.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity)
                            );

                    timePickerDialog.show(fm, "Start time picker");
                }
            });

            endDateLayout.setOnClickListener(new View.OnClickListener() { //End date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 endDate.setYear(year);
                                                 endDate.setMonthOfYear(monthOfYear + 1);
                                                 endDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
                                                 _endDateSet = true;
                                                 validateDates(startDate, endDate);
                                                 _endDate.setText(dateString);
                                                 _endDate.setVisibility(View.VISIBLE);
                                             }
                                         }, endDate.getYear(), (endDate.getMonthOfYear() - 1),
                                    endDate.getDayOfMonth()
                            );
                    calendarDatePickerDialog.show(fm, "Start Date Picker");
                }
            });


            endTimeLayout.setOnClickListener(new View.OnClickListener() { //End time picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 Date dt = new DateTime(endDate)
                                                         .withHourOfDay(hourOfDay + 1)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 endDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, endDate.toDate().getTime()).toString();
                                                 validateDates(startDate, endDate);
                                                 _endTime.setText(dateString);
                                                 _endTime.setVisibility(View.VISIBLE);
                                             }
                                         }, endDate.getHourOfDay(), endDate.getMinuteOfHour(),
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

    private void setTypeface() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _done.setTypeface(typeface);
        _startDateTitle.setTypeface(typeface);
        _endDateTitle.setTypeface(typeface);
        _startTimeTitle.setTypeface(typeface);
        _endTimeTitle.setTypeface(typeface);
        _startDate.setTypeface(typeface);
        _startTime.setTypeface(typeface);
        _endDate.setTypeface(typeface);
        _endTime.setTypeface(typeface);
    }

    private void validateDates(MutableDateTime startDate, MutableDateTime endDate) {
        if (endDate.getMillis() < startDate.getMillis() && _endDateSet == true) {
            _endTime.setTextColor(Color.RED);
            _endDate.setTextColor(Color.RED);
            Toast.makeText(getActivity(), "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
        }
        else {
            _endTime.setTextColor(getActivity().getResources().getColor(R.color.text_grey));
            _endDate.setTextColor(getActivity().getResources().getColor(R.color.text_grey));
        }
    }

}
