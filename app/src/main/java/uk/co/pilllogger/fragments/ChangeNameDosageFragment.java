package uk.co.pilllogger.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.dialogs.ChangePillInfoDialog;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.UpdatePillTask;

/**
 * Created by nick on 19/06/14.
 */
public class ChangeNameDosageFragment extends PillLoggerFragmentBase {

    private Pill _pill;
    private ChangePillInfoDialog.ChangePillInfoDialogListener _listener;

    public ChangeNameDosageFragment() {

    }
    public ChangeNameDosageFragment(Pill pill, ChangePillInfoDialog.ChangePillInfoDialogListener listener) {
        _pill = pill;
        _listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_pill_info, null);
        final EditText editPillName = (EditText) view.findViewById(R.id.edit_pill_name);
        final EditText editPillSize = (EditText) view.findViewById(R.id.edit_pill_size);
        final TextView editPillNameLabel = (TextView) view.findViewById(R.id.edit_pill_name_label);
        final TextView editPillSizeLabel = (TextView) view.findViewById(R.id.edit_pill_size_label);
        final Spinner spinner = (Spinner) view.findViewById(R.id.units_spinner);

        Typeface typeface = State.getSingleton().getTypeface();
        editPillName.setTypeface(typeface);
        editPillSize.setTypeface(typeface);
        editPillNameLabel.setTypeface(typeface);
        editPillSizeLabel.setTypeface(typeface);

        if (_pill == null) {
            getActivity().getFragmentManager().popBackStack();
        }

        editPillName.setText(_pill.getName());
        editPillSize.setText(NumberHelper.getNiceFloatString(_pill.getSize()));

        String[] units = getActivity().getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(getActivity(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getSpinnerSelection());

        View doneLayout = view.findViewById(R.id.export_pills_list_layout);
        doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
                _pill.setName(editPillName.getText().toString());

                String pillSize = editPillSize.getText().toString();
                float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
                _pill.setSize(size);
                _pill.setUnits(spinner.getSelectedItem().toString());
                _listener.onDialogInfomationChanged(_pill);
                InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPillName.getWindowToken(), 0);
            }
        });

        return view;
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
}
