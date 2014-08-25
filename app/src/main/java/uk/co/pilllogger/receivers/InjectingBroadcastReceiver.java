package uk.co.pilllogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dagger.ObjectGraph;
import uk.co.pilllogger.App;

/**
 * Created by Alex on 25/08/2014
 * in uk.co.pilllogger.receivers.
 */
public abstract class InjectingBroadcastReceiver extends BroadcastReceiver {
    private ObjectGraph _objectGraph;

    @Override
    public void onReceive(Context context, Intent intent) {
        // extend the application-scope object graph with the modules for this broadcast receiver
        _objectGraph = ((App) context.getApplicationContext()).getObjectGraph();
        // then inject ourselves
        _objectGraph.inject(this);
    }
}
