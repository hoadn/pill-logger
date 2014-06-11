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
    private TextView _startDateView;
    private TextView _endDateView;
    private TextView _startDateTitle;
    private TextView _endDateTitle;
    private TextView _done;
    private boolean _endDateSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_export_select_date, container, false);

        if(view != null) {
            _startDateTitle = (TextView) view.findViewById(R.id.export_start_date_title);
            _endDateTitle = (TextView) view.findViewById(R.id.export_end_date_title);
            _done = (TextView) view.findViewById(R.id.export_pills_done);
            _startDateView = (TextView) view.findViewById(R.id.export_start_date);
            _endDateView = (TextView) view.findViewById(R.id.export_end_date);

            setTypeface();
            loadDates();

            final Activity activity = getActivity();

            View startDateLayout = view.findViewById(R.id.export_start_date_layout);
            View endDateLayout = view.findViewById(R.id.export_end_date_layout);

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
        LocalTime startTime = _exportService.getExportSettings().getStartTime();
        if (startDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
            _startDateView.setText(dateString);
            _startDateView.setVisibility(View.VISIBLE);
        }

        MutableDateTime endDate = _exportService.getExportSettings().getEndDate();
        if (endDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
            _endDateView.setText(dateString);
            _endDateView.setVisibility(View.VISIBLE);
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
        _startDateView.setTypeface(typeface);
        _endDateView.setTypeface(typeface);
    }

    private void validateDates(MutableDateTime startDate, MutableDateTime endDate) {
        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        if (endDate.getMillis() < startDate.getMillis() && _endDateSet) {
            _endDateView.setTextColor(Color.RED);
            _startDateView.setTextColor(Color.RED);
            Toast.makeText(activity, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
        }
        else {
            _endDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
        }
    }

}
