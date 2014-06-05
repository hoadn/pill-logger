package uk.co.pilllogger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.adapters.DosageListExportAdapter;
import uk.co.pilllogger.models.Pill;

/**
 * Created by nick on 05/06/14.
 */
public class ExportSelectDosageFragment extends PillLoggerFragmentBase {

    ListView _dosageList;
    List<String> _usedDosages = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_select_dosage, container, false);

        if (view != null) {
            _dosageList = (ListView)view.findViewById(R.id.export_dosage_list);
            List<Pill> pills = ((ExportActivity)getActivity()).getPillsList();
            if (pills != null && pills.size() > 0) {
                for (Pill pill : pills) {
                    if (!_usedDosages.contains(pill.getUnits()))
                        _usedDosages.add(pill.getUnits());
                }
            }
            if (_dosageList != null)
                _dosageList.setAdapter(new DosageListExportAdapter(getActivity(), R.layout.export_dosage_item, _usedDosages, pills));

            View doneLayout = view.findViewById(R.id.export_dosage_done_layout);
            doneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getFragmentManager().popBackStack();
                }
            });
        }
        return view;
    }
}
