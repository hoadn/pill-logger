/**
 * 
 */
package uk.co.pilllogger.helpers;

import com.crashlytics.android.Crashlytics;

/**
 * @author alex
 *
 */
public class Logger {

    private static LogLevel _logLevel = LogLevel.Verbose;

	public static void e(String tag, String message){
        if(_logLevel != LogLevel.Off) {
            android.util.Log.e(tag, message);
            Crashlytics.log(1, tag, message);
        }
	}
	
	public static void e(String tag, String message, Throwable ex){
        if(_logLevel != LogLevel.Off) {
            android.util.Log.e(tag, message, ex);
            Crashlytics.logException(ex);
        }
	}

    public static void i(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.i(tag, message);
            Crashlytics.log(message);
        }
    }

    public static void i(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.i(tag, message, ex);
            Crashlytics.log(message);
        }
    }

    public static void d(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose){
            android.util.Log.d(tag, message);
            Crashlytics.log(message);
        }
    }

    public static void d(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.d(tag, message, ex);
            Crashlytics.log(message);
        }
    }

    public static void w(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.w(tag, message);
            Crashlytics.log(message);
        }
    }

    public static void w(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.w(tag, message, ex);
            Crashlytics.log(message);
        }
    }

    public static void v(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.v(tag, message);
            Crashlytics.log(message);
        }
    }

    public static void v(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose) {
            android.util.Log.v(tag, message, ex);
            Crashlytics.log(message);
        }
    }

    private enum LogLevel{
        Verbose,
        Debug,
        Information,
        Warn,
        Error,
        Off
    }
}
