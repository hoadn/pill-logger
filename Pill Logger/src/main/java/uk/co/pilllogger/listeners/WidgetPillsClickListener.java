package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

import uk.co.cntwo.pilllogger.activities.AppWidgetConfigure;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by Nick on 05/11/13.
 */
public class WidgetPillsClickListener implements AdapterView.OnItemClickListener {

    AppWidgetConfigure _activity;

    public WidgetPillsClickListener(AppWidgetConfigure activity) {
        _activity = activity;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Pill pill = (Pill)adapterView.getItemAtPosition(i);
        _activity.setChosenPill(pill);
    }
}
