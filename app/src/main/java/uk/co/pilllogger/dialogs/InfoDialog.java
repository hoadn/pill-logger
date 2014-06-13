package uk.co.pilllogger.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


/**
 * Created by Nick on 05/03/14.
 */
public abstract class InfoDialog extends DialogFragment {

    String _title;
    Pill _pill;
    List<Consumption> _consumptions;
    private TextView _firstStats;
    private ImageView _firstStatsIndicator;
    private TextView _secondStats;
    private ImageView _secondStatsIndicator;
    private TextView _thirdStats;
    private View _statsContainer;
    private boolean _statsToggled = false;
    private TextView _statsTitle;

    public InfoDialog(){
    }

    public InfoDialog(Pill pill) {
        _title = pill.getName() + " " + pill.getFormattedSize() + pill.getUnits();
        _pill = pill;
    }

    protected abstract int getLayoutId();
    protected abstract void setupMenu(View view);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        if(activity == null)
            return null;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(getLayoutId(), null);

        if(view == null)
            return null;

        ColourIndicator colour = (ColourIndicator)view.findViewById(R.id.colour);
        TextView title = (TextView) view.findViewById(R.id.info_dialog_title);
        TextView lastTaken = (TextView) view.findViewById(R.id.info_dialog_last_taken);
        TextView dosage = (TextView) view.findViewById(R.id.info_dialog_dosage);

        Typeface typeface = State.getSingleton().getTypeface();
        title.setTypeface(typeface);
        lastTaken.setTypeface(typeface);
        dosage.setTypeface(typeface);

        title.setText(_title);

        if(_pill != null) {
            colour.setColour(_pill.getColour());

            Consumption lastConsumption = _pill.getLatestConsumption(activity);
            if (lastConsumption != null) {
                String lastTakenText = lastTaken.getText() + " " + DateHelper.formatDateAndTime(activity, lastConsumption.getDate());
                lastTakenText += " " + DateHelper.getTime(activity, lastConsumption.getDate());
                lastTaken.setText(lastTakenText);
            }
            _consumptions = _pill.getConsumptions();
            String dosage24 = NumberHelper.getNiceFloatString(_pill.getTotalSize(24));
            int quantity24 = _pill.getTotalQuantity(24);

            String dosageText = dosage.getText().toString();
            if(_pill.getSize() > 0)
                dosageText += " " + dosage24 + _pill.getUnits() + " (" + quantity24 + " x " + _pill.getFormattedSize() + _pill.getUnits() + ")";
            else
                dosageText += " " + quantity24;
            dosage.setText(dosageText);
        }
        setupMenu(view);

        setupStats(view);

        builder.setView(view);
        return builder.create();
    }

    private void setupStats(View view) {
        _firstStats = (TextView) view.findViewById(R.id.pill_stats_7d);
        _firstStatsIndicator = (ImageView) view.findViewById(R.id.pill_stats_7d_indicator);
        _secondStats = (TextView) view.findViewById(R.id.pill_stats_30d);
        _secondStatsIndicator = (ImageView) view.findViewById(R.id.pill_stats_30d_indicator);
        _thirdStats = (TextView) view.findViewById(R.id.pill_stats_all_time);
        _statsTitle = (TextView) view.findViewById(R.id.info_dialog_daily_title);

        Typeface typeface = State.getSingleton().getTypeface();
        _firstStats.setTypeface(typeface);
        _secondStats.setTypeface(typeface);
        _thirdStats.setTypeface(typeface);
        _statsTitle.setTypeface(typeface);

        _statsContainer = view.findViewById(R.id.pill_stats_container);

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
}