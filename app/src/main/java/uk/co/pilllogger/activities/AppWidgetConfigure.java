package uk.co.pilllogger.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.listeners.AddConsumptionPillItemClickListener;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends PillLoggerActivityBase implements AddConsumptionPillListAdapter.IConsumptionSelected {

    public static String CLICK_ACTION = "ClickAction";
    public static String PILL_ID = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_ID";
    public static String PILL_QUANTITY = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_QUANTITY";
    int _appWidgetId = -1;
    Typeface _typeface;
    Intent _newIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(State.getSingleton().getTheme().getStyleResourceId());

        setContentView(R.layout.activity_app_widget_configure);

        _typeface = State.getSingleton().getTypeface();

        this.setResult(RESULT_CANCELED);
        new GetPillsTask(this).execute();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            _appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        _newIntent = new Intent(this, MyAppWidgetProvider.class);
    }

    @Subscribe
    public void pillsReceived(LoadedPillsEvent event) {
        ListView pillsList = (ListView) findViewById(R.id.widget_configure_pill_list);

        if (pillsList != null) {
            final AddConsumptionPillListAdapter adapter = new AddConsumptionPillListAdapter(this, this, R.layout.add_consumption_pill_list, event.getPills());
            pillsList.setAdapter(adapter);
            // pillsList.setOnItemClickListener(new AddConsumptionPillItemClickListener(this, (AddConsumptionPillListAdapter)pillsList.getAdapter(), false));

            View button = findViewById(R.id.widget_configure_add);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Pill> pillsConsumed = adapter.getPillsConsumed();
                    if(pillsConsumed.size() == 0)
                        return;

                    Map<Pill, Integer> pills = new HashMap<Pill, Integer>();
                    for(Pill pill : pillsConsumed){
                        if(pills.containsKey(pill)){
                            pills.put(pill, pills.get(pill) + 1);
                        }
                        else {
                            pills.put(pill, 1);
                        }
                    }

                    addWidget(pills, Color.RED, "XXX");
                }
            });
        }
    }

    private void addWidget(Map<Pill, Integer> pills, int colour, String name){
        if(pills == null || pills.isEmpty())
            return;

        Intent intent = new Intent(this, MyAppWidgetProvider.class);
        intent.setAction(CLICK_ACTION);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        SharedPreferences.Editor editor = this.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS).edit();

        editor.putString("widgetName" + _appWidgetId, name);
        editor.putInt("widgetColour" + _appWidgetId, colour);

        int i = 0;
        for(Pill pill : pills.keySet()) {
            intent.putExtra(AppWidgetConfigure.PILL_ID + i, pill.getId());
            Integer quantity = pills.get(pill);

            intent.putExtra(AppWidgetConfigure.PILL_QUANTITY + i, quantity);

            editor.putInt("widgetPill" + i + _appWidgetId, pill.getId());
            editor.putInt("widgetQuantity" + i + _appWidgetId, quantity);

            i++;
        }
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