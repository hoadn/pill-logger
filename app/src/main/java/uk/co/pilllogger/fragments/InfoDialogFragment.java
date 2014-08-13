package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;


/**
 * Created by Nick on 05/03/14.
 */
public abstract class InfoDialogFragment extends PillLoggerFragmentBase{

    String _title;
    Pill _pill;

    public InfoDialogFragment(){
    }

    public InfoDialogFragment(Pill pill) {
        if(pill != null) {
            _title = pill.getName() + " " + pill.getFormattedSize() + pill.getUnits();
            _pill = pill;
        }
    }

    protected abstract int getLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity activity = getActivity();
        if(activity == null)
            return null;

        return inflater.inflate(getLayoutId(), container, false);
    }
}

