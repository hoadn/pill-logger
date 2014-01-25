package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.pm.ApplicationInfo;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerActivityBase extends Activity {
    @Override
    protected void onStart(){
        super.onStart();

        boolean isDebuggable = ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

        if(isDebuggable)
            EasyTracker.getInstance(this).set(Fields.EX_DESCRIPTION, "Dev");

        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();

        EasyTracker.getInstance(this).activityStop(this);
    }
}
