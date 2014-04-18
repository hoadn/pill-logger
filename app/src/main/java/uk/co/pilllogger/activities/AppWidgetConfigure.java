package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.adapters.WidgetListAdapter;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.pilllogger.listeners.WidgetPillsClickListener;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends PillLoggerActivityBase implements GetPillsTask.ITaskComplete, AddConsumptionPillListAdapter.IConsumptionSelected {

    public static String CLICK_ACTION = "ClickAction";
    public static String PILL_ID = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_ID";
    public static String PILL_QUANTITY = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_QUANTITY";
    int _appWidgetId = -1;
    Typeface _typeface;
    Intent _newIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_widget_configure);

        _typeface = State.getSingleton().getTypeface();

        this.setResult(RESULT_CANCELED);
        new GetPillsTask(this, this).execute();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        _newIntent = new Intent(this, MyAppWidgetProvider.class);

    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        ListView pillsList = (ListView) findViewById(R.id.widget_configure_pill_list);

        if (pillsList != null) {
            final AddConsumptionPillListAdapter adapter = new AddConsumptionPillListAdapter(this, this, R.layout.add_consumption_pill_list, pills);
            pillsList.setAdapter(adapter);
            pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)pillsList.getAdapter(), false));

            View button = findViewById(R.id.widget_configure_add);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Pill> pillsConsumed = adapter.getPillsConsumed();
                    if(pillsConsumed.size() == 0)
                        return;

                    int quantity = pillsConsumed.size();
                    Pill pill = pillsConsumed.get(0);

                    addWidget(pill, quantity);
                }
            });
        }
    }

    private void addWidget(Pill pill, int quantity){
        if(pill == null)
            return;

        Intent intent = new Intent(this, MyAppWidgetProvider.class);
        intent.setAction(CLICK_ACTION);
        intent.putExtra(AppWidgetConfigure.PILL_ID, pill.getId());
        intent.putExtra(AppWidgetConfigure.PILL_QUANTITY, quantity);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        SharedPreferences.Editor editor = this.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS).edit();
        editor.putInt("widgetPill" + _appWidgetId, pill.getId());
        editor.putInt("widgetQuantity" + _appWidgetId, quantity);
        editor.commit();

        MyAppWidgetProvider.updateWidget(this, _appWidgetId, appWidgetManager);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
        setResult(RESULT_OK, resultValue);
        TrackerHelper.widgetCreatedEvent(AppWidgetConfigure.this, "AppWidgetConfigure");
        finish();
    }

    @Override
    public void setDoneEnabled(boolean enabled) {

    }
}