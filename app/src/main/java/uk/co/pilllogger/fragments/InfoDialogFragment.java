package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.PillLoggerFragmentBase;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


/**
 * Created by Nick on 05/03/14.
 */
public abstract class InfoDialogFragment extends PillLoggerFragmentBase{

    String _title;
    Pill _pill;

    public InfoDialogFragment(){
    }

    public InfoDialogFragment(Pill pill) {
        _title = pill.getName() + " " + pill.getFormattedSize() + pill.getUnits();
        _pill = pill;
    }

    protected abstract int getLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity activity = getActivity();
        if(activity == null)
            return null;

        View view = inflater.inflate(getLayoutId(), null);

        if(view == null)
            return null;

        return view;
    }
}