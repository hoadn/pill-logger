package uk.co.pilllogger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.AppWidgetConfigure;
import uk.co.pilllogger.helpers.ColourHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
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
        SharedPreferences preferences = context.getSharedPreferences("widgets", Context.MODE_MULTI_PROCESS);

        Map<Pill, Integer> pills = new HashMap<Pill, Integer>();

        String name = preferences.getString("widgetName" + id, "");
        int colour = preferences.getInt("widgetColour" + id, -1);

        int totalQuantity = 0;
        int i = 0;
        boolean found;
        do{
            found = false;

            int pillId = preferences.getInt("widgetPill" + i + id, -1);
            int quantity = preferences.getInt("widgetQuantity" + i + id, 1);

            totalQuantity += quantity;

            if(pillId == -1) {
                continue;
            }

            Pill pill = PillRepository.getSingleton(context).get(pillId);

            if(pill == null){
                continue;
            }

            found = true;

            pills.put(pill, quantity);

            i++;
        } while(found);

        if(pills.isEmpty()){
            return;
        }

        String indicatorChar = context.getString(R.string.widget_quantity_indicator);
        String indicatorOverloadChar = context.getString(R.string.widget_quantity_overload_indicator);

        Intent newIntent = new Intent(context, MyAppWidgetProvider.class);
        newIntent.setAction(CLICK_ACTION);

        Pill firstPill = null;

        i = 0;
        for(Pill pill : pills.keySet()) {
            if(firstPill == null) {
                firstPill = pill;
            }

            newIntent.putExtra(AppWidgetConfigure.PILL_ID + i, pill.getId());
            newIntent.putExtra(AppWidgetConfigure.PILL_QUANTITY + i, pills.get(pill));

            i++;
        }

        //Create a pending intent from our intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.appwidget);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        if(pills.size() == 1 && firstPill.getSize() > 0) {
            views.setTextViewText(R.id.widget_size, String.valueOf(NumberHelper.getNiceFloatString(firstPill.getSize()) + firstPill.getUnits()));
        }

        String pillName = name;
        views.setTextViewText(R.id.widget_text, pillName.substring(0, Math.min(pillName.length(), 3)));

        String quantityIndicator = "";

        if(totalQuantity > 3)
            quantityIndicator = String.format("%s %s %s", indicatorChar, indicatorChar, indicatorOverloadChar);
        else{
            for(int j = 0; j < totalQuantity; j++) {
                if(j > 0)
                    quantityIndicator += " ";

                quantityIndicator += indicatorChar;
            }
        }

        views.setTextViewText(R.id.widget_quantity_indicator, quantityIndicator);

        WidgetIndicator indicator = new WidgetIndicator(context);
        indicator.setColour(colour, true);
        int widgetSize = context.getResources().getDimensionPixelSize(R.dimen.widget_size);
        indicator.measure(widgetSize, widgetSize);
        indicator.layout(0, 0, widgetSize, widgetSize);
        indicator.setDrawingCacheEnabled(true);
        Bitmap bitmap = indicator.getDrawingCache();
        views.setImageViewBitmap(R.id.widget_colour_indicator, bitmap);

        int sizeTextColour = R.color.white;
        if (ColourHelper.isColourLight(colour)) {
            sizeTextColour = R.color.text_grey;
        }
        views.setTextColor(R.id.widget_size, context.getResources().getColor(sizeTextColour));

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
            TrackerHelper.addConsumptionEvent(context, "Widget");
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
