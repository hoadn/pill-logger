package uk.co.pilllogger.helpers;

import java.util.*;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import uk.co.pilllogger.state.State;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public abstract class NotificationHelper {


    public NotificationHelper() {
        // TODO Auto-generated constructor stub
    }

    public static NotificationManager getNotificationManager(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        return notificationManager;
    }

    public static void clearNotification(Context context, int id){
        getNotificationManager(context).cancel(id);
    }

    public static void Notification(Context context, boolean showText, String sound, boolean lights, boolean vibrate, int key, List<Tweet> tweets){
        if(State.getSingleton().isAppVisible())
            return; //don't notify if the app is already in front

        CharSequence title = "";
        CharSequence content = "";
        CharSequence ticker = "";
        Bitmap largeIcon = null;
        Tweet firstTweet = tweets.get(0);

        Uri soundUri = Uri.parse(sound);

        if(tweets.size() == 1){
            title = String.format("%s", firstTweet.get_tweeterName());
            content = firstTweet.get_cachedText();
            ticker = String.format("%s: %s", title, content);
            BitmapDrawable d = (BitmapDrawable)TweetHelper.getTweeterAvatar(context, firstTweet.getUserIdString());
            if(d != null){
                largeIcon = d.getBitmap();

                Resources res = context.getResources();
                int height = (int) (res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height) * 0.9);
                int width =  (int) (res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width) * 0.9);
                largeIcon = Bitmap.createScaledBitmap(largeIcon, width, height, false);
            }
        }
        else{
            title = "New tweets";
            content = String.format("%d new tweets", tweets.size());
            ticker = "New tweets";
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(firstTweet.getCreatedAt().getTime())
                        .setLargeIcon(largeIcon)
                        .setSound(soundUri);


        if(showText){
            builder.setTicker(ticker);
        }
        else{
            builder.setTicker(null);
        }

        if(!vibrate)
            builder.setVibrate(new long[]{});

        if(!lights)
            builder.setLights(0, 0, 0);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, TimelineActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("New tweets");


        if(tweets.size() > 1){
            CharSequence line = "";
            for (int i=0; i < tweets.size(); i++) {
                Tweet tweet = tweets.get(i);
                title = String.format("%s", tweet.get_tweeterName());
                content = tweet.get_cachedText();
                line = String.format("%s: %s", title, content);
                inboxStyle.addLine(line);
            }

            builder.setStyle(inboxStyle);
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TimelineActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
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

        mNotificationManager.notify(Constants.NOTIFICATION_TWEET, n);
    }


    public static void TweetNotification(Context context, Tweet tweet){
        List<Tweet> tweets = new ArrayList<Tweet>();
        tweets.add(tweet);
        TweetNotification(context, tweets);
    }

    public static void TweetNotification(Context context, List<Tweet> tweets){
        if(tweets == null || tweets.size() == 0)
            return;

        if(!NotificationPreferences.getSingleton().useNotifications() || !NotificationPreferences.getSingleton().useTweetNotifications()) //notifications are disabled;
            return;

        boolean showText = NotificationPreferences.getSingleton().isTweetsText();
        String sound = NotificationPreferences.getSingleton().getTweetsSound();
        boolean flashLight = NotificationPreferences.getSingleton().isTweetsLight();
        boolean vibrate = NotificationPreferences.getSingleton().isTweetsVibrate();

        long latestTweetId = TweetState.getSingleton().getLatestReadTweetId();
        if(latestTweetId <= 0)
            latestTweetId = TweetState.getSingleton().initLatestReadTweetId(context);

        try {
            tweets = new GetTweetsFromDbTask(context, null).execute(-1L, latestTweetId + 1).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Notification(context, showText, sound, flashLight, vibrate, Constants.NOTIFICATION_TWEET, tweets);
    }

    public static void MentionNotification(Context context, List<Tweet> tweets){
        if(tweets == null || tweets.size() == 0)
            return;

        if(!NotificationPreferences.getSingleton().useNotifications() || !NotificationPreferences.getSingleton().useMentionNotifications()) //notifications are disabled;
            return;

        boolean showText = NotificationPreferences.getSingleton().isMentionsText();
        String sound = NotificationPreferences.getSingleton().getMentionsSound();
        boolean flashLight = NotificationPreferences.getSingleton().isMentionsLight();
        boolean vibrate = NotificationPreferences.getSingleton().isMentionsVibrate();

        long latestTweetId = MentionState.getSingleton().getLatestReadTweetId();
        if(latestTweetId <= 0)
            latestTweetId = MentionState.getSingleton().initLatestReadTweetId(context);

        try {
            tweets = new GetMentionsFromDbTask(context, null).execute(-1L, latestTweetId + 1).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Notification(context, showText, sound, flashLight, vibrate, Constants.NOTIFICATION_MENTION, tweets);
    }

    public static void ImageUploadNotification(Context context){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(context.getString(R.string.notification_image_upload_title))
                        .setContentText(context.getString(R.string.notification_image_upload_text))
                        .setWhen(new Date().getTime())
                        .setProgress(0, 0, true);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        Notification n = builder.build();

        mNotificationManager.notify(Constants.NOTIFICATION_IMAGE_UPLOAD, n);
    }

    public static void TweetFailedNotification(Context context, FailedTweet tweet){
        if(State.getSingleton().isAppVisible())
            return; //don't notify if the app is already in front

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(context.getString(R.string.notification_tweet_failed_title))
                        .setContentText(context.getString(R.string.notification_tweet_failed_text))
                        .setWhen(new Date().getTime());

        Intent resultIntent = new Intent(context, ComposeActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        resultIntent.putExtra("failedTweet", tweet);
        resultIntent.putExtra("image", tweet.getPhotoPath());
        resultIntent.putExtra("type", Constants.COMPOSE_TYPE_FAILED);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ComposeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        Notification n = builder.build();

        mNotificationManager.notify(Constants.NOTIFICATION_TWEET_FAILED, n);
    }
}

