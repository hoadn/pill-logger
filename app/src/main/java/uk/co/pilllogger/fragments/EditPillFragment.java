package uk.co.pilllogger.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.jobs.UpdatePillJob;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 19/06/14.
 */
public class EditPillFragment extends PillLoggerFragmentBase {

    private Pill _pill;
    @Inject JobManager _jobManager;

    public EditPillFragment(){

    }

    @SuppressLint("ValidFragment")
    public EditPillFragment(Pill pill) {
        _pill = pill;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_pill, container, false);
        final EditText editPillName = (EditText) (view != null ? view.findViewById(R.id.edit_pill_name) : null);
        final EditText editPillSize = (EditText) (view != null ? view.findViewById(R.id.edit_pill_size) : null);
        final ImageView editPillFavourite = (ImageView) (view != null ? view.findViewById(R.id.edit_pill_favourite) : null);
        final Spinner spinner = (Spinner) (view != null ? view.findViewById(R.id.units_spinner) : null);
        final ViewGroup colourContainer = (ViewGroup) (view != null ? view.findViewById(R.id.colour_container) : null);

        Typeface typeface = State.getSingleton().getRobotoTypeface();
        if (editPillName != null) {
            editPillName.setTypeface(typeface);
        }
        if (editPillSize != null) {
            editPillSize.setTypeface(typeface);
        }

        final Activity activity = getActivity();

        if (activity == null) {
            return null;
        }

        if (_pill == null) {
            activity.getFragmentManager().popBackStack();
            return null;
        }

        if (editPillName != null) {
            editPillName.setText(_pill.getName());
        }
        if (editPillSize != null) {
            editPillSize.setText(NumberHelper.getNiceFloatString(_pill.getSize()));
        }

        String[] units = activity.getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(activity, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getSpinnerSelection());

        if(editPillFavourite != null){
            editPillFavourite.setImageResource(_pill.isFavourite() ? R.drawable.star : R.drawable.star_empty);

            editPillFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _pill.setFavourite(!_pill.isFavourite());
                    editPillFavourite.setImageResource(_pill.isFavourite() ? R.drawable.star : R.drawable.star_empty);
                }
            });
        }

        View doneLayout = view.findViewById(R.id.export_pills_list_layout);
        doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _pill.setName(editPillName.getText().toString());

                String pillSize = editPillSize.getText().toString();
                float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
                _pill.setSize(size);
                _pill.setUnits(spinner.getSelectedItem().toString());
                InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(editPillName.getWindowToken(), 0);

                int delayMillis = getResources().getInteger(R.integer.slide_duration);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _jobManager.addJobInBackground(new UpdatePillJob(_pill));
                    }
                }, delayMillis);

                activity.getFragmentManager().popBackStack();
            }
        });

        setupColourIndicators(colourContainer);

        return view;
    }

    private void setupColourIndicators(final ViewGroup colourContainer) {
        if(colourContainer != null) {
            for (int i = 0; i < colourContainer.getChildCount(); i++) {
                ColourIndicator ci = (ColourIndicator) colourContainer.getChildAt(i);

                if (ci == null)
                    continue;

                ci.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int colour = ((ColourIndicator) view).getColour();
                        _pill.setColour(colour);
                        setupColourIndicators(colourContainer);
                    }
                });
                int colour = ci.getColour();

                ci.setColour(colour, false, colour == _pill.getColour());
            }
        }
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
