package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.adapters.PillsListBaseAdapter;
import uk.co.pilllogger.adapters.PillsListExportAdapter;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by nick on 23/05/14.
 */
public class ExportSelectPillsFragment extends ExportFragmentBase implements GetPillsTask.ITaskComplete {

    ListView _pillsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_export_select_pills, container, false);

        if(view != null){
            _pillsList = (ListView) view.findViewById(R.id.export_pills_list);
            _pillsList.setItemsCanFocus(false);
            _pillsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            TextView done = (TextView)view.findViewById(R.id.export_pills_done);
            done.setTypeface(State.getSingleton().getRobotoTypeface());
            final Activity activity = getActivity();
            View doneLayout = view.findViewById(R.id.export_pills_list_layout);
            doneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.getFragmentManager().popBackStack();
                }
            });
            List<Pill> pills = _exportService.getAllPills();
            if (pills != null)
                setUpPillsListAdapter(pills);
            else
                new GetPillsTask(getActivity(), this).execute();
        }

        _exportService.getPillSummary(_exportService.getSummaryTextView());

        return view;
    }

    private void setUpPillsListAdapter(List<Pill> pills) {
        if (_pillsList != null)
            _pillsList.setAdapter(new PillsListExportAdapter(getActivity(), R.layout.export_pills_list_item, pills, _exportService));
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        _exportService.getExportSettings().getSelectedPills().addAll(pills);
        setUpPillsListAdapter(pills);
    }
}