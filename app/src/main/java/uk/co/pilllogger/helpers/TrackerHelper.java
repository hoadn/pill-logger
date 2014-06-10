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
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;

/**
 * Created by Alex on 09/03/14.
 */
public class TrackerHelper {

    public static final String MIXPANEL_TOKEN = "7490c73ddbe4deb70b216f00c5497bc3";

    private static String _uniqueId = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

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
        if(mixpanelAPI == null)
            mixpanelAPI = initMixPanel(context);



        mixpanelAPI.track(action, props);
    }

    public static void launchEvent(Context context){
        boolean firstRun = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("firstRun", true);

        MixpanelAPI mixpanelAPi = State.getSingleton().getMixpanelAPI();

        if(mixpanelAPi == null)
            mixpanelAPi = initMixPanel(context);

        JSONObject props = new JSONObject();
        try {
            props.put("FirstRun", firstRun);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mixpanelAPi.track("Launch", props);

        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("firstRun", false).apply();
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
        people.setOnce("First Seen", new Date());
        people.set("Medicines", pills);

        int totalConsumptions = Statistics.getInstance(context).getTotalConsumptions(consumptions);
        people.set("Consumptions", totalConsumptions);

        String theme = defaultSharedPreferences.getString(context.getString(R.string.pref_key_theme_list), context.getString(R.string.professionalTheme));
        people.set("Theme", theme);

        String medicationOrder = defaultSharedPreferences.getString(context.getString(R.string.pref_key_medication_list_order), context.getString(R.string.order_created));
        people.set("Medication Sort Order", medicationOrder);

        Boolean reversedOrder = defaultSharedPreferences.getBoolean(context.getString(R.string.pref_key_reverse_order), false);
        people.set("Reversed Sort Order", reversedOrder);

        Logger.v("TrackerHelper", "DistinctId ProfileUpdate: " + people.getDistinctId());
    }

    public static MixpanelAPI initMixPanel(Context context){
        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
        Logger.v("PillLoggerActivityBase", "DistinctId PillLoggerActivityBase: " + mixpanelAPI.getDistinctId());
        State.getSingleton().setMixpanelAPI(mixpanelAPI);
        String uniqueId = getUniqueId(context);
        mixpanelAPI.getPeople().identify(uniqueId);
        mixpanelAPI.identify(uniqueId);

        return mixpanelAPI;
    }

    private static synchronized String getUniqueId(Context context) {
        if (_uniqueId == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            _uniqueId = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (_uniqueId == null) {
                _uniqueId = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, _uniqueId);
                editor.commit();
            }
        }
        return _uniqueId;
    }
}
