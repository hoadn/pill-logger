package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.billing.IabHelper;
import uk.co.pilllogger.billing.IabResult;
import uk.co.pilllogger.billing.Purchase;
import uk.co.pilllogger.billing.SkuDetails;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.PurchasedFeatureEvent;
import uk.co.pilllogger.helpers.ExportHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.LoadPillsJob;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.FeatureType;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.fragments.
 */
public class ExportMainFragment extends ExportFragmentBase {
    private static final String TAG = "ExportMainFragment";
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
    private TextView _unlock;
    @Inject JobManager _jobManager;

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

            _unlock = (TextView)view.findViewById(R.id.export_unlock);
            _unlock.setTypeface(State.getSingleton().getRobotoTypeface());

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
        View exportFinished = view.findViewById(R.id.export_finished_layout);
        exportFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = getActivity();
                if(activity == null){
                    return;
                }

                if(State.getSingleton().hasFeature(FeatureType.export)) {
                    List<Consumption> filteredConsumptions = _exportService.getFilteredConsumptions();
                    ExportHelper export = ExportHelper.getSingleton(activity);
                    export.exportToCsv(filteredConsumptions);
                    activity.finish();
                }
                else{

                    final IabHelper billingHelper = State.getSingleton().getIabHelper();

                    if(billingHelper == null) {
                        return;
                    }

                    final SkuDetails exportSkuDetails = State.getSingleton().getAvailableFeatures().get(FeatureType.export);
                    if(exportSkuDetails != null) {
                        billingHelper.launchPurchaseFlow(activity, exportSkuDetails.getSku(), 10001, new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isFailure()) {
                                    Timber.e("Error purchasing: " + result);
                                    return;
                                }
                                Timber.d(info.getDeveloperPayload());
                                Timber.d(info.getOrderId());
                                Timber.d(info.getPackageName());
                                Timber.d(info.getSku());

                                State.getSingleton().getEnabledFeatures().add(FeatureType.export);
                                _bus.post(new PurchasedFeatureEvent(FeatureType.export));

                                setExportButtonText();

                                try {
                                    double charge = Double.parseDouble(exportSkuDetails.getPrice());
                                    if (charge > 0) {
                                        TrackerHelper.trackPurchase(FeatureType.export.name(), charge);
                                    }
                                }
                                catch(Exception ex){
                                    Timber.e(ex, "Tracking the purchase failed");
                                }
                            }
                        }, TrackerHelper.getUniqueId(activity));
                    }
                }
            }
        });
        _finishedView = view;
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        updatePillSummary(getActivity());
        updateDateSummary(getActivity());
        updateTimeSummary(getActivity());

        TextView summaryTextView = _exportService.getSummaryTextView();
        if(summaryTextView != null) {
            summaryTextView.setText(R.string.export_sub_title);
            summaryTextView.setTextColor(getResources().getColor(R.color.text_grey_medium));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        if(activity == null)
            return;

        Timber.d("onActivityCreated");

        if(_pills == null || _pills.size() == 0) {
            _jobManager.addJobInBackground(new LoadPillsJob());
        }

        setExportButtonText();
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event){
        _pills = event.getPills();
        updatePillSummary(getActivity());
    }

    private void setExportButtonText(){
        if (State.getSingleton().getAvailableFeatures().containsKey(FeatureType.export)) {
            SkuDetails skuDetails = State.getSingleton().getAvailableFeatures().get(FeatureType.export);

            if(State.getSingleton().hasFeature(FeatureType.export)) {
                _unlock.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                _unlock.setText("Export");
            }
            else {
                _unlock.setText(getString(R.string.unlock_prefix) + " " + skuDetails.getPrice());
                _unlock.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_store, 0, 0, 0);
            }
        }
    }

    private void updatePillSummary(Context context){
        if (context == null || _finishedView == null) {
            return;
        }

        String text = _exportService.getPillSummary(_pillSummary);

        _pillSummary.setText(text);
    }

    private void updateDateSummary(Context context){
        if(context == null || _finishedView == null) {
            return;
        }

        String text = _exportService.getDateSummary();

        _dateSummary.setText(text);
    }

    private void updateTimeSummary(Context context){
        if(context == null || _finishedView == null) {
            return;
        }

        String text = _exportService.getTimeSummary();

        _timeSummary.setText(text);
    }
}
