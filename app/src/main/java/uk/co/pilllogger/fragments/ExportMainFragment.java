package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportMainFragment extends PillLoggerFragmentBase {
    private View _pillSelector, _dosageSelector, _dateSelector;
    private TextView _pillSelectorText, _dosageSelectorText, _dateSelectorText;
    private ExportSelectPillsFragment _selectPillsFragment;
    private ExportSelectDateFragment _selectDateFragment;
    List<Pill> _pills = new ArrayList<Pill>();
    Set<Pill> _selectedPills = new HashSet<Pill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_main, container, false);

        if(view != null){
            _pillSelector = view.findViewById(R.id.export_select_pills);
            _dosageSelector = view.findViewById(R.id.export_dosage_options);
            _dateSelector = view.findViewById(R.id.export_date_range);
            _pillSelectorText = (TextView) view.findViewById(R.id.export_select_pills_text);
            _dosageSelectorText = (TextView) view.findViewById(R.id.export_dosage_options_text);
            _dateSelectorText = (TextView) view.findViewById(R.id.export_date_range_text);
            _pillSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());
            _dateSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());
            _dosageSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());


            TextView unlock = (TextView)view.findViewById(R.id.export_unlock);
            unlock.setTypeface(State.getSingleton().getRobotoTypeface());

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

            _dateSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _selectDateFragment = new ExportSelectDateFragment();
                    FragmentManager fm = ExportMainFragment.this.getActivity().getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(R.id.export_container, _selectDateFragment)
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

        if(_selectPillsFragment != null) {
            Set<Pill> selectedPills = _selectPillsFragment.getSelectedPills();
            _selectedPills = selectedPills;
            ((ExportActivity) getActivity()).setSelectedPills(selectedPills);
            if (_pillSelector != null) {
                setPillButtonText(getActivity());
            }
        }
        if(_selectDateFragment != null) {
            ((ExportActivity) getActivity()).setStartDate(_selectDateFragment.getStartDate());
            ((ExportActivity) getActivity()).setEndDate(_selectDateFragment.getEndDate());
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
            _pillSelectorText.setText(text + " (" + _selectedPills.size() + "/" + _pills.size() + ")");
        }
    }
}
