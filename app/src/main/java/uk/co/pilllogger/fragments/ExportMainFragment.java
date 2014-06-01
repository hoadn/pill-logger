package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportMainFragment extends PillLoggerFragmentBase {
    private Button _pillSelector;
    private ExportSelectPillsFragment _selectPillsFragment;
    List<Pill> _pills = new ArrayList<Pill>();
    Set<Pill> _selectedPills = new HashSet<Pill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_main, container, false);

        if(view != null){
            _pillSelector = (Button) view.findViewById(R.id.export_select_pills);

            _pillSelector.setTypeface(State.getSingleton().getTypeface());

            _pillSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _selectPillsFragment = new ExportSelectPillsFragment();
                    FragmentManager fm = ExportMainFragment.this.getActivity().getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(R.id.export_container, _selectPillsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(_selectPillsFragment == null)
            return;

        Set<Pill> selectedPills = _selectPillsFragment.getSelectedPills();

        _selectedPills = selectedPills;

        if(_pillSelector != null){
            setPillButtonText(getActivity());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        if(activity == null)
            return;

        new GetPillsTask(activity, new GetPillsTask.ITaskComplete() {
            @Override
            public void pillsReceived(List<Pill> pills) {
                _pills = pills;
                setPillButtonText(activity);
            }
        }).execute();
    }

    private void setPillButtonText(Context context){
        if(_pillSelector != null && context != null){
            String text = context.getString(R.string.export_select_medicine);
            _pillSelector.setText(text + " (" + _selectedPills.size() + "/" + _pills.size() + ")");
        }
    }
}
