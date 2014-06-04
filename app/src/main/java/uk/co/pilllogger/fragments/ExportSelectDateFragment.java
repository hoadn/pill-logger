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
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectDateFragment extends PillLoggerFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private String TIME_FORMAT = "kk:mm";
    private TextView _startDateView, _startTimeView, _endDateView, _endTimeView;
    private TextView _startDateTitle;
    private TextView _endDateTitle;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;
    private boolean _endDateSet = false;
    private boolean _startDateSet = false;
    private MutableDateTime _startDate;
    private MutableDateTime _endDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_select_date, container, false);

        if(view != null) {
            _startDateTitle = (TextView) view.findViewById(R.id.export_start_date_title);
            _endDateTitle = (TextView) view.findViewById(R.id.export_end_date_title);
            _startTimeTitle = (TextView) view.findViewById(R.id.export_start_time_title);
            _endTimeTitle = (TextView) view.findViewById(R.id.export_end_time_title);
            _done = (TextView) view.findViewById(R.id.export_pills_done);
            _startDateView = (TextView) view.findViewById(R.id.export_start_date);
            _startTimeView = (TextView) view.findViewById(R.id.export_start_time);
            _endDateView = (TextView) view.findViewById(R.id.export_end_date);
            _endTimeView = (TextView) view.findViewById(R.id.export_end_time);

            setTypeface();
            loadDates();

            final Activity activity = getActivity();

            View startDateLayout = view.findViewById(R.id.export_start_date_layout);
            View startTimeLayout = view.findViewById(R.id.export_start_time_layout);
            View endDateLayout = view.findViewById(R.id.export_end_date_layout);
            View endTimeLayout = view.findViewById(R.id.export_end_time_layout);

            if (_startDate == null)
                _startDate = new MutableDateTime();
            if (_endDate == null)
                _endDate = new MutableDateTime();


            startDateLayout.setOnClickListener(new View.OnClickListener() { //Start date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 _startDate.setYear(year);
                                                 _startDate.setMonthOfYear(monthOfYear + 1);
                                                 _startDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, _startDate.toDate().getTime()).toString();
                                                 _startDateSet = true;
                                                 validateDates(_startDate, _endDate);
                                                 _startDateView.setText(dateString);
                                                 _startDateView.setVisibility(View.VISIBLE);
                                             }
                                         }, _startDate.getYear(), (_startDate.getMonthOfYear() - 1),
                                    _startDate.getDayOfMonth()
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
                                                 Date dt = new DateTime(_startDate)
                                                         .withHourOfDay(hourOfDay + 1)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 _startDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, _startDate.toDate().getTime()).toString();
                                                 validateDates(_startDate, _endDate);
                                                 _startTimeView.setText(dateString);
                                                 _startTimeView.setVisibility(View.VISIBLE);
                                             }
                                         }, _startDate.getHourOfDay(), _startDate.getMinuteOfHour(),
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
                                                 _endDate.setYear(year);
                                                 _endDate.setMonthOfYear(monthOfYear + 1);
                                                 _endDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, _endDate.toDate().getTime()).toString();
                                                 _endDateSet = true;
                                                 validateDates(_startDate, _endDate);
                                                 _endDateView.setText(dateString);
                                                 _endDateView.setVisibility(View.VISIBLE);
                                             }
                                         }, _endDate.getYear(), (_endDate.getMonthOfYear() - 1),
                                    _endDate.getDayOfMonth()
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
                                                 Date dt = new DateTime(_endDate)
                                                         .withHourOfDay(hourOfDay + 1)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();

                                                 _endDate.setTime(dt.getTime());
                                                 String dateString = DateFormat.format(TIME_FORMAT, _endDate.toDate().getTime()).toString();
                                                 validateDates(_startDate, _endDate);
                                                 _endTimeView.setText(dateString);
                                                 _endTimeView.setVisibility(View.VISIBLE);
                                             }
                                         }, _endDate.getHourOfDay(), _endDate.getMinuteOfHour(),
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

    private void loadDates() {
        MutableDateTime startDate = ((ExportActivity) getActivity()).getStartDate();
        MutableDateTime endDate = ((ExportActivity) getActivity()).getEndDate();
        if (startDate != null) {
            _startDate = startDate;
            String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
            String dateStringTime = DateFormat.format(TIME_FORMAT, startDate.toDate().getTime()).toString();
            _startDateView.setText(dateString);
            _startDateView.setVisibility(View.VISIBLE);
            _startTimeView.setText(dateStringTime);
            _startTimeView.setVisibility(View.VISIBLE);
            _startDateSet = true;
        }
        if (endDate != null) {
            _endDate = endDate;
            String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
            String dateStringTime = DateFormat.format(TIME_FORMAT, endDate.toDate().getTime()).toString();
            _endDateView.setText(dateString);
            _endDateView.setVisibility(View.VISIBLE);
            _endTimeView.setText(dateStringTime);
            _endTimeView.setVisibility(View.VISIBLE);
            _endDateSet = true;
        }
        if (startDate != null && endDate != null) {
            validateDates(startDate, endDate);
        }
    }

    public MutableDateTime getStartDate() {
        if (!_startDateSet)
            return null;
        return _startDate;
    }

    public MutableDateTime getEndDate() {
        if (!_endDateSet)
            return null;
        return _endDate;
    }

    private void setTypeface() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _done.setTypeface(typeface);
        _startDateTitle.setTypeface(typeface);
        _endDateTitle.setTypeface(typeface);
        _startTimeTitle.setTypeface(typeface);
        _endTimeTitle.setTypeface(typeface);
        _startDateView.setTypeface(typeface);
        _startTimeView.setTypeface(typeface);
        _endDateView.setTypeface(typeface);
        _endTimeView.setTypeface(typeface);
    }

    private void validateDates(MutableDateTime startDate, MutableDateTime endDate) {
        if (endDate.getMillis() < startDate.getMillis() && _endDateSet == true) {
            _endTimeView.setTextColor(Color.RED);
            _endDateView.setTextColor(Color.RED);
            Toast.makeText(getActivity(), "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
        }
        else {
            _endTimeView.setTextColor(getActivity().getResources().getColor(R.color.text_grey));
            _endDateView.setTextColor(getActivity().getResources().getColor(R.color.text_grey));
        }
    }

}
