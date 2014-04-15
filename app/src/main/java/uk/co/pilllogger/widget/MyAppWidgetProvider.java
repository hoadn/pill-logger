package uk.co.pilllogger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.AppWidgetConfigure;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.views.ColourIndicator;
import uk.co.pilllogger.views.WidgetIndicator;

/**
 * Created by nick on 31/10/13.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MyAppWidgetProvider";
    public static String CLICK_ACTION = "ClickAction";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        for(int id : appWidgetIds){
            updateWidget(context, id, appWidgetManager);
        }
    }

    public static void updateWidget(Context context, int id, AppWidgetManager appWidgetManager){
        Logger.d(TAG, "WidgetId: " + id);
        int pillId = context.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS).getInt("widget" + id, -1);

        Logger.d(TAG, "PillId: " + pillId);
        if(pillId == -1){
            return;
        }

        Pill pill = PillRepository.getSingleton(context).get(pillId);

        if(pill == null)
            return;

        Intent newIntent = new Intent(context, MyAppWidgetProvider.class);
        newIntent.setAction(CLICK_ACTION);
        newIntent.putExtra(AppWidgetConfigure.PILL_ID, pill.getId());

        //Create a pending intent from our intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pill.getId(), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.appwidget);
        views.setOnClickPendingIntent(R.id.widget_text, pendingIntent);
        views.setTextViewText(R.id.widget_size, String.valueOf(NumberHelper.getNiceFloatString(pill.getSize()) + "mg"));
        views.setInt(R.id.widget_size, "setBackgroundColor", pill.getColour());
        views.setTextViewText(R.id.widget_text, pill.getName().substring(0, 2));

        WidgetIndicator indicator = new WidgetIndicator(context);
        indicator.setColour(pill.getColour(), true);
        indicator.measure(90, 90);
        indicator.layout(0, 0, 90, 90);
        indicator.setDrawingCacheEnabled(true);
        Bitmap bitmap = indicator.getDrawingCache();
        views.setImageViewBitmap(R.id.widget_colour_indicator, bitmap);

        appWidgetManager.updateAppWidget(id, views);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_ACTION)) {

            int pillId = intent.getIntExtra(AppWidgetConfigure.PILL_ID, 0);
            Pill pill = PillRepository.getSingleton(context).get(pillId);
            if (pill != null) {
                Consumption consumption = new Consumption(pill, new Date());
                new InsertConsumptionTask(context, consumption).execute();
            }
            Toast.makeText(context, pill.getName() + " added", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,"Testing", Toast.LENGTH_LONG).show();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = new int[0];
            if (appWidgetManager != null) {
                appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, this.getClass()));
            }

            for(int id : appWidgetIds){
                updateWidget(context, id, appWidgetManager);
            }
        }


    }

    @Override
    public void onEnabled(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = new int[0];
        if (appWidgetManager != null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, this.getClass()));
        }

        for(int id : appWidgetIds){
            updateWidget(context, id, appWidgetManager);
        }
    }
}
