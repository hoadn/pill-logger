package uk.co.pilllogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.AlarmHelper;
import uk.co.pilllogger.helpers.NotificationHelper;

/**
 * Created by Alex on 05/03/14.
 */
public class DelayReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "DelayReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent received");

        String group = intent.getStringExtra(context.getString(R.string.intent_extra_notification_consumption_group));

        Log.d(TAG, "Group: " + group);

        Date oneHour = DateTime.now().plusHours(1).toDate();

        AlarmHelper.addReminderAlarm(context, oneHour, group, true);

        NotificationHelper.clearNotification(context, R.id.notification_consumption_reminder);
    }
}
