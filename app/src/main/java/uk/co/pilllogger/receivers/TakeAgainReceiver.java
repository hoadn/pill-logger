package uk.co.pilllogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.NotificationHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.tasks.GetConsumptionsTask;

/**
 * Created by Alex on 05/03/14.
 */
public class TakeAgainReceiver extends BroadcastReceiver implements GetConsumptionsTask.ITaskComplete {
    private static final String TAG = "TakeAgainReceiver";
    private Context _context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        _context = context;
        Log.d(TAG, "Intent received");

        String group = intent.getStringExtra(context.getString(R.string.intent_extra_notification_consumption_group));

        Log.d(TAG, "Group: " + group);

        new GetConsumptionsTask(context, this, false, group).execute();

    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {

        String consumptionGroup = UUID.randomUUID().toString();
        Date consumptionDate = new Date();
        for(Consumption c : consumptions){
            Consumption newC = new Consumption(c);
            newC.setId(0);
            newC.setDate(consumptionDate);
            newC.setGroup(consumptionGroup);

//            try {
//
//                new InsertConsumptionTask(_context, newC).execute().get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
        }

        NotificationHelper.clearNotification(_context, R.id.notification_consumption_reminder);
    }
}
