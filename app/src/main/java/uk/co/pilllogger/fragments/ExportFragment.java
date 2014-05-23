package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 22/05/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportFragment extends PillLoggerFragmentBase {

    private Button _pillSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        if(view != null){
            _pillSelector = (Button) view.findViewById(R.id.export_select_pills);
            _pillSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExportSelectPillsFragment selectPillsFragment = new ExportSelectPillsFragment();
                    FragmentManager fm = ExportFragment.this.getActivity().getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_left,
                            R.anim.slide_out, 0, 0);
                    transaction.replace(R.id.export_container, selectPillsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

        }

        return view;
    }



}
