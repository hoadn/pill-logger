package uk.co.cntwo.pilllogger.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.tasks.GetConsumptionsTask;
import uk.co.cntwo.pilllogger.tasks.InitTestDbTask;

/**
 * Created by nick on 23/10/13.
 */
public class MainFragment extends Fragment implements InitTestDbTask.ITaskComplete, GetConsumptionsTask.ITaskComplete {

    ListView _listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        if(_listView == null){
        //Doing this to test - will not be needed when working fully
            new InitTestDbTask(this.getActivity(), this).execute();

            _listView = (ListView) (v != null ? v.findViewById(R.id.main_consumption_list) : null);
        }

        return v;
    }

    @Override
    public void initComplete() {
        new GetConsumptionsTask(this.getActivity(), this).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if(consumptions != null && consumptions.size() > 0)
            _listView.setAdapter(new ConsumptionListAdapter(getActivity(), R.layout.pill_list_item, consumptions));
    }
}
