package uk.co.pilllogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.NotificationHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.tasks.GetConsumptionsTask;

/**
 * Created by Alex on 02/03/14.
 */
public class ReminderReceiver extends BroadcastReceiver implements GetConsumptionsTask.ITaskComplete {
    private static final String TAG = "ReminderReceiver";
    private Context _context;
    private String _group;

    @Override
    public void onReceive(final Context context, Intent intent) {
        _context = context;
        Log.d(TAG, "Intent received");

        _group = intent.getStringExtra(context.getString(R.string.intent_extra_notification_consumption_group));

        Log.d(TAG, "Group: " + _group);

        new GetConsumptionsTask(context, this, true, _group).execute();

    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if(consumptions != null && consumptions.size() > 0)
            NotificationHelper.Notification(_context, true, "", true, true, consumptions, _group);
    }
}
