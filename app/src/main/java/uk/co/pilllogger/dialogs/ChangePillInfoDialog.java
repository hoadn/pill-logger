package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.DeletePillTask;
import uk.co.pilllogger.tasks.UpdatePillTask;

/**
 * Created by Nick on 24/04/2014.
 */
public class ChangePillInfoDialog extends DialogFragment {

    private Pill _pill;
    private Activity _activity;
    private ChangePillInfoDialogListener _listener;

    public ChangePillInfoDialog() {
    }

    public ChangePillInfoDialog(Activity activity, Pill pill, ChangePillInfoDialogListener listener) {
        _activity = activity;
        _pill = pill;
        _listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        if(activity == null)
            return null;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_pill_dialog, null);
        final TextView editPillTitle = (TextView) dialogView.findViewById(R.id.edit_pill_title);
        final TextView editPillInformation = (TextView) dialogView.findViewById(R.id.edit_pill_information);
        final EditText editPillName = (EditText) dialogView.findViewById(R.id.edit_pill_name);
        final EditText editPillSize = (EditText) dialogView.findViewById(R.id.edit_pill_size);
        final TextView editPillNameLabel = (TextView) dialogView.findViewById(R.id.edit_pill_name_label);
        final TextView editPillSizeLabel = (TextView) dialogView.findViewById(R.id.edit_pill_size_label);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.units_spinner);

        Typeface typeface = State.getSingleton().getTypeface();
        editPillName.setTypeface(typeface);
        editPillSize.setTypeface(typeface);
        editPillNameLabel.setTypeface(typeface);
        editPillSizeLabel.setTypeface(typeface);
        editPillTitle.setTypeface(typeface);
        editPillInformation.setTypeface(typeface);

        if (_pill == null) {
            dismiss();
            return builder.create();
        }

        editPillName.setText(_pill.getName());
        editPillSize.setText(NumberHelper.getNiceFloatString(_pill.getSize()));

        String[] units = getActivity().getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(getActivity(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getSpinnerSelection());

        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _pill.setName(editPillName.getText().toString());

                String pillSize = editPillSize.getText().toString();
                float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
                _pill.setSize(size);
                _pill.setUnits(spinner.getSelectedItem().toString());
                _listener.onDialogInfomationChanged(_pill, ChangePillInfoDialog.this);
                InputMethodManager im = (InputMethodManager)_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPillName.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager im = (InputMethodManager)_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPillName.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private int getSpinnerSelection() {
        String units = _pill.getUnits();
        if (units.equals("ml"))
            return 1;
        else if (units.equals("mcg"))
            return 2;
        else
            return 0;
    }

    @Override
    public void dismiss() {

        super.dismiss();
    }

    public interface ChangePillInfoDialogListener {
        public void onDialogInfomationChanged(Pill pill, ChangePillInfoDialog dialog);
    }
}
