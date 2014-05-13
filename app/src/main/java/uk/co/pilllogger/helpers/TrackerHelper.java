package uk.co.pilllogger.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;

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
                                value)              // Event value
                        .build()
        );

        JSONObject props = new JSONObject();
        try {
            props.put("Source", label);

            if(value != null){
                props.put("Value", value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MixpanelAPI mixpanelAPI = State.getSingleton().getMixpanelAPI();
        if(mixpanelAPI != null)
            mixpanelAPI.track(action, props);
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

    public static void updateUserProfile(Context context, int pills, List<Consumption> consumptions){
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        MixpanelAPI.People people = State.getSingleton().getMixpanelAPI().getPeople();
        people.setOnce("First seen", new Date());
        people.set("Medicines", pills);

        int totalConsumptions = Statistics.getInstance(context).getTotalConsumptions(consumptions);
        people.set("Consumptions", totalConsumptions);

        String theme = defaultSharedPreferences.getString(context.getString(R.string.pref_key_theme_list), context.getString(R.string.professionalTheme));

        people.set("Theme", theme);

        String medicationOrder = defaultSharedPreferences.getString(context.getString(R.string.pref_key_medication_list_order), context.getString(R.string.order_created));

        people.set("Medication Sort Order", medicationOrder);

        Boolean reversedOrder = defaultSharedPreferences.getBoolean(context.getString(R.string.pref_key_reverse_order), false);

        people.set("Reversed Sort Order", reversedOrder);
    }
}
