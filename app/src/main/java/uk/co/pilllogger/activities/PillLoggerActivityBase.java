package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.squareup.otto.Bus;

import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerActivityBase extends Activity {

    Bus _bus;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        _bus = State.getSingleton().getBus();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        if (isDebuggable)
            EasyTracker.getInstance(this).set(Fields.EX_DESCRIPTION, "Dev");

        EasyTracker.getInstance(this).activityStart(this);

        TrackerHelper.initMixPanel(this);
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
        _bus.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();

        State.getSingleton().setAppVisible(false);
        _bus.unregister(this);
    }
}
