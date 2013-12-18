package uk.co.pilllogger.helpers;

import android.content.Context;
import android.text.format.DateUtils;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by alex on 13/11/2013.
 */
public class DateHelper {
    public static int daysOfMonth(int year, int month, int day) {
        DateTime dateTime = new DateTime(year, month, day, 12, 0, 0, 000);
        return daysOfMonth(dateTime);
    }

    public static int daysOfMonth(){
        DateTime dateTime = new DateTime();
        return daysOfMonth(dateTime);
    }

    public static int daysOfMonth(DateTime dateTime){
        return dateTime.dayOfMonth().getMaximumValue();
    }

    public static String getRelativeDate(Context context, Date date){
        return getRelativeDate(context, new DateTime(date));
    }

    public static String getRelativeDate(Context context, DateTime date){
        return String.valueOf(DateUtils.getRelativeTimeSpanString(DateTime.now().getMillis(), date.getMillis(), DateUtils.DAY_IN_MILLIS));
    }

    public static String getRelativeDateTime(Context context, Date date){
        return getRelativeDateTime(context, new DateTime(date));
    }

    public static String getRelativeDateTime(Context context, DateTime date){

        String dateString;
        if(date.plusHours(6).isAfterNow()){
            // hours ago
            dateString = (String) DateUtils.getRelativeTimeSpanString(date.getMillis(), DateTime.now().getMillis(), DateUtils.SECOND_IN_MILLIS);
        }
        else if(date.plusDays(2).isAfterNow()){
            // yesterday / x days ago
            dateString = (String) DateUtils.getRelativeTimeSpanString(date.getMillis(), DateTime.now().getMillis(), DateUtils.DAY_IN_MILLIS);

            // at {time}
            dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
        }
        else{
            // on {date}
            dateString = (String)DateUtils.getRelativeTimeSpanString(context, date.getMillis(), true);

            // at {time}
            dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
        }

        return dateString;
    }
}
