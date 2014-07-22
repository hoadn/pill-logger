package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.PillsListExportAdapter;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by nick on 23/05/14.
 */
public class ExportSelectPillsFragment extends ExportFragmentBase {

    ListView _pillsList;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(_pillsList == null || _pillsList.getAdapter() == null){
            return;
        }

        try {
            _bus.unregister(_pillsList.getAdapter());
        }
        catch(IllegalArgumentException ignored){} // if this throws, we're not registered anyway
    }

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
                new GetPillsTask(getActivity()).execute();
        }

        _exportService.getPillSummary(_exportService.getSummaryTextView());

        return view;
    }

    private void setUpPillsListAdapter(List<Pill> pills) {
        if (_pillsList != null)
            _pillsList.setAdapter(new PillsListExportAdapter(getActivity(), R.layout.export_pills_list_item, pills, _exportService));
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event) {
        _exportService.getExportSettings().getSelectedPills().addAll(event.getPills());
        setUpPillsListAdapter(event.getPills());
    }
}
