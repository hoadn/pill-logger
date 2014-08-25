package uk.co.pilllogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.LoadedConsumptionsEvent;
import uk.co.pilllogger.helpers.NotificationHelper;
import uk.co.pilllogger.jobs.LoadConsumptionsJob;
import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 02/03/14.
 */
public class ReminderReceiver extends InjectingBroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private Context _context;
    private String _group;

    @Inject
    JobManager _jobManager;

    @Inject
    Bus _bus;

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        _context = context;
        Log.d(TAG, "Intent received");

        _group = intent.getStringExtra(context.getString(R.string.intent_extra_notification_consumption_group));

        Log.d(TAG, "Group: " + _group);

        _bus.register(this);
        _jobManager.addJobInBackground(new LoadConsumptionsJob(true, _group));
    }

    @Subscribe
    public void consumptionsReceived(LoadedConsumptionsEvent event) {
        if(event.getConsumptions().size() > 0)
            NotificationHelper.Notification(_context, true, "", true, true, event.getConsumptions(), _group);

        _bus.unregister(this);
    }


}
