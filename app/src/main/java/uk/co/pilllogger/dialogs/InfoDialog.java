package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


/**
 * Created by Nick on 05/03/14.
 */
public abstract class InfoDialog extends DialogFragment {

    String _title;
    Pill _pill;
    List<Consumption> _consumptions;

    public InfoDialog(){
    }

    public InfoDialog(Pill pill) {
        _title = pill.getName() + " " + pill.getFormattedSize() + pill.getUnits();
        _pill = pill;
    }

    protected abstract int getLayoutId();
    protected abstract void setupMenu(View view);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        if(activity == null)
            return null;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(getLayoutId(), null);

        if(view == null)
            return null;

        ColourIndicator colour = (ColourIndicator)view.findViewById(R.id.colour);
        TextView title = (TextView) view.findViewById(R.id.info_dialog_title);
        TextView lastTaken = (TextView) view.findViewById(R.id.info_dialog_last_taken);
        TextView dosage = (TextView) view.findViewById(R.id.info_dialog_dosage);

        Typeface typeface = State.getSingleton().getTypeface();
        title.setTypeface(typeface);
        lastTaken.setTypeface(typeface);
        dosage.setTypeface(typeface);

        title.setText(_title);

        if(_pill != null) {
            colour.setColour(_pill.getColour());

            Consumption lastConsumption = _pill.getLatestConsumption();
            if (lastConsumption != null) {
                String lastTakenText = lastTaken.getText() + " " + DateHelper.formatDateAndTime(activity, lastConsumption.getDate());
                lastTakenText += " " + DateHelper.getTime(activity, lastConsumption.getDate());
                lastTaken.setText(lastTakenText);
            }
            _consumptions = _pill.getConsumptions();
            String dosage24 = NumberHelper.getNiceFloatString(_pill.getTotalSize(24));
            int quantity24 = _pill.getTotalQuantity(24);

            String dosageText = dosage.getText().toString();
            if(_pill.getSize() > 0)
                dosageText += " " + dosage24 + _pill.getUnits() + " (" + quantity24 + " x " + _pill.getFormattedSize() + _pill.getUnits() + ")";
            else
                dosageText += " " + quantity24;
            dosage.setText(dosageText);
        }
        setupMenu(view);

        builder.setView(view);
        return builder.create();
    }
}