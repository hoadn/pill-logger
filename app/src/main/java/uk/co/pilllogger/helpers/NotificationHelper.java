package uk.co.pilllogger.helpers;

import java.util.*;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.AddConsumptionActivity;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public abstract class NotificationHelper {


    private static final String TAG = "NotificationHelper";

    public NotificationHelper() {
        // TODO Auto-generated constructor stub
    }

    public static NotificationManager getNotificationManager(Context context){
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void clearNotification(Context context, int id){
        getNotificationManager(context).cancel(id);
    }

    public static void Notification(Context context, boolean showText, String sound, boolean lights, boolean vibrate, List<Consumption> consumptions){
        Log.d(TAG, "Notification triggered");
        if(State.getSingleton().isAppVisible() || consumptions == null || consumptions.size() == 0) {
            return; //don't notify if the app is already in front
        }

        String title = context.getString(R.string.notification_reminder_title);
        String content = context.getString(R.string.notification_reminder_content_prefix);

        content = content.trim();
        for(int i = 0; i < consumptions.size(); i++){
            Consumption c = consumptions.get(i);
            content += " " + c.getQuantity() + " " + c.getPill().getName();

            if(consumptions.size() == 0)
                continue;

            if(i == consumptions.size() - 2){
                content += " and";
            } else {
                content += ", ";
            }
        }

        content += " " + DateHelper.getRelativeDateTime(context, consumptions.get(0).getDate());

        String ticker = String.format("%s: %s", title, content);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sound = sharedPreferences.getString(context.getString(R.string.pref_key_reminder_sound), "DEFAULT_SOUND");
        vibrate = sharedPreferences.getBoolean(context.getString(R.string.pref_key_reminder_vibrate), false);

        Uri soundUri = Uri.parse(sound);

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(bd.getBitmap())
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(new Date().getTime())
                        .setSound(soundUri);


        if(showText){
            builder.setTicker(ticker);
        }
        else{
            builder.setTicker(null);
        }

        if(vibrate)
            builder.setDefaults(Notification.DEFAULT_VIBRATE);

        if(!lights)
            builder.setLights(0, 0, 0);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, AddConsumptionActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AddConsumptionActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        Notification n = builder.build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        if(lights){
            n.flags |= Notification.FLAG_SHOW_LIGHTS;
            n.ledARGB = 0xff00ff00;
            n.ledOnMS = 300;
            n.ledOffMS = 1000;
        }

        notificationManager.notify(R.id.notification_consumption_reminder, n);
    }
}

