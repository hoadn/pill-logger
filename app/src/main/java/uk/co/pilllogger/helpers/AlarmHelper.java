package uk.co.pilllogger.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 05/03/14.
 */
public abstract class AlarmHelper {
    public static void addReminderAlarm(Context context, Date date, String group, boolean toast){

        long difference = date.getTime() - new Date().getTime();

        Intent intent = new Intent(context.getString(R.string.intent_reminder));
        intent.putExtra(context.getString(R.string.intent_extra_notification_consumption_group), group);

        PendingIntent pi = PendingIntent.getBroadcast(context, date.hashCode(), intent, 0);
        AlarmManager am = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));

        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + difference, pi);

        if(toast){
            Toast.makeText(context, context.getString(R.string.add_consumption_reminder_toast_prefix) + " " + DateHelper.getTime(context, date), Toast.LENGTH_SHORT).show();
        }
    }
}
