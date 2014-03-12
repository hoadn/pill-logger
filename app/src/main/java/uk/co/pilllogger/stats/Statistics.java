package uk.co.pilllogger.stats;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by nick on 07/03/14.
 */
public class Statistics {

    private static List<Consumption> filterConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        List<Consumption> filteredConsumptions = new ArrayList<Consumption>();
        for (Consumption consumption : consumptions) {
            if (consumption.getDate().compareTo(startDate) > 0 && consumption.getDate().compareTo(endDate) < 0)
                filteredConsumptions.add(consumption);
        }
        return filteredConsumptions;
    }

    public static Pill getMostTakenPill(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getMostTakenPill(filterConsumptions(startDate, endDate, consumptions));
    }

    public static Pill getMostTakenPill(List<Consumption> consumptions) { //Does not support grouped consumptions (Use ungrouped)
        if (consumptions == null)
            return null;

        Map<Pill, Integer> amountOfPill = new HashMap<Pill, Integer>();
        for (Consumption consumption : consumptions) {
            Pill pill = consumption.getPill();
            if (amountOfPill.containsKey(pill))
                amountOfPill.put(pill, (amountOfPill.get(pill)) + 1);
            else
                amountOfPill.put(pill, 1);
        }

        Map.Entry<Pill, Integer> mostlyTakenPill = null;
        for (Map.Entry<Pill, Integer> entry : amountOfPill.entrySet()) {
            if (mostlyTakenPill == null || entry.getValue() > mostlyTakenPill.getValue())
                mostlyTakenPill = entry;
        }

        return (mostlyTakenPill == null) ? null : mostlyTakenPill.getKey();
    }

    public static String getDayWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getDayWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public static String getDayWithMostConsumptions(List<Consumption> consumptions) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE"); //Turns date into Day of week e.g Monday
        return getTimeWithMostConsumptions(consumptions, format).getKey();
    }

    public static String getHourWithMmostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getHourWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public static String getHourWithMostConsumptions(List<Consumption> consumptions) {
        SimpleDateFormat format = new SimpleDateFormat("kk"); //Turns date into Day of week e.g Monday
        return getTimeWithMostConsumptions(consumptions, format).getKey();
    }

    private static Map.Entry<String, Integer> getTimeWithMostConsumptions(List<Consumption> consumptions, SimpleDateFormat format) {
        Map<String, Integer> amountPerTime = new HashMap<String, Integer>();

        for (Consumption consumption : consumptions) {
            String day = format.format(consumption.getDate());
            if (amountPerTime.containsKey(day))
                amountPerTime.put(day, amountPerTime.get(day) + 1);
            else
                amountPerTime.put(day, 1);
        }

        Map.Entry<String, Integer> timeWithMostConsumptions = null;
        for (Map.Entry<String, Integer> entry : amountPerTime.entrySet()) {
            if (timeWithMostConsumptions == null || entry.getValue() > timeWithMostConsumptions.getValue())
                timeWithMostConsumptions = entry;
        }

        return timeWithMostConsumptions;
    }

    public static Consumption getLastConsumption(List<Consumption> consumptions) {
        Consumption mostRecentConsumption = null;
        for (Consumption consumption : consumptions) {
            if (mostRecentConsumption == null || consumption.getDate().compareTo(mostRecentConsumption.getDate()) > 0)
                mostRecentConsumption = consumption;
        }
        return mostRecentConsumption;
    }

    public static String getAverageTimeBetweenConsumptions(Date startDate, Date endDate, List<Consumption> consumptions, Context context) {
        return getAverageTimeBetweenConsumptions(filterConsumptions(startDate, endDate, consumptions), context);
    }

    public static String getAverageTimeBetweenConsumptions(List<Consumption> consumptions, Context context) {
        Collections.sort(consumptions);
        consumptions = ConsumptionRepository.getSingleton(context).groupConsumptions(consumptions); //Need grouped consumptions for this to be accurate
        Consumption lastConsumption = consumptions.get(0);
        Consumption firstConsumption = consumptions.get(consumptions.size() - 1);

        int timeDifference = (int) ((lastConsumption.getDate().getTime()) - (firstConsumption.getDate().getTime()));
        int averageTimeDifference = timeDifference / consumptions.size();
        return timeToString(averageTimeDifference);
    }

    private static String timeToString(int time) {
        final int MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;
        final int MILLIS_TO_HOURS = 1000 * 60 * 60;
        final int MILLIS_TO_MINS = 100 * 60 * 60;

        int daysInt = time / MILLIS_TO_DAYS;
        int hoursInt = time / MILLIS_TO_HOURS % 24;
        int minsInt = time / MILLIS_TO_MINS % 60;

        String days = "";
        String hours = "";
        String minutes = "";
        if (daysInt > 0)
            days = daysInt + ((daysInt > 1) ? " days, " : " day, ");
        if (hoursInt > 0)
            hours = hoursInt + ((hoursInt > 1) ? " hours, " : " hour, ");
        if (minsInt > 0)
            minutes = String.valueOf(minsInt) + ((minsInt > 1) ? " minutes" : " minute");
        return days + hours + minutes;
    }

    public static String getLongestTimeBetweenConsumptions(Date startDate, Date endDate, List<Consumption> consumptions, Context context) {
        return getLongestTimeBetweenConsumptions(filterConsumptions(startDate, endDate, consumptions), context);
    }

    public static String getLongestTimeBetweenConsumptions(List<Consumption> consumptions, Context context) {
        Collections.sort(consumptions);
        Consumption lastConsumption = null;
        int longestTimeBetweenConsumptions = 0;
        for(Consumption consumption : consumptions) {
            if (lastConsumption == null) {
                lastConsumption = consumption;
                continue;
            }
            int timeBetweenConsumptions = (int)(lastConsumption.getDate().getTime() - consumption.getDate().getTime());
            if (timeBetweenConsumptions > longestTimeBetweenConsumptions)
                longestTimeBetweenConsumptions = timeBetweenConsumptions;

            lastConsumption = consumption;
        }
        return timeToString(longestTimeBetweenConsumptions);
    }
}
