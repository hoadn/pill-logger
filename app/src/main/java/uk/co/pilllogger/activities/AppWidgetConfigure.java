package uk.co.pilllogger.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.WidgetListAdapter;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.listeners.WidgetPillsClickListener;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends PillLoggerActivityBase implements GetPillsTask.ITaskComplete {

    public static String CLICK_ACTION = "ClickAction";
    public static String PILL_ID = "uk.co.pilllogger.activities.AppWidgetConfigure.PILL_ID";
    int _appWidgetId = -1;
    Pill _chosenPill;
    Typeface _typeface;
    ListView _pillsList;
    View _selectedPillLayout;
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

        final Activity activity = this;
        if (pillsList != null) {
            WidgetListAdapter adapter = new WidgetListAdapter(this, R.layout.pill_list_item, pills);
            pillsList.setAdapter(adapter);
            pillsList.setOnItemClickListener(new WidgetPillsClickListener(this));
            pillsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Pill pill = (Pill)parent.getItemAtPosition(position);

                    if(pill == null)
                        return;

                    Intent intent = new Intent(activity, MyAppWidgetProvider.class);
                    intent.setAction(CLICK_ACTION);
                    intent.putExtra(AppWidgetConfigure.PILL_ID, pill.getId());

                    //Create a pending intent from our intent
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, new Date().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);


                    activity.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS).edit().putInt("widget" + _appWidgetId, pill.getId()).commit();

                    MyAppWidgetProvider.updateWidget(activity, _appWidgetId, appWidgetManager);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    TrackerHelper.widgetCreatedEvent(AppWidgetConfigure.this, "AppWidgetConfigure");
                    finish();
                }
            });
        }
    }

}