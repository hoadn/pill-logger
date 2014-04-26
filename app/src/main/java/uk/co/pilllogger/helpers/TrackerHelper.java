package uk.co.pilllogger.helpers;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Created by Alex on 09/03/14.
 */
public class TrackerHelper {
    public static void sendEvent(Context context, String category, String action, String label){
        sendEvent(context, category, action, label, null);
    }

    public static void sendEvent(Context context, String category, String action, String label, Long value){
        EasyTracker easyTracker = EasyTracker.getInstance(context);

        easyTracker.send(MapBuilder
                        .createEvent(category,     // Event category (required)
                                action,            // Event action (required)
                                label,             // Event label
                                null)              // Event value
                        .build()
        );

    }

    public static void addConsumptionEvent(Context context, String source){
        sendEvent(context, "Usage", "AddConsumption", source);
    }

    public static void deleteConsumptionEvent(Context context, String source){
        sendEvent(context, "Usage", "DeleteConsumption", source);
    }

    public static void createPillEvent(Context context, String source){
        sendEvent(context, "Usage", "CreatePill", source);
    }

    public static void updatePillColourEvent(Context context, String source){
        sendEvent(context, "Usage", "UpdatePillColour", source);
    }

    public static void deletePillEvent(Context context, String source){
        sendEvent(context, "Usage", "DeletePill", source);
    }

    public static void showInfoDialogEvent(Context context, String source){
        sendEvent(context, "Usage", "ShowInfoDialog", source);
    }

    public static void filterGraphEvent(Context context, String source){
        sendEvent(context, "Usage", "FilterGraph", source);
    }

    public static void widgetClickedEvent(Context context, String source) {
        sendEvent(context, "Usage", "WidgetClicked", source);
    }

    public static void widgetCreatedEvent(Context context, String source) {
        sendEvent(context, "Usage", "WidgetCreated", source);
    }
}
