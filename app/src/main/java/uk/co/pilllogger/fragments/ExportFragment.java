package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.AlertDialog;
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

        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        new GetPillsTask(activity, new GetPillsTask.ITaskComplete() {
            @Override
            public void pillsReceived(List<Pill> pills) {

                final List<String> pillNames = new ArrayList<String>();

                for (Pill pill : pills) {
                    pillNames.add(pill.getName() + " " + pill.getFormattedSize() + pill.getUnits());
                }
                _pillSelector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder( activity )
                                .setTitle("Pills")
                                .setMultiChoiceItems(pillNames.toArray(new String[pillNames.size()]), new boolean[]{false, false, false, false, true}, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create()
                                .show();
                    }
                });
            }
        }).execute();
    }
}
