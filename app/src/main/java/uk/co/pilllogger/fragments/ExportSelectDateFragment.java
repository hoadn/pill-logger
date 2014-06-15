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

import org.joda.time.MutableDateTime;

import uk.co.pilllogger.R;
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
    private TextView _exportDateWarning;
    private View _clearStartDate;
    private View _clearEndDate;

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
            _exportDateWarning = (TextView) view.findViewById(R.id.export_date_warning);
            _clearStartDate = view.findViewById(R.id.export_start_date_clear);
            _clearEndDate = view.findViewById(R.id.export_end_date_clear);

            setTypeface();
            loadDates();

            final Activity activity = getActivity();

            View startDateLayout = view.findViewById(R.id.export_start_date_layout);
            View endDateLayout = view.findViewById(R.id.export_end_date_layout);

            _clearStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _exportService.getExportSettings().setStartDate(null);
                    _startDateView.setVisibility(View.GONE);
                    _clearStartDate.setVisibility(View.GONE);
                    _startDateTitle.setText(getActivity().getResources().getString(R.string.export_start_date_select));
                    updateSummaryText(_exportService.getDateSummary());
                }
            });

            _clearEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _exportService.getExportSettings().setEndDate(null);
                    _endDateView.setVisibility(View.GONE);
                    _clearEndDate.setVisibility(View.GONE);
                    _endDateTitle.setText(getActivity().getResources().getString(R.string.export_end_date_select));
                    updateSummaryText(_exportService.getDateSummary());
                }
            });

            startDateLayout.setOnClickListener(new View.OnClickListener() { //Start date picker
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                    MutableDateTime startDate = _exportService.getExportSettings().getStartDate();
                    if(startDate == null) startDate = new MutableDateTime();
                    final MutableDateTime finalStartDate = startDate;
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 MutableDateTime date = new MutableDateTime();

                                                 date.setYear(year);
                                                 date.setMonthOfYear(monthOfYear + 1);
                                                 date.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, date.toDate().getTime()).toString();
                                                 if (validateDates(date, _exportService.getExportSettings().getEndDate())) {
                                                     finalStartDate.setYear(date.getYear());
                                                     finalStartDate.setMonthOfYear(date.getMonthOfYear());
                                                     finalStartDate.setDayOfMonth(date.getDayOfMonth());
                                                     finalStartDate.setHourOfDay(0);
                                                     finalStartDate.setMinuteOfHour(0);
                                                     _exportService.getExportSettings().setStartDate(finalStartDate);

                                                     updateSummaryText(_exportService.getDateSummary());
                                                 }
                                                 _startDateView.setText(dateString);
                                                 _startDateTitle.setText(activity.getResources().getString(R.string.export_start_date));
                                                 _startDateView.setVisibility(View.VISIBLE);
                                                 _clearStartDate.setVisibility(View.VISIBLE);
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
                    MutableDateTime endDate = _exportService.getExportSettings().getEndDate();
                    if(endDate == null) endDate = new MutableDateTime();
                    final MutableDateTime finalEndDate = endDate;
                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 MutableDateTime date = new MutableDateTime();

                                                 date.setYear(year);
                                                 date.setMonthOfYear(monthOfYear + 1);
                                                 date.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, date.toDate().getTime()).toString();
                                                 if (validateDates(_exportService.getExportSettings().getStartDate(), date)) {
                                                     finalEndDate.setYear(date.getYear());
                                                     finalEndDate.setMonthOfYear(date.getMonthOfYear());
                                                     finalEndDate.setDayOfMonth(date.getDayOfMonth());
                                                     finalEndDate.setHourOfDay(0);
                                                     finalEndDate.setMinuteOfHour(0);
                                                     _exportService.getExportSettings().setEndDate(finalEndDate);

                                                     updateSummaryText(_exportService.getDateSummary());
                                                 }
                                                 _endDateTitle.setText(activity.getResources().getString(R.string.export_end_date));
                                                 _endDateView.setText(dateString);
                                                 _endDateView.setVisibility(View.VISIBLE);
                                                 _clearEndDate.setVisibility(View.VISIBLE);
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

        updateSummaryText(_exportService.getDateSummary());

        return view;
    }

    private void loadDates() {
        MutableDateTime startDate = _exportService.getExportSettings().getStartDate();
        if (startDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, startDate.toDate().getTime()).toString();
            _startDateView.setText(dateString);
            _startDateView.setVisibility(View.VISIBLE);
            _clearStartDate.setVisibility(View.VISIBLE);
            if (getActivity() != null)
                _startDateTitle.setText(getActivity().getResources().getString(R.string.export_start_date));
        }

        MutableDateTime endDate = _exportService.getExportSettings().getEndDate();
        if (endDate != null) {
            String dateString = DateFormat.format(DATE_FORMAT, endDate.toDate().getTime()).toString();
            _endDateView.setText(dateString);
            _endDateView.setVisibility(View.VISIBLE);
            _clearEndDate.setVisibility(View.VISIBLE);
            if (getActivity() != null)
                _endDateTitle.setText(getActivity().getResources().getString(R.string.export_end_date));
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
        _exportDateWarning.setTypeface(typeface);
    }

    private boolean validateDates(MutableDateTime startDate, MutableDateTime endDate) {
        Activity activity = getActivity();

        if (activity == null) {
            return true;
        }

        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            int warningColour = activity.getResources().getColor(R.color.warning_red);
            _endDateView.setTextColor(warningColour);
            _startDateView.setTextColor(warningColour);
            _exportDateWarning.setVisibility(View.VISIBLE);
            _done.setText(activity.getString(R.string.discard));
            return false;
        }
        else {
            _endDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _startDateView.setTextColor(activity.getResources().getColor(R.color.text_grey));
            _exportDateWarning.setVisibility(View.GONE);
            _done.setText(activity.getString(R.string.done_label));
            return true;
        }

    }

}
