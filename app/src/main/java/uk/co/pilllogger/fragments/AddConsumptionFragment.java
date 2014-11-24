package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.jobs.UpdatePillJob;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

public class AddConsumptionFragment extends PillLoggerFragmentBase {

    private static String DATE_FORMAT = "E, MMM dd, yyyy";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragent_time_picker_name";
    private View _view;
    private Date _consumptionDate = new Date();
    private Date _reminderDate = new Date();

    private Pill _pill;
    @Inject JobManager _jobManager;

    @InjectView(R.id.add_consumption_fragment_decrease)
    public ImageView _decreaseQuantity;

    @InjectView(R.id.add_consumption_fragment_increase)
    public ImageView _increaseQuantity;

    @InjectView(R.id.add_consumption_fragment_quantity)
    public TextView _quantity;

    @InjectView(R.id.add_consumption_fragment_date)
    public Spinner _dateSpinner;

    @InjectView(R.id.add_consumption_fragment_reminder_date)
    public Spinner _reminderDateSpinner;

    @InjectView(R.id.add_consumption_fragment_time)
    public Spinner _timeSpinner;

    @InjectView(R.id.add_consumption_fragment_reminder_time)
    public Spinner _reminderTimeSpinner;

    @InjectView(R.id.add_consumption_fragment_set_reminder_title)
    public TextView _reminderTitle;

    @InjectView(R.id.add_consumption_fragment_set_reminder_toggle)
    public CheckBox _reminderToggle;

    @InjectView(R.id.add_consumption_fragment_reminder_type_selection)
    public RadioGroup _reminderRadioGroup;

    @InjectView(R.id.add_consumption_fragment_reminder_layout)
    public View _reminderHoursContainer;

    public AddConsumptionFragment(){

    }

    @SuppressLint("ValidFragment")
    public AddConsumptionFragment(Pill pill) {
        _pill = pill;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_add_consumption, container, false);
        ButterKnife.inject(this, _view);

        _decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.valueOf(_quantity.getText().toString());
                if (quantity > 1) {
                    _quantity.setText(String.valueOf(--quantity));
                    if (quantity == 1) {
                        _decreaseQuantity.setClickable(false);
                    }
                }
            }
        });

        _increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.valueOf(_quantity.getText().toString());
                _quantity.setText(String.valueOf(++quantity));
                if (quantity == 2) {
                    _decreaseQuantity.setClickable(true);
                }
            }
        });

        _reminderTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _reminderToggle.setChecked(!_reminderToggle.isChecked());
            }
        });

        _reminderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for(int i = 0; i <_reminderRadioGroup.getChildCount(); i++){
                    _reminderRadioGroup.getChildAt(i).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
                _reminderHoursContainer.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });

        setUpRadioGroups();
        setUpSpinners();

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
                } else {
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
        _reminderDateSpinner.setAdapter(adapter);
        View datePickerContainer = _view.findViewById(R.id.add_consumption_fragment_date_container);
        View reminderDatePickerContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_date_container);

        final Context context = this.getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner dateSpinner = null;
                Date date = null;

                final boolean consumptionDate = view.getId() == R.id.add_consumption_fragment_date_container;
                final boolean consumptionReminder = view.getId() == R.id.add_consumption_fragment_reminder_date_container;

                if(consumptionDate){
                    dateSpinner = _dateSpinner;
                    date = _consumptionDate;
                }
                else if (consumptionReminder){
                    dateSpinner = _reminderDateSpinner;
                    date = _reminderDate;
                }

                final MutableDateTime finalDate = new MutableDateTime(date);
                final Spinner finalSpinner = dateSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    FragmentManager fm = ((FragmentActivity)AddConsumptionFragment.this.getActivity()).getSupportFragmentManager();

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

                                                 if(consumptionDate){
                                                     _consumptionDate.setTime(finalDate.toDate().getTime());
                                                 }
                                                 else if (consumptionReminder){
                                                     _reminderDate.setTime(finalDate.toDate().getTime());
                                                 }
                                             }
                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                    finalDate.getDayOfMonth());
                    calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
                }
            }
        };
        datePickerContainer.setOnClickListener(listener);
        reminderDatePickerContainer.setOnClickListener(listener);

        String time = DateHelper.getTime(this.getActivity(), date);
        String[] times = new String[]{time};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(timeAdapter);
        _reminderTimeSpinner.setAdapter(timeAdapter);
        View timePickerContainer = _view.findViewById(R.id.add_consumption_fragment_time_container);
        View reminderTimePickerContainer = _view.findViewById(R.id.add_consumption_fragment_reminder_time_container);

        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Spinner timeSpinner = null;
                Date date = null;

                if(view.getId() == R.id.add_consumption_fragment_time_container){
                    timeSpinner = _timeSpinner;
                    date = _consumptionDate;
                }
                else if (view.getId() == R.id.add_consumption_fragment_reminder_time_container){
                    timeSpinner = _reminderTimeSpinner;
                    date = _reminderDate;
                }

                final MutableDateTime finalDate = new MutableDateTime(date);
                final Spinner finalSpinner = timeSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    FragmentActivity activity = (FragmentActivity) AddConsumptionFragment.this.getActivity();
                    RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                            .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                             @Override
                                             public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                                                 Date dt = new DateTime(finalDate)
                                                         .withHourOfDay(hourOfDay)
                                                         .withMinuteOfHour(minute)
                                                         .toDate();
                                                 java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);

                                                 String[] times = new String[]{ timeFormat.format(dt) };

                                                 finalDate.setTime(dt.getTime());

                                                 if(view.getId() == R.id.add_consumption_time_container){
                                                     _consumptionDate.setTime(dt.getTime());
                                                 }
                                                 else if (view.getId() == R.id.add_consumption_reminder_time_container){
                                                     _reminderDate.setTime(dt.getTime());
                                                 }

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
        reminderTimePickerContainer.setOnClickListener(timeListener);
    }

}
