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
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class ConsumptionInfoDialogFragment extends InfoDialogFragment {
    private Context _context;
    private Consumption _consumption;

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

        TextView takeAgain = (TextView) activity.findViewById(R.id.consumption_info_dialog_take);
        TextView quantityText = (TextView) activity.findViewById(R.id.consumption_info_quantity_text);
        TextView quantity = (TextView) activity.findViewById(R.id.consumption_info_quantity);
        View increase = activity.findViewById(R.id.consumption_info_dialog_increase);
        ImageView decrease = (ImageView) activity.findViewById(R.id.consumption_info_dialog_decrease);
        TextView delete = (TextView) activity.findViewById(R.id.info_dialog_delete);

        Typeface typeface = State.getSingleton().getTypeface();
        takeAgain.setTypeface(typeface);
        quantityText.setTypeface(typeface);
        quantity.setTypeface(typeface);
        delete.setTypeface(typeface);

        if(_consumption == null) {
            activity.finish();
            return;
        }

        quantity.setText("" + _consumption.getQuantity());

        String takeAgainText =  _context.getString(R.string.consumption_info_dialog_take_again_prefix);
        if(_consumption.getQuantity() > 1)
            takeAgainText += " " + _consumption.getQuantity();
        takeAgainText += String.format(" %s %s", _consumption.getPill().getName(), _context.getString(R.string.consumption_info_dialog_take_again_suffix));
        takeAgain.setText(takeAgainText);

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

        takeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogTakeAgain(_consumption, ConsumptionInfoDialogFragment.this);
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogIncrease(_consumption, ConsumptionInfoDialogFragment.this);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogDecrease(_consumption, ConsumptionInfoDialogFragment.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogDelete(_consumption, ConsumptionInfoDialogFragment.this);
            }
        });
    }

    public interface ConsumptionInfoDialogListener {
        public void onDialogTakeAgain(Consumption consumption, InfoDialogFragment dialog);
        public void onDialogIncrease(Consumption consumption, InfoDialogFragment dialog);
        public void onDialogDecrease(Consumption consumption, InfoDialogFragment dialog);
        public void onDialogDelete(Consumption consumption, InfoDialogFragment dialog);
    }
}
