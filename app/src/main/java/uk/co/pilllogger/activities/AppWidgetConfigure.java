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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.WidgetListAdapter;
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
    TextView _selectedPillName, _selectedPillSize;
    ListView _pillsList;
    View _selectedPillLayout;
    Intent _newIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        View finishButton = findViewById(R.id.widget_finish);
        finishButton.setOnClickListener(new finishConfigureClickListener(this));

        _selectedPillName = (TextView) findViewById(R.id.widget_configure_selected_pill_name);
        _selectedPillSize = (TextView) findViewById(R.id.widget_configure_selected_pill_size);
        _selectedPillName.setTypeface(_typeface);
        _selectedPillSize.setTypeface(_typeface);


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
                    finish();
                }
            });
        }
    }


    public void setChosenPill(Pill pill) {
        _chosenPill = pill;
        _selectedPillName.setText(pill.getName());
        _selectedPillSize.setText(String.valueOf(pill.getSize()));
        _selectedPillLayout = findViewById(R.id.widget_configure_selected_pill);


        _pillsList = (ListView)findViewById(R.id.widget_configure_pill_list);
        _pillsList.setVisibility(View.GONE);
        _selectedPillLayout.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putInt(AppWidgetConfigure.PILL_ID, _chosenPill.getId());
        _newIntent.putExtras(bundle);
    }

    public void cancelPillSelection(View view) {
        _pillsList.setVisibility(View.VISIBLE);
        _selectedPillLayout.setVisibility(View.GONE);
    }

    public class finishConfigureClickListener implements View.OnClickListener {

        Context _context;

        public finishConfigureClickListener(Context context) {
            _context = context;
        }

        @Override
        public void onClick(View view) {
            _newIntent.setAction(CLICK_ACTION);
            Bundle bundle = _newIntent.getExtras();
            int pillId = bundle.getInt(AppWidgetConfigure.PILL_ID);
            //Create a pending intent from our intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, _newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(_context);

            RemoteViews views = new RemoteViews(_context.getPackageName(),
                    R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            views.setTextViewText(R.id.widget_size, String.valueOf(_chosenPill.getSize() + "mg"));
            views.setInt(R.id.widget_size,"setBackgroundColor", _chosenPill.getColour());
            views.setTextViewText(R.id.widget_text, _chosenPill.getName().substring(0,1));
            appWidgetManager.updateAppWidget(_appWidgetId, views);


            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}