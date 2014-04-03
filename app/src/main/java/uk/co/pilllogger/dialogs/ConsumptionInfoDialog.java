package uk.co.pilllogger.dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class ConsumptionInfoDialog extends InfoDialog {
    private Context _context;
    private Consumption _consumption;
    private ConsumptionInfoDialogListener _listener;


    public ConsumptionInfoDialog(){
        super();
    }

    public ConsumptionInfoDialog(Context context, Consumption consumption, ConsumptionInfoDialogListener listener) {
        super(consumption.getPill());
        _context = context;
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

        Typeface typeface = State.getSingleton().getTypeface();
        takeAgain.setTypeface(typeface);
        increase.setTypeface(typeface);
        decrease.setTypeface(typeface);
        delete.setTypeface(typeface);

        if(_consumption == null) {
            dismiss();
            return;
        }

        takeAgain.setText(String.format("%s %d %s %s", _context.getString(R.string.consumption_info_dialog_take_again_prefix), _consumption.getQuantity(), _consumption.getPill().getName(), _context.getString(R.string.consumption_info_dialog_take_again_suffix)));

        if(_consumption.getQuantity() > 1){
            decrease.setClickable(true);
            decrease.setEnabled(true);
            decrease.setTextColor(_context.getResources().getColor(R.color.text_grey));
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
    }

    public interface ConsumptionInfoDialogListener {
        public void onDialogTakeAgain(Consumption consumption, InfoDialog dialog);
        public void onDialogIncrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDecrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDelete(Consumption consumption, InfoDialog dialog);
    }
}
