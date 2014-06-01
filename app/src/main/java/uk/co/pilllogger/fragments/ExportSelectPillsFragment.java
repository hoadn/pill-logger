package uk.co.pilllogger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillsListBaseAdapter;
import uk.co.pilllogger.adapters.PillsListExportAdapter;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by nick on 23/05/14.
 */
public class ExportSelectPillsFragment extends PillLoggerFragmentBase implements GetPillsTask.ITaskComplete {

    ListView _pillsList;
    Set<Pill> _selectedPills = new HashSet<Pill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_select_pills, container, false);

        if(view != null){
            _pillsList = (ListView) view.findViewById(R.id.export_pills_list);
            _pillsList.setItemsCanFocus(false);
            _pillsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            new GetPillsTask(getActivity(), this).execute();
        }

        return view;
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        if (_pillsList != null)
            _pillsList.setAdapter(new PillsListExportAdapter(getActivity(), R.layout.pill_list_item, pills));
    }
}
