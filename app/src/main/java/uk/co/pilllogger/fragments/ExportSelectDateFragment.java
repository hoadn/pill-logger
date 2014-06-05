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
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 04/06/14.
 */
public class ExportSelectDateFragment extends ExportFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private String TIME_FORMAT = "kk:mm";
    private TextView _startDateView;
    private TextView _startTimeView;
    private TextView _endDateView;
    private TextView _endTimeView;
    private TextView _startDateTitle;
    private TextView _endDateTitle;
    private TextView _startTimeTitle;
    private TextView _endTimeTitle;
    private TextView _done;
    private boolean _endDateSet = false;
    private boolean _startDateSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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

            startDateLayout.setOnClickListener(new View.OnClickListener() { //Start date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    final MutableDateTime startDate = _exportService.getExportSettings().getStartDate();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 startDate.setYear(year);
                                                 startDate.setMonthOfYear(monthOfYear + 1);
                                                 startDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
                                                 _startDateSet = true;
                                                 validateDates(startDate, _exportService.getExportSettings().getEndDate());
                                                 _startDateView.setText(dateString);
                                                 _startDateView.setVisibility(View.VISIBLE);
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
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 _exportService.getExportSettings().setStartTime(lt);
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _startTimeView.setText(timeString);
                                                 _startTimeView.setVisibility(View.VISIBLE);
                                             }
                                         }, _exportService.getExportSettings().getStartTime().getHourOfDay(), _exportService.getExportSettings().getStartTime().getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity)
                            );

                    timePickerDialog.show(fm, "Start time picker");
                }
            });

            endDateLayout.setOnClickListener(new View.OnClickListener() { //End date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    final MutableDateTime endDate = _exportService.getExportSettings().getEndDate();
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 endDate.setYear(year);
                                                 endDate.setMonthOfYear(monthOfYear + 1);
                                                 endDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
                                                 _endDateSet = true;
                                                 validateDates(_exportService.getExportSettings().getStartDate(), endDate);
                                                 _endDateView.setText(dateString);
                                                 _endDateView.setVisibility(View.VISIBLE);
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
                                                 LocalTime lt = new LocalTime()
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute);

                                                 _exportService.getExportSettings().setEndTime(lt);
                                                 String timeString = DateHelper.getTime(getActivity(), lt.toDateTimeToday());
                                                 _endTimeView.setText(timeString);
                                                 _endTimeView.setVisibility(View.VISIBLE);
                                             }
                                         }, _exportService.getExportSettings().getEndTime().getHourOfDay(), _exportService.getExportSettings().getEndTime().getMinuteOfHour(),
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
        MutableDateTime startDate = _exportService.getExportSettings().getStartDate();
        if (startDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
            String dateStringTime = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getStartTime().toDateTimeToday());
            _startDateView.setText(dateString);
            _startDateView.setVisibility(View.VISIBLE);
            _startTimeView.setText(dateStringTime);
            _startTimeView.setVisibility(View.VISIBLE);
            _startDateSet = true;
        }

        MutableDateTime endDate = _exportService.getExportSettings().getEndDate();
        if (endDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
            String dateStringTime = DateHelper.getTime(getActivity(), _exportService.getExportSettings().getEndTime().toDateTimeToday());
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
        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        if (endDate.getMillis() < startDate.getMillis() && _endDateSet) {
            _endTimeView.setTextColor(Color.RED);
            _endDateView.setTextColor(Color.RED);
            _startDateView.setTextColor(Color.RED);
            _startTimeView.setTextColor(Color.RED);
            Toast.makeText(activity, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
        }
        else {
            _endTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _endDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startTimeView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
        }
    }

}
