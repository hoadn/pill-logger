package uk.co.pilllogger.dialogs;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 05/03/14.
 */
public class PillInfoDialog extends InfoDialog {
    private PillInfoDialogListener _listener;

    public PillInfoDialog(){
        super();
    }

    public PillInfoDialog(Pill pill, PillInfoDialogListener listener) {
        super(pill);
        _listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pill_info_dialog;
    }

    @Override
    protected void setupMenu(View view) {

        TextView addConsumption = (TextView) view.findViewById(R.id.info_dialog_add_consumption);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);

        Typeface typeface = State.getSingleton().getTypeface();
        addConsumption.setTypeface(typeface);
        delete.setTypeface(typeface);

        if(_pill == null) {
            dismiss();
            return;
        }

        addConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogAddConsumption(_pill, PillInfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogDelete(_pill, PillInfoDialog.this);
            }
        });
    }

    public interface PillInfoDialogListener {
        public void onDialogAddConsumption(Pill pill, InfoDialog dialog);
        public void onDialogDelete(Pill pill, InfoDialog dialog);
    }
}
