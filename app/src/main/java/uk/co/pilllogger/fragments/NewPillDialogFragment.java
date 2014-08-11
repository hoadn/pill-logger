package uk.co.pilllogger.fragments;

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
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.UnitAdapter;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.InsertPillTask;
import uk.co.pilllogger.views.ColourIndicator;

public class NewPillDialogFragment extends PillLoggerFragmentBase {

    @InjectView(R.id.new_pill_name)
    EditText _newPillName;

    @InjectView(R.id.new_pill_done)
    TextView _newPillDone;

    @InjectView(R.id.new_pill_size)
    EditText _newPillSize;

    @InjectView(R.id.new_pill_favourite)
    ImageView _newPillFavourite;

    @InjectView(R.id.units_spinner)
    Spinner _unitsSpinner;

    @InjectView(R.id.colour_container)
    ViewGroup _colourContainer;

    @InjectView(R.id.new_pill_title)
    TextView _newPillTitle;

    Pill _newPill = new Pill();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Activity activity = getActivity();

        if(activity == null){
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_new_pill, container, false);

        ButterKnife.inject(this, view);

        setTypeface();
        setupColourIndicators();
        setupUnitsSpinner(activity);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    private void setupColourIndicators() {
        for (int i = 0; i < _colourContainer.getChildCount(); i++) {
            ColourIndicator ci = (ColourIndicator) _colourContainer.getChildAt(i);

            if (ci == null)
                continue;

            ci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int colour = ((ColourIndicator) view).getColour();
                    _newPill.setColour(colour);
                    setupColourIndicators();
                }
            });
            int colour = ci.getColour();

            ci.setColour(colour, false, colour == _newPill.getColour());
        }
    }

    private void setupUnitsSpinner(Activity activity) {
        String[] units = activity.getResources().getStringArray(R.array.units_array);
        UnitAdapter adapter = new UnitAdapter(activity, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _unitsSpinner.setAdapter(adapter);
    }

    private void setTypeface() {
        Typeface tf = State.getSingleton().getRobotoTypeface();

        _newPillName.setTypeface(tf);
        _newPillDone.setTypeface(tf);
        _newPillSize.setTypeface(tf);
        _newPillTitle.setTypeface(tf);
    }

    @OnClick(R.id.new_pill_favourite)
    public void toggleFavourite(){
        _newPill.setFavourite(!_newPill.isFavourite());

        int drawable = _newPill.isFavourite() ? R.drawable.star : R.drawable.star_empty;

        _newPillFavourite.setImageDrawable(getResources().getDrawable(drawable));
    }

    @OnClick(R.id.new_pill_done)
    public void submit(){
        _newPill.setName(String.valueOf(_newPillName.getText()));
        String pillSize = _newPillSize.getText().toString();
        float size = pillSize.trim().length() > 0 ? Float.valueOf(pillSize) : 0;
        _newPill.setSize(size);
        _newPill.setUnits(_unitsSpinner.getSelectedItem().toString());
        Activity activity = getActivity();
        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(_newPillName.getWindowToken(), 0);

        if (_newPill.getName().equals("") == false) {
            new InsertPillTask(activity, _newPill).execute();
        }

        activity.finish();
    }
}
