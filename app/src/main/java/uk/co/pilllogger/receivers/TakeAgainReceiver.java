package uk.co.pilllogger.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import uk.co.pilllogger.R;
import uk.co.pilllogger.events.LoadedConsumptionsEvent;
import uk.co.pilllogger.helpers.NotificationHelper;
import uk.co.pilllogger.jobs.InsertConsumptionsJob;
import uk.co.pilllogger.jobs.LoadConsumptionsJob;
import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 05/03/14.
 */
public class TakeAgainReceiver extends InjectingBroadcastReceiver {
    private static final String TAG = "TakeAgainReceiver";
    private Context _context;

    @Inject
    JobManager _jobManager;

    @Inject
    Bus _bus;

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        _context = context;
        Log.d(TAG, "Intent received");

        String group = intent.getStringExtra(context.getString(R.string.intent_extra_notification_consumption_group));

        Log.d(TAG, "Group: " + group);
        _bus.register(this);
        _jobManager.addJobInBackground(new LoadConsumptionsJob(false, group));

    }

    @Subscribe
    public void consumptionsReceived(LoadedConsumptionsEvent event) {

        String consumptionGroup = UUID.randomUUID().toString();
        Date consumptionDate = new Date();
        for(Consumption c : event.getConsumptions()){
            Consumption newC = new Consumption(c);
            newC.setId(0);
            newC.setDate(consumptionDate);
            newC.setGroup(consumptionGroup);

            _jobManager.addJobInBackground(new InsertConsumptionsJob(newC));
        }

        NotificationHelper.clearNotification(_context, R.id.notification_consumption_reminder);

        _bus.unregister(this);
    }
}
