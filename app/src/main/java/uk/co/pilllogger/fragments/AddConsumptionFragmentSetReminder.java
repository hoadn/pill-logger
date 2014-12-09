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
import android.widget.EditText;
import android.widget.RadioButton;
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
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.services.IAddConsumptionService;
import uk.co.pilllogger.state.State;

public class AddConsumptionFragmentSetReminder extends PillLoggerFragmentBase {

    private String DATE_FORMAT = "E, MMM dd, yyyy";
    private final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private final String FRAG_TAG_TIME_PICKER = "fragent_time_picker_name";
    private final String TAG = "AddConsumptionFragment";
    private View _view;
    private Date _reminderDate = new Date();
    private IAddConsumptionService _service;

    @Inject JobManager _jobManager;

    @InjectView(R.id.add_consumption_fragment_set_reminder_title)
    public TextView _title;

    @InjectView(R.id.add_consumption_fragment_reminder_type_selection)
    public RadioGroup _reminderRadioGroup;

    @InjectView(R.id.add_consumption_fragment_reminder_layout)
    public View _reminderHoursContainer;

    @InjectView(R.id.add_consumption_fragment_reminder_hours)
    public EditText _reminderHours;

    @InjectView(R.id.add_consumption_fragment_reminder_date)
    public Spinner _dateSpinner;

    @InjectView(R.id.add_consumption_fragment_reminder_time)
    public Spinner _timeSpinner;

    @InjectView(R.id.add_consumption_fragment_done)
    public View _doneLayout;

    @InjectView(R.id.add_consumption_fragment_cancel)
    public View _cancelLayout;

    public AddConsumptionFragmentSetReminder(){

    }

    @SuppressLint("ValidFragment")
    public AddConsumptionFragmentSetReminder(IAddConsumptionService service) {
        _service = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.add_consumption_set_reminder, container, false);
        ButterKnife.inject(this, _view);

        setUpRadioGroups();
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
//
        return _view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpRadioGroups() {
        _reminderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View reminderDatePickers = _view.findViewById(R.id.add_consumption_fragment_reminder_date_pickers_layout);
                View reminderDateContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_date_container);
                View reminderTimeContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_time_container);
                View reminderHoursContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_hours_container);

                if (checkedId == R.id.add_consumption_fragment_select_reminder_hours) {
                    reminderDateContainer.setVisibility(View.GONE);
                    reminderTimeContainer.setVisibility(View.GONE);
                    reminderDatePickers.setVisibility(View.GONE);
                    reminderHoursContainer.setVisibility(View.VISIBLE);
                }
                else {
                    reminderDateContainer.setVisibility(View.VISIBLE);
                    reminderTimeContainer.setVisibility(View.VISIBLE);
                    reminderDatePickers.setVisibility(View.VISIBLE);
                    reminderHoursContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUpSpinners() {
        Date date = new Date();
        String dateString = DateFormat.format(DATE_FORMAT, date).toString();
        String[] dates = new String[]{dateString};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, dates );
        _dateSpinner.setAdapter(adapter);
        View datePickerContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_date_container);

        final Context context = this.getActivity();
        final MutableDateTime finalDate = new MutableDateTime();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Spinner finalSpinner = _dateSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    FragmentManager fm = ((FragmentActivity)AddConsumptionFragmentSetReminder.this.getActivity()).getSupportFragmentManager();

                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 finalDate.setYear(year);
                                                 finalDate.setMonthOfYear(monthOfYear + 1);
                                                 finalDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
                                                 String[] dates = new String[]{dateString};
                                                 ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dates );
                                                 finalSpinner.setAdapter(adapter);

                                                 _reminderDate.setTime(finalDate.toDate().getTime());

                                             }
                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                    finalDate.getDayOfMonth());
                    calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
                }
            }
        };
        datePickerContainer.setOnClickListener(listener);

        String time = DateHelper.getTime(this.getActivity(), date);
        String[] times = new String[]{time};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(timeAdapter);
        View timePickerContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_time_container);

        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final Spinner finalSpinner = _timeSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    FragmentActivity activity = (FragmentActivity) AddConsumptionFragmentSetReminder.this.getActivity();
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
                                                 Date dt = new DateTime(finalDate)
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();
                                                 java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);

                                                 String[] times = new String[]{ timeFormat.format(dt) };

                                                 finalDate.setTime(dt.getTime());

                                                 _reminderDate.setTime(dt.getTime());

                                                 ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, times );
                                                 finalSpinner.setAdapter(adapter);
                                             }
                                         }, finalDate.getHourOfDay(), finalDate.getMinuteOfHour(),
                                    DateFormat.is24HourFormat(activity));

                    timePickerDialog.show(activity.getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                }
            }
        };

        timePickerContainer.setOnClickListener(timeListener);
    }

    public void done() {
        Date reminderDate = new Date();
        RadioButton reminderDateSelectorHours = (RadioButton)_view.findViewById(R.id.add_consumption_fragment_select_reminder_hours);


        if(!reminderDateSelectorHours.isChecked()){
            reminderDate = DateHelper.getDateFromSpinners(_dateSpinner, _timeSpinner, null, this.getActivity());
        }
        else {
            int hours = 0;
            try {
                hours = Integer.parseInt(_reminderHours.getText().toString());
            }
            catch(NumberFormatException e) {
                Timber.e("Parse of reminder hours error: " + e.getMessage());
            }
            reminderDate = DateTime.now().plusHours(hours).toDate();
        }

        _service.setReminderDate(reminderDate);
        getActivity().getFragmentManager().popBackStack();
    }

}
