package uk.co.cntwo.pilllogger.helpers;

import android.content.Context;
import android.view.View;

/**
 * Created by nick on 25/10/13.
 */
public class LayoutHelper {

    public static float dpToPx(Context context, float dp){
        if(context == null) return dp;
        return (dp * context.getResources().getDisplayMetrics().density);
    }

    public static float pxToDp(Context context, float px){
        if(context == null) return px;
        return (px / context.getResources().getDisplayMetrics().density);
    }

}
