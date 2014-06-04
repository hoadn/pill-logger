package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    TextView _startDateTitle;
    TextView _endDateTitle;
    TextView _startTimeTitle;
    TextView _endTimeTitle;
    TextView _done;

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

            final MutableDateTime finalDate = new MutableDateTime();


            startDateLayout.setOnClickListener(new View.OnClickListener() { //Start date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 finalDate.setYear(year);
                                                 finalDate.setMonthOfYear(monthOfYear + 1);
                                                 finalDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
                                                 _startDate.setText(dateString);
                                                 _startDate.setVisibility(View.VISIBLE);
                                             }
                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                    finalDate.getDayOfMonth());
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
                                                 Date dt = new DateTime(finalDate)
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 finalDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, finalDate.toDate().getTime()).toString();
                                                 _startTime.setText(dateString);
                                                 _startTime.setVisibility(View.VISIBLE);
                                             }
                                         }, finalDate.getHourOfDay(), finalDate.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity));

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
                                                 finalDate.setYear(year);
                                                 finalDate.setMonthOfYear(monthOfYear + 1);
                                                 finalDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
                                             }
                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                    finalDate.getDayOfMonth());
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
                                                 Date dt = new DateTime(finalDate)
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 finalDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, finalDate.toDate().getTime()).toString();
                                                 _endTime.setText(dateString);
                                                 _endTime.setVisibility(View.VISIBLE);
                                             }
                                         }, finalDate.getHourOfDay(), finalDate.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity));

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


}
