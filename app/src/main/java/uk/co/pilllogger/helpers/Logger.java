/**
 * 
 */
package uk.co.pilllogger.helpers;

/**
 * @author alex
 *
 */
public class Logger {

    private static LogLevel _logLevel = LogLevel.Verbose;

	public static void e(String tag, String message){
        if(_logLevel != LogLevel.Off)
		    android.util.Log.e(tag, message);
	}
	
	public static void e(String tag, String message, Throwable ex){
        if(_logLevel != LogLevel.Off)
            android.util.Log.e(tag, message, ex);
	}

    public static void i(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message, ex);
    }

    public static void d(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message);
    }

    public static void d(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message, ex);
    }

    public static void w(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message);
    }

    public static void w(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message, ex);
    }

    public static void v(String tag, String message){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message);
    }

    public static void v(String tag, String message, Throwable ex){
        if(_logLevel == LogLevel.Information || _logLevel == LogLevel.Verbose)
            android.util.Log.i(tag, message, ex);
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
