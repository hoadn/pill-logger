package uk.co.pilllogger.activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.fragments.ExportMainFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.ExportSettings;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.services.IExportService;
import uk.co.pilllogger.state.FeatureType;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetMaxDosagesTask;
import uk.co.pilllogger.tasks.GetPillsTask;

/**
 * Created by Alex on 22/05/2014
 * in uk.co.pilllogger.activities.
 */
public class ExportActivity extends FragmentActivity
        implements
        IExportService,
        GetMaxDosagesTask.ITaskComplete,
        GetConsumptionsTask.ITaskComplete, Observer.IFeaturePurchased {

    private static final String TAG = "ExportActivity";
    private List<Pill> _pillsList;
    private ExportSettings _exportSettings = new ExportSettings();
    private Map<Integer, Integer> _maxDosages;
    private List<Consumption> _consumptions;
    private TextView _exportUnlockTitle;
    private TextView _exportSubTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export);

        if (!PillRepository.getSingleton(this).isCached()) {
            new GetPillsTask(this).execute();
        }

        new GetConsumptionsTask(this, this, true).execute();
        new GetMaxDosagesTask(this, this).execute();
        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        Typeface roboto = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        TextView exportTitle = (TextView)findViewById(R.id.export_title);
        _exportSubTitle = (TextView)findViewById(R.id.export_sub_title);
        _exportUnlockTitle = (TextView) findViewById(R.id.export_unlock_title);
        exportTitle.setTypeface(roboto);
        _exportUnlockTitle.setTypeface(roboto);
        _exportSubTitle.setTypeface(roboto);

        View container = findViewById(R.id.export_container);

        if(container != null) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.width = (int) (width * 0.9);
        }

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.export_container, new ExportMainFragment())
                    .commit();
        }

        Observer.getSingleton().registerFeaturePurchasedObserver(this);

        if(State.getSingleton().hasFeature(FeatureType.export)){
            _exportUnlockTitle.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event) {
        _pillsList = event.getPills();
        _exportSettings.getSelectedPills().addAll(event.getPills());
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public ExportSettings getExportSettings() {
        return _exportSettings;
    }

    @Override
    public List<Pill> getAllPills() {
        return _pillsList;
    }

    @Override
    public Map<Integer, Integer> getMaxDosages() {
        return _maxDosages;
    }

    @Override
    public List<Consumption> getFilteredConsumptions() {
        if(_consumptions == null || _consumptions.size() == 0){
            return new ArrayList<Consumption>();
        }

        List<Consumption> filteredConsumptions = new ArrayList<Consumption>();

        Set<Pill> selectedPills = _exportSettings.getSelectedPills();
        MutableDateTime startDate = _exportSettings.getStartDate();
        MutableDateTime endDate = _exportSettings.getEndDate();
        LocalTime startTime = _exportSettings.getStartTime();
        LocalTime endTime = _exportSettings.getEndTime();

        for(Consumption c : _consumptions){
            LocalTime consumptionTime = new LocalTime().withHourOfDay(c.getDate().getHours())
                                                            .withMinuteOfHour(c.getDate().getMinutes());
            MutableDateTime consumptionDate = new MutableDateTime(c.getDate());

            if (!selectedPills.contains(c.getPill()))
                continue;
            if (startDate != null && consumptionDate.isBefore(startDate))
                continue;
            if (endDate != null && consumptionDate.isAfter(endDate))
                continue;
            if (startTime != null && consumptionTime.isBefore(startTime))
                continue;
            if (endTime != null && consumptionTime.isAfter(endTime))
                continue;

            filteredConsumptions.add(c);
        }

        return filteredConsumptions;
    }

    @Override
    public String getPillSummary(){
        return getPillSummary(null);
    }

    @Override
    public String getPillSummary(TextView tv) {
        String prefix = "All";

        int currentlySelectedPills = getExportSettings().getSelectedPills().size();

        if(currentlySelectedPills == 0){
            String text = "You must select at least 1 medicine";
            if(tv != null) {
                tv.setText(text);
                tv.setTextColor(this.getResources().getColor(R.color.warning_red));
            }

            return text;
        }
        if(tv != null) {
            tv.setTextColor(this.getResources().getColor(R.color.text_grey_medium));
        }

        if(currentlySelectedPills != _pillsList.size())
            prefix = currentlySelectedPills + " of";

        String text = prefix;

        if(currentlySelectedPills == 2 && _pillsList.size() == 2)
            text = "Both";

        if(_pillsList.size() > 2 || currentlySelectedPills != _pillsList.size())
            text += " " + _pillsList.size();

        text += " medicine";
        if(_pillsList.size() > 1 || currentlySelectedPills == _pillsList.size())
            text += "s";

        text += " selected";

        if(tv != null){
            tv.setText(text);
        }

        return text;
    }

    @Override
    public String getDateSummary() {
        String text = "Any date";

        if(getExportSettings().getStartDate() != null
                && getExportSettings().getEndDate() != null){

            String startDateString = DateHelper.formatDateAndTimeMedium(this, getExportSettings().getStartDate().toDate());
            String endDateString = DateHelper.formatDateAndTimeMedium(this, getExportSettings().getEndDate().toDate());

            text = startDateString + " - " + endDateString;
        }
        else {
            if (getExportSettings().getEndDate() != null) {
                String endDateString = DateHelper.formatDateAndTimeMedium(this, getExportSettings().getEndDate().toDate());
                text = "Before " + endDateString;
            }
            else{
                if(getExportSettings().getStartDate() != null){
                    String startDateString = DateHelper.formatDateAndTimeMedium(this, getExportSettings().getStartDate().toDate());
                    text = "After " + startDateString;
                }
            }
        }

        return text;
    }

    @Override
    public String getTimeSummary() {
        String text = "Any time of the day";

        if(getExportSettings().getStartTime() != null
                && getExportSettings().getEndTime() != null){

            String startTimeString = DateHelper.getTime(this, getExportSettings().getStartTime().toDateTimeToday());
            String endTimeString = DateHelper.getTime(this, getExportSettings().getEndTime().toDateTimeToday());

            text = startTimeString + " - " + endTimeString;
        }
        else {
            if (getExportSettings().getEndTime() != null) {
                String endTimeString = DateHelper.getTime(this, getExportSettings().getEndTime().toDateTimeToday());
                text = "Before " + endTimeString;
            }
            else{
                if(getExportSettings().getStartTime() != null){
                    String startTimeString = DateHelper.getTime(this, getExportSettings().getStartTime().toDateTimeToday());
                    text = "After " + startTimeString;
                }
            }
        }

        return text;
    }

    @Override
    public TextView getSummaryTextView() {
        return _exportSubTitle;
    }

    @Override
    public void maxConsumptionsReceived(Map<Integer, Integer> pillConsumptionMaxQuantityMap) {
        _maxDosages = pillConsumptionMaxQuantityMap;
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        _consumptions = consumptions;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if(State.getSingleton().getIabHelper() == null) {
            return;
        }

        // Pass on the activity result to the helper for handling
        if (!State.getSingleton().getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onDestroy(){
        Observer.getSingleton().unregisterFeaturePurchasedObserver(this);

        super.onDestroy();
    }

    @Override
    public void featurePurchased(FeatureType featureType) {
        if(_exportUnlockTitle != null){
            _exportUnlockTitle.setVisibility(View.GONE);
        }
    }
}
