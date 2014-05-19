package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.UUID;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerActivityBase extends Activity {

    public static final String MIXPANEL_TOKEN = "7490c73ddbe4deb70b216f00c5497bc3";

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @Override
    protected void onStart(){
        super.onStart();

        boolean isDebuggable = ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

        if(isDebuggable)
            EasyTracker.getInstance(this).set(Fields.EX_DESCRIPTION, "Dev");

        EasyTracker.getInstance(this).activityStart(this);

        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
        Logger.v("PillLoggerActivityBase", "DistinctId PillLoggerActivityBase: " + mixpanelAPI.getDistinctId());
        State.getSingleton().setMixpanelAPI(mixpanelAPI);
        mixpanelAPI.identify(getUniqueId());

    }

    @Override
    protected void onStop(){
        super.onStop();

        EasyTracker.getInstance(this).activityStop(this);

        State.getSingleton().getMixpanelAPI().flush();
    }

    @Override
    protected void onResume(){
        super.onResume();

        State.getSingleton().setAppVisible(true);
    }

    @Override
    protected void onPause(){
        super.onPause();

        State.getSingleton().setAppVisible(false);
    }

    protected synchronized String getUniqueId() {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }
}
