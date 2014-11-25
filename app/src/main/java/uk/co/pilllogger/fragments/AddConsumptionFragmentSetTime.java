package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.path.android.jobqueue.JobManager;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.services.IAddConsumptionService;
import uk.co.pilllogger.services.IExportService;
import uk.co.pilllogger.state.State;

public class AddConsumptionFragmentSetTime extends PillLoggerFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private final String FRAG_TAG_TIME_PICKER = "fragent_time_picker_name";
    private final String TAG = "AddConsumptionFragment";
    private View _view;
    private Date _consumptionDate = new Date();
    private IAddConsumptionService _service;

    @Inject JobManager _jobManager;

    @InjectView(R.id.add_consumption_fragment_select_time_title)
    public TextView _timeTitle;

    @InjectView(R.id.add_consumption_fragment_time_summary)
    public TextView _timeSummary;

    @InjectView(R.id.add_consumption_fragment_select_date_title)
    public TextView _dateTitle;

    @InjectView(R.id.add_consumption_fragment_date_summary)
    public TextView _dateSummary;

    @InjectView(R.id.add_consumption_fragment_set_time_done_text)
    public TextView _doneText;

    @InjectView(R.id.add_consumption_fragment_set_time_cancel_text)
    public TextView _cancelText;

    @InjectView(R.id.add_consumption_fragment_set_time_done)
    public View _doneLayout;

    @InjectView(R.id.add_consumption_fragment_set_time_cancel)
    public View _cancelLayout;

    public AddConsumptionFragmentSetTime(){

    }

    @SuppressLint("ValidFragment")
    public AddConsumptionFragmentSetTime(IAddConsumptionService service) {
        _service = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.add_consumption_set_time, container, false);
        ButterKnife.inject(this, _view);

        _dateTitle.setTypeface(State.getSingleton().getRobotoTypeface());
        _timeTitle.setTypeface(State.getSingleton().getRobotoTypeface());
        _doneText.setTypeface(State.getSingleton().getRobotoTypeface());
        _cancelText.setTypeface(State.getSingleton().getRobotoTypeface());

        setUpSpinners();

        _doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });

        _cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        return _view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpSpinners() {
        Date date = new Date();
        View datePickerContainer = _view.findViewById(R.id.add_consumption_fragment_date_layout);
        final Context context = this.getActivity();
        final MutableDateTime finalDate = new MutableDateTime();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                FragmentManager fm = ((FragmentActivity)AddConsumptionFragmentSetTime.this.getActivity()).getSupportFragmentManager();

                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                         @Override
                                         public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                             finalDate.setYear(year);
                                             finalDate.setMonthOfYear(monthOfYear + 1);
                                             finalDate.setDayOfMonth(dayOfMonth);

                                             String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
                                             _dateSummary.setText(dateString);
                                             _consumptionDate.setTime(finalDate.toDate().getTime());
                                         }
                                     }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                finalDate.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        };
        datePickerContainer.setOnClickListener(listener);


        View timePickerContainer = _view.findViewById(R.id.add_consumption_fragment_time_layout);
        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                    FragmentActivity activity = (FragmentActivity) AddConsumptionFragmentSetTime.this.getActivity();
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 Date dt = new DateTime(finalDate)
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();
                                                 java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);

                                                 finalDate.setTime(dt.getTime());
                                                 _consumptionDate.setTime(dt.getTime());
                                                 _timeSummary.setText(timeFormat.format(dt.getTime()));
                                             }
                                         }, finalDate.getHourOfDay(), finalDate.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity));

                    timePickerDialog.show(activity.getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                }
        };

        timePickerContainer.setOnClickListener(timeListener);
    }

    public void done() {
        _service.setConsumptionDate(_consumptionDate);
        getActivity().getFragmentManager().popBackStack();
    }

}
