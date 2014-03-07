package uk.co.pilllogger.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

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

    public static String getDayWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptinos) {
        return getDayWithMostConsumptions(filterConsumptions(startDate, endDate, consumptinos));
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
}
