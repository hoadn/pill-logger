package uk.co.pilllogger.helpers;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 13/11/2013.
 */
public class DateHelper {

    public static String getPrettyDayOfMonth(DateTime dateTime){
        String dayOfMonth = dateTime.dayOfMonth().getAsText();

        String suffix = "";
        if(dayOfMonth.endsWith("1")) suffix = "st";
        if(dayOfMonth.endsWith("2")) suffix = "nd";
        if(dayOfMonth.endsWith("3")) suffix= "rd";
        if(dayOfMonth.endsWith("0") || dayOfMonth.endsWith("4") || dayOfMonth.endsWith("5") || dayOfMonth.endsWith("6")
                || dayOfMonth.endsWith("7") || dayOfMonth.endsWith("8") || dayOfMonth.endsWith("9")) suffix = "th";

        return dateTime.toString("EEEE") + " " + dayOfMonth + suffix + " " + dateTime.toString("MMMM");
    }

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
        if (isDateInFuture(date)) {
            dateString = setAsDateAndTime(context, date);
        }
        else if(date.plusHours(6).isAfterNow()){
            // hours ago
            long timeMs = System.currentTimeMillis() - date.getMillis();
            long minutes = timeMs / 1000 / 60;
            String minutePlural = (minutes == 1) ? "minute" : "minutes";
            dateString = String.valueOf(minutes) + " " + minutePlural + " ago";
            if (minutes > 60) {
                long hours = minutes / 60;
                long leftOverMinutes = minutes % 60;
                String hourPlural = (hours == 1) ? "hour" : "hours";
                minutePlural = (leftOverMinutes == 1) ? "minute" : "minutes";
                dateString = String.valueOf(hours) + " " + hourPlural + " " + leftOverMinutes + " " + minutePlural + " ago";
            }
        }
        else if(date.plusDays(2).isAfterNow()){
            // yesterday / x days ago
            dateString = (String) DateUtils.getRelativeTimeSpanString(date.getMillis(), DateTime.now().getMillis(), DateUtils.DAY_IN_MILLIS);

            // at {time}
            dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
        }
        else{
            dateString = setAsDateAndTime(context, date);
        }

        return dateString;
    }

    private static String setAsDateAndTime(Context context, DateTime date) {
        String dateString;
        // on {date}
        dateString = (String)DateUtils.getRelativeTimeSpanString(context, date.getMillis(), true);

        // at {time}
        return dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
    }

    public static boolean isDateInFuture(Date date) {
        Date currentDate = new Date();
        return date.compareTo(currentDate) > 0;
    }

    public static boolean isDateInFuture(DateTime date) {
        DateTime currentDate = new DateTime();
        return date.compareTo(currentDate) > 0;
    }

    public static String getTime(Context context, Date date){
        java.text.DateFormat df = DateFormat.getTimeFormat(context);
        return df.format(date);
    }

    public static String getTime(Context context, DateTime date){
        return getTime(context, date.toDate());
    }

    public static String formatDateAndTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy kk:mm");
        return simpleDateFormat.format(date);
    }
}
