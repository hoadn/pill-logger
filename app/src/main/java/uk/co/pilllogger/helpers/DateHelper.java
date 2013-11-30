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

    public static String getRelativeDateTime(Context context, Date date){
        return getRelativeDateTime(context, new DateTime(date));
    }

    public static String getRelativeDateTime(Context context, DateTime date){

        String dateString;
        if(date.plusHours(6).isAfterNow()){
            dateString = (String) DateUtils.getRelativeTimeSpanString(date.getMillis(), DateTime.now().getMillis(), DateUtils.SECOND_IN_MILLIS);
        }
        else if(date.plusDays(2).isAfterNow()){
            dateString = (String) DateUtils.getRelativeTimeSpanString(date.getMillis(), DateTime.now().getMillis(), DateUtils.DAY_IN_MILLIS);
            dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.plusDays(1).getMillis(), true);
        }
        else{
            dateString = (String)DateUtils.getRelativeTimeSpanString(context, date.getMillis(), true);
            dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
        }

        return dateString;
    }
}
