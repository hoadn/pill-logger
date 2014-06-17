package uk.co.pilllogger.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class ConsumptionInfoDialog extends InfoDialog {
    private Context _context;
    private Consumption _consumption;

    public ConsumptionInfoDialog(){
        super();
    }

    @SuppressLint("ValidFragment")
    public ConsumptionInfoDialog(Context context, Consumption consumption) {
        super(consumption.getPill());
        _context = context;
        _consumption = consumption;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.consumption_info_dialog;
    }

    @Override
    protected void setupMenu(View view) {

        TextView takeAgain = (TextView) view.findViewById(R.id.consumption_info_dialog_take);
        TextView quantityText = (TextView) view.findViewById(R.id.consumption_info_quantity_text);
        TextView quantity = (TextView) view.findViewById(R.id.consumption_info_quantity);
        View increase = view.findViewById(R.id.consumption_info_dialog_increase);
        ImageView decrease = (ImageView) view.findViewById(R.id.consumption_info_dialog_decrease);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);

        Typeface typeface = State.getSingleton().getTypeface();
        takeAgain.setTypeface(typeface);
        quantityText.setTypeface(typeface);
        quantity.setTypeface(typeface);
        delete.setTypeface(typeface);

        if(_consumption == null) {
            getActivity().finish();
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
                Observer.getSingleton().notifyOnConsumptionDialogTakeAgain(_consumption, ConsumptionInfoDialog.this);
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogIncrease(_consumption, ConsumptionInfoDialog.this);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogDecrease(_consumption, ConsumptionInfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observer.getSingleton().notifyOnConsumptionDialogDelete(_consumption, ConsumptionInfoDialog.this);
            }
        });
    }

    public interface ConsumptionInfoDialogListener {
        public void onDialogTakeAgain(Consumption consumption, InfoDialog dialog);
        public void onDialogIncrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDecrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDelete(Consumption consumption, InfoDialog dialog);
    }
}
