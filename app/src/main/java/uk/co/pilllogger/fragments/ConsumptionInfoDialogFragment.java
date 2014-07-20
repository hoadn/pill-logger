package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.DecreaseConsumptionEvent;
import uk.co.pilllogger.events.IncreaseConsumptionEvent;
import uk.co.pilllogger.events.TakeConsumptionAgainEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class ConsumptionInfoDialogFragment extends InfoDialogFragment {
    private Context _context;
    private Consumption _consumption;
    private TextView _takeAgain;
    private TextView _quantityText;
    private TextView _takeAgainSummary;
    private TextView _quantity;
    private TextView _delete;
    private TextView _deleteSummary;
    private TextView _quantitySummary;

    public ConsumptionInfoDialogFragment(){
        super();
    }

    @SuppressLint("ValidFragment")
    public ConsumptionInfoDialogFragment(Context context, Consumption consumption) {
        super(consumption.getPill());
        _context = context;
        _consumption = consumption;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.consumption_info_dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();

        View takeAgainContainer = activity.findViewById(R.id.info_dialog_take_again);
        _takeAgain = (TextView) activity.findViewById(R.id.info_dialog_take_again_title);
        _takeAgainSummary = (TextView) activity.findViewById(R.id.info_dialog_take_again_summary);
        _quantityText = (TextView) activity.findViewById(R.id.info_dialog_quantity_title);
        _quantity = (TextView) activity.findViewById(R.id.consumption_info_quantity);
        _quantitySummary = (TextView) activity.findViewById(R.id.info_dialog_quantity_summary);
        View increase = activity.findViewById(R.id.consumption_info_dialog_increase);
        ImageView decrease = (ImageView) activity.findViewById(R.id.consumption_info_dialog_decrease);
        View deleteContainer = activity.findViewById(R.id.info_dialog_delete);
        _delete = (TextView) activity.findViewById(R.id.info_dialog_delete_title);
        _deleteSummary = (TextView) activity.findViewById(R.id.info_dialog_delete_summary);

        setTypeFace();

        if(_consumption == null) {
            activity.finish();
            return;
        }

        _quantity.setText("" + _consumption.getQuantity());

        String takeAgainText =  _context.getString(R.string.consumption_info_dialog_take_again_prefix);
        if(_consumption.getQuantity() > 1)
            takeAgainText += " " + _consumption.getQuantity();
        takeAgainText += String.format(" %s %s", _consumption.getPill().getName(), _context.getString(R.string.consumption_info_dialog_take_again_suffix));
        _takeAgainSummary.setText(takeAgainText);

        if(_consumption.getQuantity() > 1){
            decrease.setClickable(true);
            decrease.setEnabled(true);
            decrease.setImageResource(R.drawable.chevron_down_red);
        }
        else {
            decrease.setClickable(false);
            decrease.setEnabled(false);
            decrease.setImageResource(R.drawable.chevron_down_grey);
        }

        takeAgainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new TakeConsumptionAgainEvent(_consumption, ConsumptionInfoDialogFragment.this));
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new IncreaseConsumptionEvent(_consumption, ConsumptionInfoDialogFragment.this));
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new DecreaseConsumptionEvent(_consumption, ConsumptionInfoDialogFragment.this));
            }
        });
        deleteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bus.post(new DeleteConsumptionEvent(_consumption, ConsumptionInfoDialogFragment.this));
            }
        });
    }

    private void setTypeFace() {
        Typeface typeface = State.getSingleton().getRobotoTypeface();
        _takeAgain.setTypeface(typeface);
        _takeAgainSummary.setTypeface(typeface);
        _quantityText.setTypeface(typeface);
        _quantity.setTypeface(typeface);
        _quantitySummary.setTypeface(typeface);
        _delete.setTypeface(typeface);
        _deleteSummary.setTypeface(typeface);
    }
}
