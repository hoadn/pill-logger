package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;

import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerActivityBase extends FragmentActivity {
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
}
