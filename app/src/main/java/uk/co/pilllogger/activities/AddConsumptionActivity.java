package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.GetTutorialSeenTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.InsertPillTask;
import uk.co.pilllogger.tasks.SetTutorialSeenTask;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionActivity extends Activity implements
        GetPillsTask.ITaskComplete,
        InsertPillTask.ITaskComplete,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GetTutorialSeenTask.ITaskComplete {

    private static final String TAG = "AddConsumptionActivity";
    public static String DATE_FORMAT = "E, MMM dd, yyyy";
    public static String TIME_FORMAT = "kk:mm";

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
    RadioGroup _dateRadioGroup;
    private RadioGroup _reminderRadioGroup;

    private DatePickerDialog _startDateDialog;
    private DatePickerDialog _endDateDialog;
    DatePickerDialog.OnDateSetListener _endDateListener;
    DatePickerDialog.OnDateSetListener _startDateListener;

    Date _consumptionDate = new Date();
    Date _reminderDate = new Date();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_consumption_activity);

        _pillsList = (ListView)findViewById(R.id.add_consumption_pill_list);

        new GetPillsTask(this, this).execute();

        _activity = this;
        _selectPillLayout = _activity.findViewById(R.id.add_consumption_pill_list);
        _newPillLayout = _activity.findViewById(R.id.add_consumption_quick_create);

        _dateSpinner = (Spinner) findViewById(R.id.add_consumption_date);
        _timeSpinner = (Spinner) findViewById(R.id.add_consumption_time);

        _reminderDateSpinner = (Spinner) findViewById(R.id.add_consumption_reminder_date);
        _reminderTimeSpinner = (Spinner)findViewById(R.id.add_consumption_reminder_time);

        Typeface typeface = State.getSingleton().getTypeface();
        _newPillName = (TextView) findViewById(R.id.add_consumption_add_pill_name);
        _newPillSize = (TextView) findViewById(R.id.add_consumption_add_pill_size);
        _newPillName.setTypeface(typeface);
        _newPillSize.setTypeface(typeface);

        View addPillCompleted = findViewById(R.id.add_consumption_add_pill_completed);
        addPillCompleted.setOnClickListener(new addNewPillClickListener());

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

        _unitSpinner = (Spinner) this.findViewById(R.id.add_consumption_units_spinner);
        String[] units = { "mg", "ml" };
        UnitAdapter adapter = new UnitAdapter(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _unitSpinner.setAdapter(adapter);
        if (!(State.getSingleton().getOpenPills().size() > 0))
            setDoneEnabled(false);

        new GetTutorialSeenTask(this, TAG, this).execute();
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

        _dateRadioGroup = (RadioGroup) findViewById(R.id.add_consumption_time_type_selection);
        _dateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View datePickers = findViewById(R.id.add_consumption_date_pickers_layout);
                if (checkedId == R.id.add_consumption_select_select_now) {
                    datePickers.setVisibility(View.GONE);
                } else {
                    datePickers.setVisibility(View.VISIBLE);
                }
            }
        });

        _reminderRadioGroup = (RadioGroup) findViewById(R.id.add_consumption_reminder_type_selection);
        _reminderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View reminderDatePickers = findViewById(R.id.add_consumption_reminder_date_pickers_layout);
                View reminderDateContainer = findViewById(R.id.add_consumption_reminder_date_container);
                View reminderTimeContainer = findViewById(R.id.add_consumption_reminder_time_container);
                if (checkedId == R.id.add_consumption_select_reminder_hours) {
                    reminderDateContainer.setVisibility(View.GONE);
                    reminderTimeContainer.setVisibility(View.GONE);
                    reminderDatePickers.setVisibility(View.INVISIBLE);
                } else {
                    reminderDateContainer.setVisibility(View.VISIBLE);
                    reminderTimeContainer.setVisibility(View.VISIBLE);
                    reminderDatePickers.setVisibility(View.VISIBLE);
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

                if(view.getId() == R.id.add_consumption_date_container){
                    dateSpinner = _dateSpinner;
                    date = _consumptionDate;
                }
                else if (view.getId() == R.id.add_consumption_reminder_date_container){
                    dateSpinner = _reminderDateSpinner;
                    date = _reminderDate;
                }

                final Date finalDate = date;
                final Spinner finalSpinner = dateSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(finalDate);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dateDialog = new DatePickerDialog(AddConsumptionActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(year, monthOfYear, dayOfMonth);
                            String dateString = DateFormat.format(DATE_FORMAT, finalDate).toString();
                            String[] dates = new String[]{dateString};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dates );
                            finalSpinner.setAdapter(adapter);
                        }
                    }, year, month, day);
                    dateDialog.show();
                }
            }
        };
        datePickerContainer.setOnClickListener(listener);
        reminderDatePickerContainer.setOnClickListener(listener);

        String time = DateFormat.format(TIME_FORMAT, date.getTime()).toString();
        String[] times = new String[]{time};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(timeAdapter);
        _reminderTimeSpinner.setAdapter(timeAdapter);
        View timePickerContainer = this.findViewById(R.id.add_consumption_time_container);
        View reminderTimePickerContainer = this.findViewById(R.id.add_consumption_reminder_time_container);

        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                final Date finalDate = date;
                final Spinner finalSpinner = timeSpinner;


                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(finalDate);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    TimePickerDialog timeDialog = new TimePickerDialog(AddConsumptionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String hours = String.valueOf(hourOfDay);
                            if (hourOfDay < 10)
                                hours = "0" + hours;
                            String minutes = String.valueOf(minute);
                            if (minute < 10)
                                minutes = "0" + minutes;
                            String[] times = new String[]{hours + ":" + minutes};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, times );
                            finalSpinner.setAdapter(adapter);
                        }
                    }, hour, minute, true);
                    timeDialog.show();
                }
            }
        };

        timePickerContainer.setOnClickListener(timeListener);
        reminderTimePickerContainer.setOnClickListener(timeListener);
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        if(pills.size() == 0){
            showNewPillOptions();
        }
        else{
            showSelectPillOptions();
        }

        _adapter = new AddConsumptionPillListAdapter(this, R.layout.add_consumption_pill_list, pills);
         _pillsList.setAdapter(_adapter);
        _pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)_pillsList.getAdapter()));

        _adapter.updateAdapter(pills);
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
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT);
        try {
            return format.parse(selectedDate + selectedTime);
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

        Date consumptionDate = new Date();
        String consumptionGroup = UUID.randomUUID().toString();
        Date reminderDate = null;
        RadioButton dateSelectorNow = (RadioButton) findViewById(R.id.add_consumption_select_select_now);
        RadioButton reminderDateSelectorHours = (RadioButton)findViewById(R.id.add_consumption_select_reminder_hours);
        if (!dateSelectorNow.isChecked()) {
            consumptionDate = getDateFromSpinners(_dateSpinner, _timeSpinner, new Date());
        }

        if(!reminderDateSelectorHours.isChecked()){
            reminderDate = getDateFromSpinners(_reminderDateSpinner, _reminderTimeSpinner, null);
        }

        if (DateHelper.isDateInFuture(consumptionDate) && !futureConsumptionOk) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to add this consumption in the future?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddConsumptionActivity.this.done(v, true);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else if(reminderDate != null && !DateHelper.isDateInFuture(reminderDate)){
            new AlertDialog.Builder(this)
                    .setMessage("You must set a date in the future for the reminder")
                    .setCancelable(true)
                    .setNeutralButton("OK", null)
                    .show();
        }
        else {
            for (Pill pill : consumptionPills) {
                Consumption consumption = new Consumption(pill, consumptionDate, consumptionGroup);
                new InsertConsumptionTask(this, consumption).execute();
            }

            if(reminderDate != null){
                long difference = reminderDate.getTime() - new Date().getTime();

                Intent intent = new Intent(getString(R.string.intent_reminder));
                intent.putExtra(getString(R.string.intent_extra_notification_consumption_group), consumptionGroup);

                PendingIntent pi = PendingIntent.getBroadcast(this, reminderDate.hashCode(), intent, 0);
                AlarmManager am = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));

                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + difference, pi);
            }

            _adapter.clearOpenPillsList();
            _adapter.clearConsumedPills();
            finish();
        }
    }


    @Override
    public void pillInserted(Pill pill) {
        if (_adapter != null) {
            new GetPillsTask(_activity, (GetPillsTask.ITaskComplete)_activity).execute();
            _adapter.addOpenPill(pill);
            _adapter.addConsumedPill(pill);

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
            Toast.makeText(this, "Need to show tutorial for add consumption activity", Toast.LENGTH_LONG).show();
            new SetTutorialSeenTask(this, TAG).execute();
        }
    }

    private class addNewPillClickListener implements View.OnClickListener {

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
            int size = 0;
            if(!_newPillSize.getText().toString().equals("")){
                size = Integer.parseInt(String.valueOf(_newPillSize.getText()));
            }
            Pill pill = new Pill(name, size);
            String units = String.valueOf(_unitSpinner.getSelectedItem());
            pill.setUnits(units);
            new InsertPillTask(_activity, pill, (InsertPillTask.ITaskComplete)_activity).execute();

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