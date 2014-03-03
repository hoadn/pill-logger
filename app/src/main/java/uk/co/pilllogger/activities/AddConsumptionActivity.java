package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import uk.co.pilllogger.views.ColourIndicator;

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
    RadioGroup _choosePillRadioGroup;
    RadioGroup _dateRadioGroup;
    private DatePickerDialog _startDateDialog;
    private DatePickerDialog _endDateDialog;
    DatePickerDialog.OnDateSetListener _endDateListener;
    DatePickerDialog.OnDateSetListener _startDateListener;
    public boolean _futureOk = false;
    private List<Pill> _addedPills = new ArrayList<Pill>();
    private ColourIndicator _colour;

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

        _unitSpinner = (Spinner) this.findViewById(R.id.units_spinner);
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

        /*
        Sorts pill collection based on its last consumption date
         */
        Collections.sort(pills, new Comparator<Pill>() {
            public int compare(Pill pill1, Pill pill2) {
                if (_addedPills.contains(pill1) && !_addedPills.contains(pill2)) { //if pill has been added during this Add Consumption it should be top
                    return -1;
                }
                if (_addedPills.contains(pill1) && !_addedPills.contains(pill2)) {
                    return 1;
                }
                Consumption pill1Consumption = pill1.getLatestConsumption();
                Consumption pill2Consumption = pill2.getLatestConsumption();
                if (pill1Consumption == null && pill2Consumption == null) {
                    return 0;
                }
                if (pill1Consumption == null && pill2Consumption != null) {
                    return 1;
                }
                if (pill2Consumption == null && pill1Consumption != null) {
                    return -1;
                }
                return pill2.getLatestConsumption().getDate().compareTo(pill1.getLatestConsumption().getDate());
            }
        });

        _adapter = new AddConsumptionPillListAdapter(this, R.layout.add_consumption_pill_list, pills);
         _pillsList.setAdapter(_adapter);
        _pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)_pillsList.getAdapter()));

        _adapter.updateAdapter(pills);
    }

    public void cancel(View view) {
        _adapter.clearOpenPillsList();
        finish();
    }

    public void done(View view) {
        final View v = view;
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

        if (DateHelper.isDateInFuture(date) && !_futureOk) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to add this consumption in the future?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddConsumptionActivity.this._futureOk = true;
                            AddConsumptionActivity.this.done(v);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else {
            for (Pill pill : consumptionPills) {
                Consumption consumption = new Consumption(pill, date);
                new InsertConsumptionTask(this, consumption).execute();
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
            pill.setColour(_colour.getColour());

            new InsertPillTask(_activity, pill, (InsertPillTask.ITaskComplete)_activity).execute();
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