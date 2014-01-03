package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.InsertPillTask;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionActivity extends Activity implements GetPillsTask.ITaskComplete, InsertPillTask.ITaskComplete,
                                                            DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

    RadioGroup _choosePillRadioGroup;
    RadioGroup _dateRadioGroup;

    private DatePickerDialog _startDateDialog;
    private DatePickerDialog _endDateDialog;
    DatePickerDialog.OnDateSetListener _endDateListener;
    DatePickerDialog.OnDateSetListener _startDateListener;

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

        Typeface typeface = State.getSingleton().getTypeface();
        _newPillName = (TextView) findViewById(R.id.add_consumption_add_pill_name);
        _newPillSize = (TextView) findViewById(R.id.add_consumption_add_pill_size);
        _newPillName.setTypeface(typeface);
        _newPillSize.setTypeface(typeface);

        ImageView addPillCompleted = (ImageView) findViewById(R.id.add_consumption_add_pill_completed);
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
                    datePickers.setVisibility(View.INVISIBLE);
                } else {
                    datePickers.setVisibility(View.VISIBLE);
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
        View datePickerContainer = this.findViewById(R.id.add_consumption_date_container);
        datePickerContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (_dateSpinner.isEnabled()) {
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dateDialog = new DatePickerDialog(AddConsumptionActivity.this, AddConsumptionActivity.this, year, month, day);
                    dateDialog.show();
                }
            }
        });

        String time = DateFormat.format(TIME_FORMAT, date.getTime()).toString();
        String[] times = new String[]{time};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(timeAdapter);
        View timePickerContainer = this.findViewById(R.id.add_consumption_time_container);
        timePickerContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (_timeSpinner.isEnabled()) {
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    TimePickerDialog timeDialog = new TimePickerDialog(AddConsumptionActivity.this, AddConsumptionActivity.this, hour, minute, true);
                    timeDialog.show();
                }
            }
        });
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        if(pills.size() == 0){
            showNewPillOptions();
        }
        else{
            showSelectPillOptions();
        }

        if (_adapter == null) {
            _adapter = new AddConsumptionPillListAdapter(this, R.layout.add_consumption_pill_list, pills);
            _pillsList.setAdapter(_adapter);
            _pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)_pillsList.getAdapter()));
        }
        _adapter.updateAdapter(pills);
    }

    public void cancel(View view) {
        _adapter.clearOpenPillsList();
        finish();
    }

    public void done(View view) {
        _adapter.clearOpenPillsList();
        AddConsumptionPillListAdapter adapter = (AddConsumptionPillListAdapter) _pillsList.getAdapter();
        List<Pill> consumptionPills = adapter.getPillsConsumed();

        Date date = new Date();
        RadioButton dateSelectorNow = (RadioButton) findViewById(R.id.add_consumption_select_select_now);
        if (!dateSelectorNow.isChecked()
                && _dateSpinner.getSelectedItem() != null
                && _timeSpinner.getSelectedItem() != null) {
            String selectedDate = _dateSpinner.getSelectedItem().toString();
            String selectedTime = _timeSpinner.getSelectedItem().toString();
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT);
            try {
                date = format.parse(selectedDate + selectedTime);
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (Pill pill : consumptionPills) {
            Consumption consumption = new Consumption(pill, date);
            new InsertConsumptionTask(this, consumption).execute();
        }
        _adapter.clearConsumedPills();
        finish();
    }

    @Override
    public void pillInserted(Pill pill) {
        if (_adapter != null) {
            new GetPillsTask(_activity, (GetPillsTask.ITaskComplete)_activity).execute();
            _adapter.addOpenPill(pill);
            _adapter.addConsumedPill(pill);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Calendar cal = Calendar.getInstance();
        cal.set(i, i2, i3);
        Date date = cal.getTime();
        String dateString = DateFormat.format(DATE_FORMAT, date).toString();
        String[] dates = new String[]{dateString};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates );
        _dateSpinner.setAdapter(adapter);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        String hours = String.valueOf(i);
        if (i < 10)
            hours = "0" + hours;
        String minutes = String.valueOf(i2);
        if (i2 < 10)
            minutes = "0" + minutes;
        String[] times = new String[]{hours + ":" + minutes};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, times );
        _timeSpinner.setAdapter(adapter);
    }

    private class addNewPillClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            completed();
        }
    }

    public void completed() {
        CharSequence name = _newPillName.getText();
        int size = Integer.parseInt(String.valueOf(_newPillSize.getText()));
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