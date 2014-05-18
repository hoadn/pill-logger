package uk.co.pilllogger.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 14/04/2014
 * in uk.co.pilllogger.helpers.
 */
public class FeedbackHelper {

    public static void sendFeedbackIntent(Context context) {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("support@allendev.co") +
                "?subject=" + Uri.encode(context.getString(R.string.support_subject)) +
                "&body=" + Uri.encode("");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        context.startActivity(Intent.createChooser(send, context.getString(R.string.feedback_chooser_title)));
    }
}
