package uk.co.pilllogger.stats;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;

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

    public static Pill getMostTakePill(Date startDate, Date endDate, List<Consumption> consumptions) {
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
        Map<String, Integer> amountPerDay = new HashMap<String, Integer>();

        for (Consumption consumption : consumptions) {
            SimpleDateFormat format = new SimpleDateFormat("EEEE");
            String day = format.format(consumption.getDate());
            if (amountPerDay.containsKey(day))
                amountPerDay.put(day, amountPerDay.get(day) + 1);
            else
                amountPerDay.put(day, 1);
        }

        Map.Entry<String, Integer> dayWithMostConsumptions = null;
        for (Map.Entry<String, Integer> entry : amountPerDay.entrySet()) {
            if (dayWithMostConsumptions == null || entry.getValue() > dayWithMostConsumptions.getValue())
                dayWithMostConsumptions = entry;
        }

        return dayWithMostConsumptions.getKey();
    }
}
