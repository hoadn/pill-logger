package uk.co.pilllogger.dialogs;

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
    private Consumption _consumption;
    private ConsumptionInfoDialogListener _listener;

    public ConsumptionInfoDialog(Consumption consumption, ConsumptionInfoDialogListener listener) {
        super(consumption.getPill());
        _consumption = consumption;
        _listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.consumption_info_dialog;
    }

    @Override
    protected void setupMenu(View view) {

        TextView increase = (TextView) view.findViewById(R.id.consumption_info_dialog_increase);
        TextView decrease = (TextView) view.findViewById(R.id.consumption_info_dialog_decrease);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);

        Typeface typeface = State.getSingleton().getTypeface();
        increase.setTypeface(typeface);
        decrease.setTypeface(typeface);
        delete.setTypeface(typeface);

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
        public void onDialogIncrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDecrease(Consumption consumption, InfoDialog dialog);
        public void onDialogDelete(Consumption consumption, InfoDialog dialog);
    }
}
