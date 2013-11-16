package uk.co.cntwo.pilllogger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Date;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.activities.AppWidgetConfigure;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.repositories.PillRepository;
import uk.co.cntwo.pilllogger.tasks.InsertConsumptionTask;

/**
 * Created by nick on 31/10/13.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    public static String CLICK_ACTION = "ClickAction";
    public RemoteViews _views;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
              int appWidgetId = appWidgetIds[i];
//
//            // Create an Intent to add to our button with an action so we can tell what's been pressed
//            Intent intent = new Intent(context, MyAppWidgetProvider.class);
//            intent.setAction(CLICK_ACTION);
//
//            //Create a pending intent from our intent
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//            // Get the layout for the App Widget and attach an on-click listener to the view
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
//            views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_ACTION)) {
            Bundle bundle = intent.getExtras();
            int pillId = bundle.getInt(AppWidgetConfigure.PILL_ID);
            Pill pill = PillRepository.getSingleton(context).get(pillId);
            if (pill != null) {
                Consumption consumption = new Consumption(pill, new Date());
                new InsertConsumptionTask(context, consumption).execute();
            }
            Toast.makeText(context, pill.getName() + " added", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,"Testing", Toast.LENGTH_LONG).show();
            intent.setAction(CLICK_ACTION);
            Bundle bundle = intent.getExtras();
            //Create a pending intent from our intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (_views == null)
                _views = new RemoteViews(context.getPackageName(),
                        R.layout.appwidget);

            _views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);

            int pillId = bundle.getInt(AppWidgetConfigure.PILL_ID);
            Pill pill = PillRepository.getSingleton(context).get(pillId);
            if (pill != null) {
                _views.setTextViewText(R.id.widget_size, String.valueOf(pill.getSize() + "mg"));
                _views.setInt(R.id.widget_size,"setBackgroundColor", pill.getColour());
                _views.setTextViewText(R.id.widget_text, pill.getName().substring(0,1));
            }
            AppWidgetManager.getInstance(context).updateAppWidget(intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS), _views);
        }


    }

    @Override
    public void onEnabled(Context context){

    }

    private void setUpWidget(Context context) {

    }
}
