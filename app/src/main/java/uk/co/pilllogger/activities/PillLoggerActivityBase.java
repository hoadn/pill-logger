package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.squareup.otto.Bus;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import uk.co.pilllogger.App;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public abstract class PillLoggerActivityBase extends Activity {

    private ObjectGraph _activityGraph;
    Bus _bus;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        App application = (App) getApplication();
        _activityGraph = application.createScopedGraph(getModules().toArray());
        _activityGraph.inject(this);

        _bus = State.getSingleton().getBus();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        _activityGraph = null;
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

    public ObjectGraph getActivityGraph(){return _activityGraph;}

    protected List<Object> getModules() {
        return Arrays.<Object>asList();
    }
}
