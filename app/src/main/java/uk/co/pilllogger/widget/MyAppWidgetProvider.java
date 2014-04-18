package uk.co.pilllogger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.AppWidgetConfigure;
import uk.co.pilllogger.helpers.ColourHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
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

    public static void updateAllWidgets(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = new int[0];
        if (appWidgetManager != null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, MyAppWidgetProvider.class));
        }

        for(int id : appWidgetIds){
            updateWidget(context, id, appWidgetManager);
        }
    }

    public static void updateWidget(Context context, int id, AppWidgetManager appWidgetManager){
        Logger.d(TAG, "WidgetId: " + id);
        SharedPreferences preferences = context.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS);
        int pillId = preferences.getInt("widgetPill" + id, -1);
        int quantity = preferences.getInt("widgetQuantity" + id, 1);

        Logger.d(TAG, "PillId: " + pillId);
        if(pillId == -1){
            return;
        }

        Pill pill = PillRepository.getSingleton(context).get(pillId);

        if(pill == null)
            return;

        String indicatorChar = context.getString(R.string.widget_quantity_indicator);
        String indicatorOverloadChar = context.getString(R.string.widget_quantity_overload_indicator);

        Intent newIntent = new Intent(context, MyAppWidgetProvider.class);
        newIntent.setAction(CLICK_ACTION);
        newIntent.putExtra(AppWidgetConfigure.PILL_ID, pill.getId());
        newIntent.putExtra(AppWidgetConfigure.PILL_QUANTITY, quantity);

        //Create a pending intent from our intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pill.getId(), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.appwidget);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        views.setTextViewText(R.id.widget_size, String.valueOf(NumberHelper.getNiceFloatString(pill.getSize()) + "mg"));
        views.setInt(R.id.widget_size, "setBackgroundColor", pill.getColour());
        views.setTextViewText(R.id.widget_text, pill.getName().substring(0, 3));

        String quantityIndicator = "";

        if(quantity > 3)
            quantityIndicator = String.format("%s %s %s", indicatorChar, indicatorChar, indicatorOverloadChar);
        else{
            for(int i = 0; i < quantity; i++) {
                if(i > 0)
                    quantityIndicator += " ";

                quantityIndicator += indicatorChar;
            }
        }

        views.setTextViewText(R.id.widget_quantity_indicator, quantityIndicator);

        WidgetIndicator indicator = new WidgetIndicator(context);
        indicator.setColour(pill.getColour(), true);
        int widgetSize = (int)LayoutHelper.dpToPx(context, 60);
        indicator.measure(widgetSize, widgetSize);
        indicator.layout(0, 0, widgetSize, widgetSize);
        indicator.setDrawingCacheEnabled(true);
        Bitmap bitmap = indicator.getDrawingCache();
        views.setImageViewBitmap(R.id.widget_colour_indicator, bitmap);

        int sizeTextColour = R.color.white;
        if (ColourHelper.isColourLight(pill.getColour())) {
            sizeTextColour = R.color.text_grey;
        }
        views.setInt(R.id.widget_size, "setTextColor", context.getResources().getColor(sizeTextColour));

        appWidgetManager.updateAppWidget(id, views);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_ACTION)) {

            int pillId = intent.getIntExtra(AppWidgetConfigure.PILL_ID, 0);
            int quantity = intent.getIntExtra(AppWidgetConfigure.PILL_QUANTITY, 1);
            Pill pill = PillRepository.getSingleton(context).get(pillId);

            String group = UUID.randomUUID().toString();
            Date d = new Date();
            if (pill != null) {
                for(int i = 0; i < quantity; i++) {
                    Consumption consumption = new Consumption(pill, d, group);
                    new InsertConsumptionTask(context, consumption).execute();
                }
            }
            TrackerHelper.widgetClickedEvent(context, "MyAppWidgetProvider");
            Toast.makeText(context, quantity + " " + pill.getName() + " added", Toast.LENGTH_SHORT).show();
        }
        else {
            updateAllWidgets(context);
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
