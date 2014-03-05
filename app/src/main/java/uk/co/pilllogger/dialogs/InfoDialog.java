package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;


/**
 * Created by Nick on 05/03/14.
 */
public class InfoDialog extends DialogFragment {

    String _title;
    Pill _pill;
    List<Consumption> _consumpions;
    InfoDialogListener _listener;

    public InfoDialog(Pill pill, InfoDialogListener listener) {
        _title = pill.getName() + " " + pill.getSize() + pill.getUnits();
        _pill = pill;
        _listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog, null);

        TextView title = (TextView) view.findViewById(R.id.info_dialog_title);
        TextView addConsumption = (TextView) view.findViewById(R.id.info_dialog_add_consumption);
        TextView delete = (TextView) view.findViewById(R.id.info_dialog_delete);
        TextView lastTaken = (TextView) view.findViewById(R.id.info_dialog_last_taken);
        TextView dosage = (TextView) view.findViewById(R.id.info_dialog_dosage);

        Typeface typeface = State.getSingleton().getTypeface();
        title.setTypeface(typeface);
        addConsumption.setTypeface(typeface);
        delete.setTypeface(typeface);
        lastTaken.setTypeface(typeface);
        dosage.setTypeface(typeface);

        title.setText(_title);
        Consumption lastConsumption = _pill.getLatestConsumption();
        if (lastConsumption != null)
            lastTaken.setText(lastTaken.getText() + " " + DateHelper.formatDateAndTime(_pill.getLatestConsumption().getDate()));
        _consumpions = _pill.getConsumptions();
        int dosage24 = 0;
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.HOUR, -24);
        Date oneDayBack = cal.getTime();
        for (Consumption consumption : _consumpions) {
            Date consumptionDate = consumption.getDate();
            if (consumptionDate.compareTo(oneDayBack) >= 0 && consumptionDate.compareTo(currentDate) <= 0) {
                dosage24 += (consumption.getQuantity() * _pill.getSize());
            }
        }
        dosage.setText(dosage.getText() + " " + dosage24 + _pill.getUnits());

        addConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogAddConsumption(_pill, InfoDialog.this);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onDialogDelete(_pill, InfoDialog.this);
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public interface InfoDialogListener {
        public void onDialogAddConsumption(Pill pill, InfoDialog dialog);
        public void onDialogDelete(Pill pill, InfoDialog dialog);
    }
}