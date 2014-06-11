package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportMainFragment extends ExportFragmentBase {
    private ExportSelectPillsFragment _selectPillsFragment;
    private ExportSelectDateFragment _selectDateFragment;
    private ExportSelectDosageFragment _selectDosageFragment;
    private ExportSelectTimeFragment _selectTimeFragment;

    List<Pill> _pills = new ArrayList<Pill>();
    private TextView _pillSummary;
    private TextView _dosageSummary;
    private TextView _dateSummary;
    private TextView _timeSummary;

    private View _finishedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_finishedView != null)
            return _finishedView;
        View view = inflater.inflate(R.layout.fragment_export_main, container, false);

        if(view != null){
            View pillSelector = view.findViewById(R.id.export_select_pills);
            View dosageSelector = view.findViewById(R.id.export_dosage_options);
            View dateSelector = view.findViewById(R.id.export_date_range);
            View timeSelector = view.findViewById(R.id.export_time_range);

            TextView pillSelectorText = (TextView) view.findViewById(R.id.export_select_pills_title);
            TextView dosageSelectorText = (TextView) view.findViewById(R.id.export_select_dosage_title);
            TextView dateSelectorText = (TextView) view.findViewById(R.id.export_select_dates_title);
            TextView timeSelectorText = (TextView) view.findViewById(R.id.export_select_times_title);

            _pillSummary = (TextView) view.findViewById(R.id.export_select_pills_summary);
            _dosageSummary = (TextView) view.findViewById(R.id.export_select_dosage_summary);
            _dateSummary = (TextView) view.findViewById(R.id.export_select_dates_summary);
            _timeSummary = (TextView) view.findViewById(R.id.export_select_times_summary);

            pillSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());
            dateSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());
            dosageSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());
            timeSelectorText.setTypeface(State.getSingleton().getRobotoTypeface());

            _pillSummary.setTypeface(State.getSingleton().getRobotoTypeface());
            _dosageSummary.setTypeface(State.getSingleton().getRobotoTypeface());
            _dateSummary.setTypeface(State.getSingleton().getRobotoTypeface());
            _timeSummary.setTypeface(State.getSingleton().getRobotoTypeface());

            TextView unlock = (TextView)view.findViewById(R.id.export_unlock);
            unlock.setTypeface(State.getSingleton().getRobotoTypeface());

            pillSelector.setOnClickListener(new View.OnClickListener() {
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

            dateSelector.setOnClickListener(new View.OnClickListener() {
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

            timeSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _selectTimeFragment = new ExportSelectTimeFragment();
                    FragmentManager fm = ExportMainFragment.this.getActivity().getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(R.id.export_container, _selectTimeFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            dosageSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _selectDosageFragment = new ExportSelectDosageFragment();
                    FragmentManager fm = ExportMainFragment.this.getActivity().getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(R.id.export_container, _selectDosageFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        _finishedView = view;
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        updatePillSummary(getActivity());
        updateTimeSummary(getActivity());
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
                updatePillSummary(activity);
            }
        }).execute();
    }

    private void updatePillSummary(Context context){
        if (context == null) {
            return;
        }

        String prefix = "All";

        int currentlySelectedPills = _exportService.getExportSettings().getSelectedPills().size();

        if(currentlySelectedPills == 0){
            _pillSummary.setText("You must select at least 1 medicine");
            _pillSummary.setTextColor(context.getResources().getColor(R.color.warning_red));
            return;
        }
        _pillSummary.setTextColor(context.getResources().getColor(R.color.text_grey_medium));

        if(currentlySelectedPills != _pills.size())
        prefix = currentlySelectedPills + " of";

        String text = prefix;

        if(currentlySelectedPills == 2 && _pills.size() == 2)
            text = "Both";

        if(_pills.size() > 2 || currentlySelectedPills != _pills.size())
            text += " " + _pills.size();

        text += " medicine";
        if(_pills.size() > 1 || currentlySelectedPills == _pills.size())
            text += "s";

        text += " selected";
        _pillSummary.setText(text);
    }

    private void updateTimeSummary(Context context){
        if(context == null) {
            return;
        }

        String text = "Any time of the day";

        if(_exportService.getExportSettings().getStartTime() != null
                && _exportService.getExportSettings().getEndTime() != null){

            String startTimeString = DateHelper.getTime(context, _exportService.getExportSettings().getStartTime().toDateTimeToday());
            String endTimeString = DateHelper.getTime(context, _exportService.getExportSettings().getEndTime().toDateTimeToday());

            text = startTimeString + " - " + endTimeString;
        }
        else {
            if (_exportService.getExportSettings().getEndTime() != null) {
                String endTimeString = DateHelper.getTime(context, _exportService.getExportSettings().getEndTime().toDateTimeToday());
                text = "Before " + endTimeString;
            }
            else{
                if(_exportService.getExportSettings().getStartTime() != null){
                    String startTimeString = DateHelper.getTime(context, _exportService.getExportSettings().getStartTime().toDateTimeToday());
                    text = "After " + startTimeString;
                }
            }
        }

        _timeSummary.setText(text);
    }
}
