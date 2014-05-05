package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.MutableDateTime;

import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class ConsumptionInfoDialog extends InfoDialog {
    private Activity _activity;
    private Consumption _consumption;
    private ConsumptionInfoDialogListener _listener;
    public static String DATE_FORMAT = "E, MMM dd, yyyy";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    public ConsumptionInfoDialog(){
        super();
    }

    public ConsumptionInfoDialog(Activity activity, Consumption consumption, ConsumptionInfoDialogListener listener) {
        super(consumption.getPill());
        _activity = activity;
        _consumption = consumption;
        _listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.consumption_info_dialog;
    }

    @Override
    protected void setupMenu(View view) {

        TextView takeAgain = (TextView) view.findViewById(R.id.consumption_info_dialog_take);
        TextView increase = (TextView) view.findViewById(R.id.consumption_info_dialog_increase);
        TextView decrease = (TextView) view.findViewById(R.id.consumption_info_dialog_decrease);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);
        TextView timeText = (TextView) view.findViewById(R.id.consumption_info_dialog_time_text);
        final View time = view.findViewById(R.id.consumption_info_dialog_pickers_layout);

        Typeface typeface = State.getSingleton().getTypeface();
        takeAgain.setTypeface(typeface);
        increase.setTypeface(typeface);
        decrease.setTypeface(typeface);
        delete.setTypeface(typeface);
        timeText.setTypeface(typeface);

        if(_consumption == null) {
            dismiss();
            return;
        }

        takeAgain.setText(String.format("%s %d %s %s", _activity.getString(R.string.consumption_info_dialog_take_again_prefix), _consumption.getQuantity(), _consumption.getPill().getName(), _activity.getString(R.string.consumption_info_dialog_take_again_suffix)));

        if(_consumption.getQuantity() > 1){
            decrease.setClickable(true);
            decrease.setEnabled(true);
            decrease.setTextColor(_activity.getResources().getColor(R.color.text_grey));
        }
        else {
            decrease.setClickable(false);
            decrease.setEnabled(false);
        }

        takeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogTakeAgain(_consumption, ConsumptionInfoDialog.this);
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogIncrease(_consumption, ConsumptionInfoDialog.this);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogDecrease(_consumption, ConsumptionInfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogDelete(_consumption, ConsumptionInfoDialog.this);
            }
        });

        setUpSpinners(view);



        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time.getVisibility() == View.GONE)
                    time.setVisibility(View.VISIBLE);
                else
                    time.setVisibility(View.GONE);
            }
        });
    }

    private void setUpSpinners(View view) {
        final Spinner dateSpinner =  (Spinner) view.findViewById(R.id.consumption_info_dialog_date);
        final Spinner timeSpinner = (Spinner) view.findViewById(R.id.consumption_info_dialog_time);

        String dateString = DateFormat.format(DATE_FORMAT, _consumption.getDate()).toString();
        String[] dates = new String[]{dateString};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(_activity, android.R.layout.simple_spinner_item, dates );
        dateSpinner.setAdapter(adapter);

        String timeString = DateHelper.getTime(_activity, _consumption.getDate());
        String[] times = new String[]{timeString};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(_activity, android.R.layout.simple_spinner_item, times );
        timeSpinner.setAdapter(timeAdapter);

        View datePickerContainer = view.findViewById(R.id.add_consumption_date_container);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = null;

                final MutableDateTime finalDate = new MutableDateTime(date);
                final Spinner finalSpinner = dateSpinner;

                if (finalSpinner != null && finalSpinner.isEnabled()) {
                    FragmentManager fm = ((FragmentActivity)_activity).getSupportFragmentManager();

                    CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                            .newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
                                             @Override
                                             public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                                                 finalDate.setYear(year);
                                                 finalDate.setMonthOfYear(monthOfYear + 1);
                                                 finalDate.setDayOfMonth(dayOfMonth);

                                                 String dateString = DateFormat.format(DATE_FORMAT, finalDate.toDate().getTime()).toString();
                                                 String[] dates = new String[]{dateString};
                                                 ArrayAdapter<String> adapter = new ArrayAdapter<String>(_activity, android.R.layout.simple_spinner_item, dates );
                                                 finalSpinner.setAdapter(adapter);

                                             }
                                         }, finalDate.getYear(), finalDate.getMonthOfYear() - 1,
                                    finalDate.getDayOfMonth());
                    calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
                }
            }
        };
        datePickerContainer.setOnClickListener(listener);
    }

    public interface ConsumptionInfoDialogListener {
        public void onDialogTakeAgain(Consumption consumption, InfoDialog dialog);
        public void onDialogIncrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDecrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDelete(Consumption consumption, InfoDialog dialog);
    }
}
