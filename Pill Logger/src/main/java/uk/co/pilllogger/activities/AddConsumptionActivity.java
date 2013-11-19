package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.cntwo.pilllogger.listeners.PillItemClickListener;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.state.State;
import uk.co.cntwo.pilllogger.tasks.GetPillsTask;
import uk.co.cntwo.pilllogger.tasks.InsertConsumptionTask;
import uk.co.cntwo.pilllogger.tasks.InsertPillTask;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionActivity extends Activity implements GetPillsTask.ITaskComplete, InsertPillTask.ITaskComplete,
                                                            DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AddConsumptionActivity";
    ListView _pillsList;
    Activity _activity;
    Typeface _openSans;
    TextView _newPillName;
    TextView _newPillSize;
    AddConsumptionPillListAdapter _adapter;
    View _selectPillLayout;
    View _newPillLayout;
    String[] _dates;
    String[] _times;
    android.text.format.DateFormat _df;
    public static String DATE_FORMAT = "E, MMM dd, yyyy";
    public static String TIME_FORMAT = "kk:mm";
    Spinner _timeSpinner;
    Spinner _dateSpinner;

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

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.add_consumption_pill_type_selection);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.add_consumption_select_select_pill) {
                    _newPillLayout.setVisibility(View.GONE);
                    _selectPillLayout.setVisibility(View.VISIBLE);
                }
                else {
                    _selectPillLayout.setVisibility(View.GONE);
                    _newPillLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        RadioGroup dateRadioGroup = (RadioGroup) findViewById(R.id.add_consumption_time_type_selection);
        dateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View datePickers = findViewById(R.id.add_consumption_date_pickers_layout);
                if (checkedId == R.id.add_consumption_select_select_now) {
                    datePickers.setVisibility(View.GONE);
                }
                else {
                    datePickers.setVisibility(View.VISIBLE);
                }
            }
        });

        _openSans = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");
        _newPillName = (TextView) findViewById(R.id.add_consumption_add_pill_name);
        _newPillSize = (TextView) findViewById(R.id.add_consumption_add_pill_size);
        _newPillName.setTypeface(_openSans);
        _newPillSize.setTypeface(_openSans);

        ImageView addPillCompleted = (ImageView) findViewById(R.id.add_consumption_add_pill_completed);
        addPillCompleted.setOnClickListener(new addNewPillClickListener());

        setUpSpinners();
    }

    private void setUpSpinners() {
        Date date = new Date();
        _df = new android.text.format.DateFormat();
        String dateString = _df.format(DATE_FORMAT, date).toString();
        _dates = new String[]{ dateString };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _dates );
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
                    DatePickerDialog dateDialog = new DatePickerDialog(AddConsumptionActivity.this, (DatePickerDialog.OnDateSetListener) AddConsumptionActivity.this, year, month, day);
                    dateDialog.show();
                }
            }
        });

        String time = _df.format(TIME_FORMAT, date.getTime()).toString();
        _times = new String[] { time };
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _times );
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
                    TimePickerDialog timeDialog = new TimePickerDialog(AddConsumptionActivity.this, (TimePickerDialog.OnTimeSetListener) AddConsumptionActivity.this, hour, minute, true);
                    timeDialog.show();
                }
            }
        });
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        if (_adapter == null) {
            _adapter = new AddConsumptionPillListAdapter(this, R.layout.add_consumption_pill_list, pills);
            _pillsList.setAdapter(_adapter);
            _pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)_pillsList.getAdapter()));
        }
        _adapter.updateAdapter(pills);
    }

    public void cancel(View view) {
        State.getSingleton().clearOpenPillsList();
        finish();
    }

    public void done(View view) {
        State.getSingleton().clearOpenPillsList();
        AddConsumptionPillListAdapter adapter = (AddConsumptionPillListAdapter) _pillsList.getAdapter();
        List<Pill> consumptionPills = adapter.getPillsConsumed();

        Date date = new Date();
        RadioButton dateSelectorNow = (RadioButton) findViewById(R.id.add_consumption_select_select_now);
        if (!dateSelectorNow.isChecked()) {
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
        finish();
    }

    @Override
    public void pillInserted(Pill pill) {
        if (_adapter != null) {
            new GetPillsTask(_activity, (GetPillsTask.ITaskComplete)_activity).execute();
            State.getSingleton().addOpenPill(pill);
            _adapter.addConsumedPill(pill);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {;
        Calendar cal = Calendar.getInstance();
        cal.set(i, i2, i3);
        Date date = cal.getTime();
        _df = new android.text.format.DateFormat();
        String dateString = _df.format(DATE_FORMAT, date).toString();
        _dates = new String[]{ dateString };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _dates );
        _dateSpinner.setAdapter(adapter);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        _times = new String[]{ String.valueOf(i) + ":" + String.valueOf(i2) };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _times );
        _timeSpinner.setAdapter(adapter);
    }

    private class addNewPillClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String name = _newPillName.getText().toString();
            int size = Integer.parseInt(_newPillSize.getText().toString());
            Pill pill = new Pill(name, size);
            new InsertPillTask(_activity, pill, (InsertPillTask.ITaskComplete)_activity).execute();

            _newPillName.setText("");
            _newPillSize.setText("");

            _newPillLayout.setVisibility(View.GONE);
            _selectPillLayout.setVisibility(View.VISIBLE);

            RadioButton selectPill = (RadioButton) findViewById(R.id.add_consumption_select_select_pill);
            selectPill.setChecked(true);
        }
    }
}