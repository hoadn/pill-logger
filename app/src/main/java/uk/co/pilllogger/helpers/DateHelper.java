package uk.co.pilllogger.helpers;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.widget.Spinner;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 13/11/2013.
 */
public class DateHelper {

    public static String DATE_FORMAT = "E, MMM dd, yyyy";

    public static String getPrettyDayOfMonth(DateTime dateTime){
        String dayOfMonth = dateTime.dayOfMonth().getAsText();

        String suffix = "";
        if(dayOfMonth.endsWith("1")) suffix = "st";
        if(dayOfMonth.endsWith("2")) suffix = "nd";
        if(dayOfMonth.endsWith("3")) suffix= "rd";
        if(dayOfMonth.endsWith("0") || dayOfMonth.endsWith("4") || dayOfMonth.endsWith("5") || dayOfMonth.endsWith("6")
                || dayOfMonth.endsWith("7") || dayOfMonth.endsWith("8") || dayOfMonth.endsWith("9")) suffix = "th";

        if(dayOfMonth.length() > 1 && dayOfMonth.charAt(dayOfMonth.length() - 2) == '1')
            suffix = "th";

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
        return getRelativeDateTime(context, date, false);
    }

    public static String getRelativeDateTime(Context context, Date date, boolean isPrefixed){
        return getDateTimeStringImpl(context, new DateTime(date), isPrefixed, true);
    }

    public static String getRelativeDateTime(Context context, DateTime date){
        return getDateTimeStringImpl(context, date, false, true);
    }

    public static String getAbsoluteDateTime(Context context, Date date){
        return getAbsoluteDateTime(context, date, false);
    }

    public static String getAbsoluteDateTime(Context context, Date date, boolean isPrefixed){
        return getDateTimeStringImpl(context, new DateTime(date), isPrefixed, false);
    }

    public static String getAbsoluteDateTime(Context context, DateTime date){
        return getDateTimeStringImpl(context, date, false, false);
    }

    public static String getUserPreferenceDateTime(Context context, Date date){
        return getUserPreferenceDateTime(context, date, false);
    }

    public static String getUserPreferenceDateTime(Context context, Date date, boolean isPrefixed){
        return getDateTimeStringImpl(context, new DateTime(date), isPrefixed, State.getSingleton().isUseRelativeTimes());
    }

    public static String getUserPreferenceDateTime(Context context, DateTime date){
        return getDateTimeStringImpl(context, date, false, State.getSingleton().isUseRelativeTimes());
    }

    private static String getDateTimeStringImpl(Context context, DateTime date, boolean isPrefixed, boolean isRelative){
        String dateString;
        if (isDateInFuture(date)) {
            dateString = setAsDateAndTime(context, date);
        }
        else if(date.plusHours(23).isAfterNow() && isRelative){
            // hours ago
            long timeMs = System.currentTimeMillis() - date.getMillis();
            long minutes = timeMs / 1000 / 60;
            String minuteSuffix = (minutes == 1) ? "m" : "m";
            dateString = String.valueOf(minutes) + "" + minuteSuffix + " ago";

            if(minutes == 0)
                dateString = context.getString(R.string.just_now);

            if (minutes > 60) {
                long hours = minutes / 60;
                long leftOverMinutes = minutes % 60;
                String hourSuffix = (hours == 1) ? "h" : "h";
                minuteSuffix = (leftOverMinutes == 1) ? "m" : "m";
                dateString = String.valueOf(hours) + "" + hourSuffix + " ";
                if(minutes > 0)
                    dateString += leftOverMinutes + "" + minuteSuffix;
                dateString += " ago";
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

        if(isPrefixed){
            dateString = dateString.substring(0, 1).toLowerCase() + dateString.substring(1);
        }

        return dateString;
    }

    private static String setAsDateAndTime(Context context, DateTime date) {
        String dateString;
        // on {date}
        if (date.getDayOfYear() == new DateTime().getDayOfYear()
                && date.getYear() == new DateTime().getYear()) {
            return "Today at " + (String)DateUtils.getRelativeTimeSpanString(context, date.getMillis(), false);
        }
        dateString = (String)DateUtils.getRelativeTimeSpanString(context, date.getMillis(), false);

        // at {time}
        dateString += " " + DateUtils.getRelativeTimeSpanString(context, date.withDate(DateTime.now().year().get(), DateTime.now().monthOfYear().get(), DateTime.now().getDayOfMonth()).getMillis(), true);
        return dateString;
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

    public static String formatDateAndTime(Context context, Date date) {
        java.text.DateFormat df = DateFormat.getDateFormat(context);
        return df.format(date);
    }

    public static String formatDateAndTimeMedium(Context context, Date date){
        java.text.DateFormat df = DateFormat.getMediumDateFormat(context);
        return df.format(date);
    }

    public static String getDayOfWeek(int dayOfWeek, Context context){
        switch(dayOfWeek){
            case 1:
                return context.getString(R.string.monday);
            case 2:
                return context.getString(R.string.tuesday);
            case 3:
                return context.getString(R.string.wednesday);
            case 4:
                return context.getString(R.string.thursday);
            case 5:
                return context.getString(R.string.friday);
            case 6:
                return context.getString(R.string.saturday);
            case 7:
                return context.getString(R.string.sunday);
        }

        return "";
    }

    public static String getShortDayOfWeek(int dayOfWeek, Context context){
        switch(dayOfWeek){
            case 1:
                return context.getString(R.string.monday_short);
            case 2:
                return context.getString(R.string.tuesday_short);
            case 3:
                return context.getString(R.string.wednesday_short);
            case 4:
                return context.getString(R.string.thursday_short);
            case 5:
                return context.getString(R.string.friday_short);
            case 6:
                return context.getString(R.string.saturday_short);
            case 7:
                return context.getString(R.string.sunday_short);
        }

        return "";
    }

    public static Date getDateFromSpinners(Spinner date, Spinner time, Date defaultDate, Context context) {
        if(date == null || time == null){
            throw new IllegalArgumentException();
        }

        if(date.getSelectedItem() == null
                || time.getSelectedItem() == null)
            return defaultDate;

        String selectedDate = date.getSelectedItem().toString();
        String selectedTime = time.getSelectedItem().toString();

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        java.text.DateFormat tf = DateFormat.getTimeFormat(context);

        try {
            Date parsedDate = format.parse(selectedDate);
            Date parsedTime = tf.parse(selectedTime);

            DateTime parsedDateTime = new DateTime(parsedDate);
            DateTime parsedTimeDateTime = new DateTime(parsedTime);
            return parsedDateTime
                    .withHourOfDay(parsedTimeDateTime.getHourOfDay())
                    .withMinuteOfHour(parsedTimeDateTime.getMinuteOfHour())
                    .toDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return defaultDate;
    }
}
