package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.path.android.jobqueue.JobManager;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.adapters.AddConsumptionPillRecyclerAdapter;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.AlarmHelper;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.InsertConsumptionsJob;
import uk.co.pilllogger.jobs.UpdatePillJob;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.services.IAddConsumptionService;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

public class AddConsumptionFragment extends PillLoggerFragmentBase {
    private Date _consumptionDate = new Date();
    private int _consumptionQuantity = 1;
    private IAddConsumptionService _service;

    private Pill _pill;
    @Inject JobManager _jobManager;

    @InjectView(R.id.add_consumption_fragment_title)
    public TextView _title;

    @InjectView(R.id.add_consumption_fragment_sub_title)
    public TextView _subTitle;

    @InjectView(R.id.add_consumption_fragment_decrease)
    public ImageView _decreaseQuantity;

    @InjectView(R.id.add_consumption_fragment_increase)
    public ImageView _increaseQuantity;

    @InjectView(R.id.add_consumption_fragment_quantity)
    public TextView _quantity;

    @InjectView(R.id.add_consumption_fragment_set_time)
    public View _timeLayout;

    @InjectView(R.id.add_consumption_fragment_set_time_title)
    public TextView _timeTitle;

    @InjectView(R.id.add_consumption_fragment_set_time_summary)
    public TextView _timeSummary;

    @InjectView(R.id.add_consumption_fragment_set_reminder)
    public View _reminderLayout;

    @InjectView(R.id.add_consumption_fragment_set_reminder_title)
    public TextView _reminderTitle;

    @InjectView(R.id.add_consumption_fragment_set_reminder_summary)
    public TextView _reminderSummary;

    @InjectView(R.id.add_consumption_fragment_quantity_title)
    public TextView _quantityTitle;

    @InjectView(R.id.add_consumption_fragment_done)
    public View _doneLayout;

    @InjectView(R.id.add_consumption_fragment_cancel)
    public View _cancelLayout;

    public AddConsumptionFragment(){

    }

    @SuppressLint("ValidFragment")
    public AddConsumptionFragment(Pill pill, IAddConsumptionService service) {
        _pill = pill;
        _service = service;
    }

    @SuppressLint("ValidFragment")
    public AddConsumptionFragment(Consumption consumption, IAddConsumptionService service){
        this(consumption.getPill(), service);

        _consumptionQuantity = consumption.getQuantity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Date consumptionDate = _service.getConsumptionDate();
        if (consumptionDate != null) {
            _timeSummary.setText(DateHelper.getAbsoluteDateTime(getActivity(), consumptionDate));
        }

        Date reminderDate = _service.getReminderDate();
        if (reminderDate != null) {
            _reminderSummary.setText(DateHelper.getAbsoluteDateTime(getActivity(), reminderDate));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_consumption, container, false);
        ButterKnife.inject(this, view);

        if(_pill != null) {
            _subTitle.setText("Take " + _pill.getName() + " " + _pill.getFormattedSize() + _pill.getUnits());
        }

        ((DialogActivity) getActivity()).setTopInfoHidden(true);

        setTypeFace();
        setClickListeners();

        _quantity.setText(String.valueOf(_consumptionQuantity));

        return view;
    }

    private void setClickListeners() {
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

        _timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNewFragment(new AddConsumptionFragmentSetTime(_service));
            }
        });

        _reminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNewFragment(new AddConsumptionFragmentSetReminder(_service));
            }
        });

        _doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done(false);
            }
        });

        _cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finished();
            }
        });
    }

    private void setTypeFace() {
        _subTitle.setTypeface(State.getSingleton().getRobotoTypeface());
        _quantityTitle.setTypeface(State.getSingleton().getRobotoTypeface());
        _timeTitle.setTypeface(State.getSingleton().getRobotoTypeface());
        _reminderTitle.setTypeface(State.getSingleton().getRobotoTypeface());
    }

    private void moveToNewFragment(PillLoggerFragmentBase fragment) {
        android.app.FragmentManager fm = this.getActivity().getFragmentManager();

        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.export_container, fragment)
                .addToBackStack(null)
                .commit();
    }



    public void done(final boolean futureConsumptionOk) {
        Date consumptionDate = _service.getConsumptionDate();
        if (consumptionDate == null) {
            consumptionDate = new Date();
        }

        Date reminderDate = _service.getReminderDate();


        if (DateHelper.isDateInFuture(_consumptionDate) && !futureConsumptionOk) {
            new AlertDialog.Builder(this.getActivity())
                    .setMessage(getString(R.string.add_consumption_future_confirmation))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddConsumptionFragment.this.done(true);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
        else if(reminderDate != null && !DateHelper.isDateInFuture(reminderDate)){
            new AlertDialog.Builder(this.getActivity())
                    .setMessage(getString(R.string.add_consumption_reminder_warning_past))
                    .setCancelable(true)
                    .setNeutralButton(getString(R.string.ok), null)
                    .show();
        }
        else {
            List<Consumption> consumptions = new ArrayList<Consumption>();
            int quantity = Integer.valueOf(_quantity.getText().toString());
            for (int i = 0; i < quantity; i++) {
                Consumption consumption = new Consumption(_pill, consumptionDate);
                consumptions.add(consumption);
            }


            _jobManager.addJobInBackground(new InsertConsumptionsJob(consumptions, true));
            Toast.makeText(this.getActivity(), quantity + " " + _pill.getName() + " added", Toast.LENGTH_SHORT).show();

            if(reminderDate != null) {
                AlarmHelper.addReminderAlarm(this.getActivity(), reminderDate, consumptions.get(0).getGroup(), true);
            }

            TrackerHelper.addConsumptionEvent(this.getActivity(), "AddConsumptionFragment");

            finished();
        }
    }

    private void finished() {
        this.getActivity().finish();
    }

}
