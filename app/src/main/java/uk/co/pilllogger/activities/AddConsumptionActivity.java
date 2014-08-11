package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.events.CreatedPillEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.helpers.AlarmHelper;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.GetTutorialSeenTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.InsertPillTask;
import uk.co.pilllogger.tasks.SetTutorialSeenTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GetTutorialSeenTask.ITaskComplete,
        AddConsumptionPillListAdapter.IConsumptionSelected{

    private static final String TAG = "AddConsumptionActivity";
    public static String DATE_FORMAT = "E, MMM dd, yyyy";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragent_time_picker_name";

    ListView _pillsList;
    Activity _activity;
    TextView _newPillName;
    TextView _newPillSize;
    AddConsumptionPillListAdapter _adapter;
    View _selectPillLayout;
    View _newPillLayout;
    Spinner _timeSpinner;
    Spinner _dateSpinner;
    Spinner _unitSpinner;
    private Spinner _reminderDateSpinner;
    private Spinner _reminderTimeSpinner;

    RadioGroup _choosePillRadioGroup;
    private RadioGroup _reminderRadioGroup;

    Date _consumptionDate = new Date();
    Date _reminderDate = new Date();
    private List<Pill> _addedPills = new ArrayList<Pill>();
    private ColourIndicator _colour;

    TextView _reminderTitle;
    CheckBox _reminderToggle;
    ViewGroup _reminderHoursContainer;
    EditText _reminderHours;
    TextView _reminderHoursSuffix;
    Bus _bus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(State.getSingleton().getTheme().getStyleResourceId());

        setContentView(R.layout.add_consumption_activity);

        _bus = State.getSingleton().getBus();

        _pillsList = (ListView)findViewById(R.id.add_consumption_pill_list);

        if(PillRepository.getSingleton(this).isCached() == false) { // this should be handled by the producer
            new GetPillsTask(this).execute();
        }

        _activity = this;
        _selectPillLayout = _activity.findViewById(R.id.add_consumption_pill_list);
        _newPillLayout = _activity.findViewById(R.id.add_consumption_quick_create);

        _dateSpinner = (Spinner) findViewById(R.id.add_consumption_date);
        _timeSpinner = (Spinner) findViewById(R.id.add_consumption_time);

        _reminderDateSpinner = (Spinner) findViewById(R.id.add_consumption_reminder_date);
        _reminderTimeSpinner = (Spinner)findViewById(R.id.add_consumption_reminder_time);

        Typeface typeface = State.getSingleton().getTypeface();
        _newPillName = (TextView) findViewById(R.id.pill_fragment_add_pill_name);
        _newPillSize = (TextView) findViewById(R.id.pill_fragment_add_pill_size);
        TextView title = (TextView) findViewById(R.id.pill_fragment_add_pill_title);
        TextView colourText = (TextView) findViewById(R.id.pill_fragment_add_pill_colour);
        TextView create = (TextView) findViewById(R.id.pill_fragment_add_pill_create);
        _newPillName.setTypeface(typeface);
        _newPillSize.setTypeface(typeface);
        title.setVisibility(View.GONE);
        colourText.setTypeface(typeface);
        create.setTypeface(typeface);

        _colour = (ColourIndicator) findViewById(R.id.pill_fragment_colour);
        _colour.setColour(getResources().getColor(R.color.pill_colour7));
        _colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View colourHolder = AddConsumptionActivity.this.findViewById(R.id.pill_fragment_colour_picker_container);
                final ViewGroup colourContainer = (ViewGroup) colourHolder.findViewById(R.id.colour_container);
                if (colourHolder.getVisibility() == View.VISIBLE) {
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(null);
                        }
                    }
                    colourHolder.setVisibility(View.GONE);
                } else {
                    colourHolder.setVisibility(View.VISIBLE);
                    int colourCount = colourContainer.getChildCount();
                    for (int i = 0; i < colourCount; i++) {
                        View colourView = colourContainer.getChildAt(i);
                        if (colourView != null) {
                            colourView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int colour = ((ColourIndicator) view).getColour();
                                    _colour.setColour(colour);
                                    colourHolder.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        });

        View addPillCompleted = findViewById(R.id.pill_fragment_add_pill_completed);
        addPillCompleted.setOnClickListener(new AddNewPillClickListener());

        _newPillSize.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    completed();
                    handled = true;
                }
                return handled;
            }
        });

        setUpRadioGroups();
        setUpSpinners();

        _reminderTitle = (TextView) findViewById(R.id.add_consumption_set_reminder_title);
        _reminderToggle = (CheckBox) findViewById(R.id.add_consumption_set_reminder_toggle);
        _reminderHoursContainer = (ViewGroup) findViewById(R.id.add_consumption_reminder_layout);
        _reminderHours = (EditText) findViewById(R.id.add_consumption_reminder_hours);
        _reminderHoursSuffix = (TextView)findViewById(R.id.add_consumption_reminder_hours_suffix);

        final String defaultSuffix = getString(R.string.add_consumption_hours_suffix);
        final Context context = this;

        _reminderHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = defaultSuffix;
                if(s.length() > 0){
                    int hours = Integer.parseInt(s.toString());
                    DateTime dt = DateTime.now().plusHours(hours);

                    if(hours == 1)
                        str = str.substring(0, str.length() - 1);

                    str += " (" + DateHelper.getTime(context, dt) + ")";
                }

                _reminderHoursSuffix.setText(str);
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

        _unitSpinner = (Spinner) this.findViewById(R.id.units_spinner);
        String[] units = getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _unitSpinner.setAdapter(adapter);
        if (!(State.getSingleton().getOpenPills().size() > 0))
            setDoneEnabled(false);

        new GetTutorialSeenTask(this, TAG, this).execute();
    }

    @Override
    protected void onResume(){
        super.onResume();

        _bus.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();

        _bus.unregister(this);
    }

    private void setUpRadioGroups() {
        _choosePillRadioGroup = (RadioGroup) findViewById(R.id.add_consumption_pill_type_selection);
        _choosePillRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.add_consumption_select_select_pill) {
                    showSelectPillOptions();
                } else {
                    showNewPillOptions();
                }
            }
        });

        _reminderRadioGroup = (RadioGroup) findViewById(R.id.add_consumption_reminder_type_selection);
        _reminderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View reminderDatePickers = findViewById(R.id.add_consumption_reminder_date_pickers_layout);
                View reminderDateContainer = findViewById(R.id.add_consumption_reminder_date_container);
                View reminderTimeContainer = findViewById(R.id.add_consumption_reminder_time_container);
                View reminderHoursContainer = findViewById(R.id.add_consumption_reminder_hours_container);
                if (checkedId == R.id.add_consumption_select_reminder_hours) {
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

    private void showNewPillOptions(){
        _selectPillLayout.setVisibility(View.GONE);
        _newPillLayout.setVisibility(View.VISIBLE);
        _choosePillRadioGroup.check(R.id.add_consumption_select_new_pill);
    }

    private void showSelectPillOptions(){
        _newPillLayout.setVisibility(View.GONE);
        _selectPillLayout.setVisibility(View.VISIBLE);
        _choosePillRadioGroup.check(R.id.add_consumption_select_select_pill);
    }

    private void setUpSpinners() {
        Date date = new Date();
        String dateString = DateFormat.format(DATE_FORMAT, date).toString();
        String[] dates = new String[]{dateString};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates );
        _dateSpinner.setAdapter(adapter);
        _reminderDateSpinner.setAdapter(adapter);
        View datePickerContainer = this.findViewById(R.id.add_consumption_date_container);
        View reminderDatePickerContainer = this.findViewById(R.id.add_consumption_reminder_date_container);

        final Context context = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner dateSpinner = null;
                Date date = null;

                final boolean consumptionDate = view.getId() == R.id.add_consumption_date_container;
                final boolean consumptionReminder = view.getId() == R.id.add_consumption_reminder_date_container;

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
                    FragmentManager fm = getSupportFragmentManager();

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

        String time = DateHelper.getTime(this, date);
        String[] times = new String[]{time};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(timeAdapter);
        _reminderTimeSpinner.setAdapter(timeAdapter);
        View timePickerContainer = this.findViewById(R.id.add_consumption_time_container);
        View reminderTimePickerContainer = this.findViewById(R.id.add_consumption_reminder_time_container);

        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Spinner timeSpinner = null;
                Date date = null;

                if(view.getId() == R.id.add_consumption_time_container){
                    timeSpinner = _timeSpinner;
                    date = _consumptionDate;
                }
                else if (view.getId() == R.id.add_consumption_reminder_time_container){
                    timeSpinner = _reminderTimeSpinner;
                    date = _reminderDate;
                }

                final MutableDateTime finalDate = new MutableDateTime(date);
                final Spinner finalSpinner = timeSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {

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
                                    DateFormat.is24HourFormat(AddConsumptionActivity.this));

                    timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                }
            }
        };

        timePickerContainer.setOnClickListener(timeListener);
        reminderTimePickerContainer.setOnClickListener(timeListener);
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event) {
        if(event.getPills().size() == 0){
            showNewPillOptions();
        }
        else{
            showSelectPillOptions();
        }

        /*
        Sorts pill collection based on its last consumption date
         */
        Collections.sort(event.getPills(), new Comparator<Pill>() {
            public int compare(Pill pill1, Pill pill2) {
                if(pill1.getId() == pill2.getId())
                    return 0;

                if (_addedPills.contains(pill1) && !_addedPills.contains(pill2)) { //if pill has been added during this Add Consumption it should be top
                    return -1;
                }
                if (_addedPills.contains(pill2) && !_addedPills.contains(pill1)) {
                    return 1;
                }
                Consumption pill1Consumption = pill1.getLatestConsumption(_activity);
                Consumption pill2Consumption = pill2.getLatestConsumption(_activity);
                if (pill1Consumption == null && pill2Consumption == null) {
                    return 0;
                }
                if (pill1Consumption == null && pill2Consumption != null) {
                    return 1;
                }
                if (pill2Consumption == null && pill1Consumption != null) {
                    return -1;
                }
                return pill2.getLatestConsumption(_activity).getDate().compareTo(pill1.getLatestConsumption(_activity).getDate());
            }
        });

        _adapter = new AddConsumptionPillListAdapter(this, this, R.layout.add_consumption_pill_list, event.getPills(), true);
         _pillsList.setAdapter(_adapter);

        _adapter.updateAdapter(event.getPills());
    }

    public void cancel(View view) {
        _adapter.clearOpenPillsList();
        finish();
    }

    private Date getDateFromSpinners(Spinner date, Spinner time, Date defaultDate) {
        if(date == null || time == null){
            throw new IllegalArgumentException();
        }

        if(date.getSelectedItem() == null
                || time.getSelectedItem() == null)
            return defaultDate;

        String selectedDate = date.getSelectedItem().toString();
        String selectedTime = time.getSelectedItem().toString();

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        java.text.DateFormat tf = DateFormat.getTimeFormat(this);

        try {
            Date parsedDate = format.parse(selectedDate);
            Date parsedTime = tf.parse(selectedTime);

            DateTime parsedDateTime = new DateTime(parsedDate);
            DateTime parsedTimeDateTime = new DateTime(parsedTime);
            return parsedDateTime
                    .withHourOfDay(parsedTimeDateTime.getHourOfDay())
                    .withMinuteOfHour(parsedTimeDateTime.getMinuteOfHour())
                    .toDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return defaultDate;
    }

    public void done(View view){
        done(view, false);
    }

    public void done(View view, final boolean futureConsumptionOk) {
        final View v = view;
        AddConsumptionPillListAdapter adapter = (AddConsumptionPillListAdapter) _pillsList.getAdapter();
        List<Pill> consumptionPills = adapter.getPillsConsumed();

        Date consumptionDate = getDateFromSpinners(_dateSpinner, _timeSpinner, new Date());
        String consumptionGroup = UUID.randomUUID().toString();
        Date reminderDate = null;
        RadioButton reminderDateSelectorHours = (RadioButton)findViewById(R.id.add_consumption_select_reminder_hours);

        if(_reminderToggle.isChecked()){
            if(!reminderDateSelectorHours.isChecked()){
                reminderDate = getDateFromSpinners(_reminderDateSpinner, _reminderTimeSpinner, null);
            } else {
                int hours = 0;
                try {
                    hours = Integer.parseInt(_reminderHours.getText().toString());
                }
                catch(NumberFormatException e) {
                    Timber.e("Parse of reminder hours error: " + e.getMessage());
                }
                reminderDate = DateTime.now().plusHours(hours).toDate();
            }
        }

        if (DateHelper.isDateInFuture(consumptionDate) && !futureConsumptionOk) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.add_consumption_future_confirmation))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddConsumptionActivity.this.done(v, true);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
        else if(reminderDate != null && !DateHelper.isDateInFuture(reminderDate)){
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.add_consumption_reminder_warning_past))
                    .setCancelable(true)
                    .setNeutralButton(getString(R.string.ok), null)
                    .show();
        }
        else {
            for (Pill pill : consumptionPills) {
                Consumption consumption = new Consumption(pill, consumptionDate, consumptionGroup);
                new InsertConsumptionTask(this, consumption).execute();
            }

            if(reminderDate != null){
                AlarmHelper.addReminderAlarm(this, reminderDate, consumptionGroup, true);
            }

            _adapter.clearOpenPillsList();
            _adapter.clearConsumedPills();

            TrackerHelper.addConsumptionEvent(this, TAG);

            finish();
        }
    }

    @Subscribe
    public void pillInserted(CreatedPillEvent event) {
        if (_adapter != null) {
            _addedPills.add(event.getPill());

            new GetPillsTask(_activity).execute();
            _adapter.addOpenPill(event.getPill());
            _adapter.addConsumedPill(event.getPill());

            LayoutHelper.hideKeyboard(this);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
    }

    @Override
    public void isTutorialSeen(Boolean seen, String tag) {
        if(!seen) {
            new SetTutorialSeenTask(this, TAG).execute();
        }
    }

    private class AddNewPillClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            completed();
        }
    }

    public void completed() {

        if(_newPillName == null || _newPillSize == null)
            return;

        if(!_newPillName.getText().toString().equals("")){
            CharSequence name = _newPillName.getText();
            float size = 0f;
            if(!_newPillSize.getText().toString().equals("")){
                size = Float.parseFloat(String.valueOf(_newPillSize.getText()));
            }
            Pill pill = new Pill(name, size);
            String units = String.valueOf(_unitSpinner.getSelectedItem());
            pill.setUnits(units);
            pill.setColour(_colour.getColour());

            new InsertPillTask(_activity, pill, (InsertPillTask.ITaskComplete)_activity).execute();

            TrackerHelper.createPillEvent(_activity, TAG);
            _addedPills.add(pill);

            _newPillName.setText("");
            _newPillSize.setText("");

            _newPillLayout.setVisibility(View.GONE);
            _selectPillLayout.setVisibility(View.VISIBLE);

            RadioButton selectPill = (RadioButton) findViewById(R.id.add_consumption_select_select_pill);
            selectPill.setChecked(true);
        }
    }

    public void setDoneEnabled(boolean enabled) {
        View doneText = findViewById(R.id.add_consumption_done_text);
        View doneIcon = findViewById(R.id.add_consumption_done_icon);
        View doneLayout = findViewById(R.id.add_consumption_done_layout);
        if (!enabled) {
            doneText.setAlpha(0.25f);
            doneIcon.setAlpha(0.25f);
            doneLayout.setClickable(false);
        }
        else {
            doneText.setAlpha(1);
            doneIcon.setAlpha(1);
            doneLayout.setClickable(true);
        }
    }
}
