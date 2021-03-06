package uk.co.pilllogger.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

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

    public static void hideKeyboard(Activity activity) {
        if(activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
