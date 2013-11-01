package uk.co.cntwo.pilllogger.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.PillsListAdapter;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.GetPillsTask;
import uk.co.cntwo.pilllogger.widget.MyAppWidgetProvider;

/**
 * Created by nick on 01/11/13.
 */
public class AppWidgetConfigure extends Activity implements GetPillsTask.ITaskComplete {

    public static String CLICK_ACTION = "ClickAction";
    int _appWidgetId = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_widget_configure);

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

    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        ListView pillsList = (ListView) findViewById(R.id.widget_configure_pill_list);
        if (pillsList != null) {
            PillsListAdapter adapter = new PillsListAdapter(this, R.layout.pill_list_item, pills);
            pillsList.setAdapter(adapter);
        }
    }

    public class finishConfigureClickListener implements View.OnClickListener {

        Context _context;

        public finishConfigureClickListener(Context context) {
            _context = context;
        }

        @Override
        public void onClick(View view) {
            Intent newIntent = new Intent(_context, MyAppWidgetProvider.class);
            newIntent.setAction(CLICK_ACTION);

            //Create a pending intent from our intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, newIntent, 0);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(_context);

            RemoteViews views = new RemoteViews(_context.getPackageName(),
                    R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
            appWidgetManager.updateAppWidget(_appWidgetId, views);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}