package uk.co.pilllogger.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.fragments.ConsumptionInfoDialogFragment;
import uk.co.pilllogger.fragments.NewPillDialogFragment;
import uk.co.pilllogger.fragments.PillInfoDialogFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;
import static butterknife.ButterKnife.findById;

/**
 * Created by Alex on 22/05/2014
 * in uk.co.pilllogger.activities.
 */
public class DialogActivity extends FragmentActivity{

    @Inject
    ConsumptionRepository _consumptionRepository;

    private static final String TAG = "DialogActivity";
    private Pill _pill;
    private List<Consumption> _consumptions;
    private TextView _firstStats;
    private ImageView _firstStatsIndicator;
    private TextView _secondStats;
    private ImageView _secondStatsIndicator;
    private TextView _thirdStats;
    private TextView _statsTitle;
    private View _statsContainer;
    private boolean _statsToggled;
    private ColourIndicator _colour;
    private TextView _title;
    private TextView _lastTaken;
    private TextView _dosage;
    private Bus _bus;
    private ViewGroup _dialogTop;
    @Inject PillRepository _pillRepository;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);

        _bus = State.getSingleton().getBus();

        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        Typeface roboto = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");

        View container = findViewById(R.id.export_container);

        if(container != null) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.width = (int) (width * 0.9);
        }

        _colour = (ColourIndicator)findViewById(R.id.colour);
        _title = (TextView) findViewById(R.id.info_dialog_title);
        _lastTaken = (TextView) findViewById(R.id.info_dialog_last_taken);
        _dosage = (TextView) findViewById(R.id.info_dialog_dosage);
        _dialogTop = findById(this, R.id.info_dialog_top);

        setFragment();

        Typeface typeface = State.getSingleton().getTypeface();
        _title.setTypeface(typeface);
        _lastTaken.setTypeface(typeface);
        _dosage.setTypeface(typeface);

        bindPill();

        setupStats();
    }

    private void bindPill(){
        if(_pill != null) {
            _colour.setColour(_pill.getColour());

            Consumption lastConsumption = _pill.getLatestConsumption(_consumptionRepository);
            if (lastConsumption != null) {
                String lastTakenText = getString(R.string.info_dialog_last_taken) + " " + DateHelper.formatDateAndTime(this, lastConsumption.getDate());
                lastTakenText += " " + DateHelper.getTime(this, lastConsumption.getDate());
                _lastTaken.setText(lastTakenText);
            }
            _consumptions = _pill.getConsumptions();
            String dosage24 = NumberHelper.getNiceFloatString(_pill.getTotalSize(24));
            int quantity24 = _pill.getTotalQuantity(24);

            String dosageText = getString(R.string.twenty_four_hour_short_hand);
            if(_pill.getSize() > 0)
                dosageText += " " + dosage24  + _pill.getUnits() + " (" + quantity24 + " x " + _pill.getFormattedSize() + _pill.getUnits() + ")";
            else
                dosageText += " " + quantity24;
            _dosage.setText(dosageText);

            _title.setText(_pill.getName() + " " + _pill.getFormattedSize() + _pill.getUnits());
        }
    }

    private void setFragment() {
        Fragment fragment = null;

        Intent intent = getIntent();
        if (intent != null) {
            int dialogTypeInt = intent.getIntExtra("DialogType", DialogType.Consumption.ordinal());

            DialogType dialogType = DialogType.values()[dialogTypeInt];

            int pillId = intent.getIntExtra("PillId", -1);
            if (pillId >= 0) {
                _pill = _pillRepository.get(pillId);
            }

            switch (dialogType) {

                case Consumption:
                    String consumptionGroup = intent.getStringExtra("ConsumptionGroup");

                    List<Consumption> consumptions = _consumptionRepository.getForGroup(consumptionGroup);

                    consumptions = _consumptionRepository.groupConsumptions(consumptions);

                    for (Consumption consumption : consumptions) {
                        if (consumption.getPillId() != pillId) {
                            continue;
                        }

                        fragment = new ConsumptionInfoDialogFragment(this, consumption);
                        break;
                    }
                    break;
                case Pill:
                    fragment = new PillInfoDialogFragment(_pill);
                    break;

                case NewPill:
                    _dialogTop.setVisibility(View.GONE);
                    fragment = new NewPillDialogFragment();
                    break;
            }
        }

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null || fragment == null){
            return;
        }

        fragmentManager
                .beginTransaction()
                .add(R.id.export_container, fragment)
                .commitAllowingStateLoss();
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
    protected void onResume(){
        super.onResume();

        State.getSingleton().setAppVisible(true);
        _bus.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();

        State.getSingleton().setAppVisible(false);
        _bus.unregister(this);
    }

    private void setupStats() {
        _firstStats = (TextView) findViewById(R.id.pill_stats_7d);
        _firstStatsIndicator = (ImageView) findViewById(R.id.pill_stats_7d_indicator);
        _secondStats = (TextView) findViewById(R.id.pill_stats_30d);
        _secondStatsIndicator = (ImageView) findViewById(R.id.pill_stats_30d_indicator);
        _thirdStats = (TextView) findViewById(R.id.pill_stats_all_time);
        _statsTitle = (TextView) findViewById(R.id.info_dialog_daily_title);

        Typeface typeface = State.getSingleton().getTypeface();
        _firstStats.setTypeface(typeface);
        _secondStats.setTypeface(typeface);
        _thirdStats.setTypeface(typeface);
        _statsTitle.setTypeface(typeface);

        _statsContainer = findViewById(R.id.pill_stats_container);

        if(_pill != null && _pill.getSize() > 0) {
            _statsContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _statsToggled = !_statsToggled;
                    updateStats();
                }
            });
        }

        updateStats();
    }

    private void updateStats(){
        if(_pill == null)
            return;

        float firstAverage = _pill.getDailyAverage(7);
        float secondAverage = _pill.getDailyAverage(30);
        float totalAverage = _pill.getDailyAverage();

        _firstStatsIndicator.setImageResource(firstAverage > totalAverage ? R.drawable.chevron_up_grey : R.drawable.chevron_down_grey);
        _secondStatsIndicator.setImageResource(secondAverage > totalAverage ? R.drawable.chevron_up_grey : R.drawable.chevron_down_grey);

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String firstText = _statsToggled ? decimalFormat.format(firstAverage * _pill.getSize()) + _pill.getUnits() : decimalFormat.format(firstAverage);
        String secondText = _statsToggled ? decimalFormat.format(secondAverage * _pill.getSize()) + _pill.getUnits() : decimalFormat.format(secondAverage);
        String thirdText = _statsToggled ? decimalFormat.format(totalAverage * _pill.getSize()) + _pill.getUnits() : decimalFormat.format(totalAverage);

        _firstStats.setText(firstText);
        _secondStats.setText(secondText);
        _thirdStats.setText(thirdText);

        _statsTitle.setText(_statsToggled ? R.string.info_daily_title_dosage : R.string.info_daily_title_units);
    }

    @Subscribe @DebugLog
    public void pillsUpdated(UpdatedPillEvent event) {
        _pill = event.getPill();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bindPill();
            }
        });
    }

    public enum DialogType{
        Consumption,
        NewPill, Pill
    }
}
