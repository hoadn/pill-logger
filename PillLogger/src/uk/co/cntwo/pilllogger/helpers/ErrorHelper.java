/**
 * 
 */
package uk.co.cntwo.pilllogger.helpers;

import uk.co.cntwo.pilllogger.R;
import android.content.Context;
import android.util.Log;

/**
 * @author alex
 *
 */
public class ErrorHelper {
	public static void logError(Context context, String message){
		Log.i(context.getString(R.string.app_name), message);
	}
	
	public static void logError(Context context, String message, Throwable ex){
		Log.i(context.getString(R.string.app_name), message, ex);
	
	}
}
