package uk.co.pilllogger.stats;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
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

    public static Pill getMostTakenPill(Date startDate, Date endDate, List<Consumption> consumptions){
        return getMostTakenPill(filterConsumptions(startDate, endDate, consumptions));
    }

    public static Pill getMostTakenPill(List<Consumption> consumptions){
        List<PillAmount> amountOfPill = getPillsWithAmounts(consumptions);

        return amountOfPill.get(0).getPill();
    }

    public static List<PillAmount> getPillsWithAmounts(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getPillsWithAmounts(filterConsumptions(startDate, endDate, consumptions));
    }

    public static List<PillAmount> getPillsWithAmounts(List<Consumption> consumptions) { //Does not support grouped consumptions (Use ungrouped)
        if (consumptions == null)
            return null;

        Map<Pill, Integer> amountOfPill = new HashMap<Pill, Integer>();
        for (Consumption consumption : consumptions) {
            Pill pill = consumption.getPill();
            if (amountOfPill.containsKey(pill))
                amountOfPill.put(pill, (amountOfPill.get(pill)) + consumption.getQuantity());
            else
                amountOfPill.put(pill, consumption.getQuantity());
        }

        List<PillAmount> pillAmounts = new ArrayList<PillAmount>();
        for(Pill p : amountOfPill.keySet()){
            PillAmount pa = new PillAmount();
            pa.setPill(p);
            pa.setAmount(amountOfPill.get(p));

            pillAmounts.add(pa);
        }

        Collections.sort(pillAmounts, new Comparator<PillAmount>() {
            @Override
            public int compare(PillAmount lhs, PillAmount rhs) {
                if(lhs.getAmount() < rhs.getAmount())
                    return 1;
                if(lhs.getAmount() > rhs.getAmount())
                    return -1;

                return 0;
            }
        });

        return pillAmounts;
    }

    public static int getDayWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getDayWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public static int getDayWithMostConsumptions(List<Consumption> consumptions) {
        Map<Integer, Integer> days = getDaysWithAmounts(consumptions);

        Map.Entry<Integer, Integer> dayWithMostConsumptions = null;
        for (Map.Entry<Integer, Integer> entry : days.entrySet()) {
            if (dayWithMostConsumptions == null || entry.getValue() > dayWithMostConsumptions.getValue())
                dayWithMostConsumptions = entry;
        }

        int day = dayWithMostConsumptions.getKey();

        return day;
    }

    public static int getHourWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getHourWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public static int getHourWithMostConsumptions(List<Consumption> consumptions) {
        Map<Integer, Integer> hours =  getHoursWithAmounts(consumptions);

        Map.Entry<Integer, Integer> timeWithMostConsumptions = null;
        for (Map.Entry<Integer, Integer> entry : hours.entrySet()) {
            if (timeWithMostConsumptions == null || entry.getValue() > timeWithMostConsumptions.getValue())
                timeWithMostConsumptions = entry;
        }

        return timeWithMostConsumptions.getKey();
    }

    public static Map<Integer, Integer> getHoursWithAmounts(List<Consumption> consumptions){
        return getHoursWithAmounts(consumptions, -1);
    }

    public static Map<Integer, Integer> getHoursWithAmounts(List<Consumption> consumptions, int dayOfWeek) {
        Map<Integer, Integer> amountPerTime = new HashMap<Integer, Integer>();

        for(int i = 0; i < 24; i++){
            amountPerTime.put(i, 0);
        }

        for (Consumption consumption : consumptions) {
            DateTime dateTime = new DateTime(consumption.getDate());
            if(dayOfWeek > 0 && dateTime.getDayOfWeek() != dayOfWeek)
                continue;

            int hour = dateTime.getHourOfDay();
            if (amountPerTime.containsKey(hour))
                amountPerTime.put(hour, amountPerTime.get(hour) + 1);
            else
                amountPerTime.put(hour, 1);
        }

        return amountPerTime;
    }

    public static Map<Integer, Map<Integer, Integer>> getDaysWithHourAmounts(List<Consumption> consumptions){
        Map<Integer, Map<Integer, Integer>> days = new HashMap<Integer, Map<Integer, Integer>>();
        for(int i = 1; i <= 7; i++){
            Map<Integer, Integer> hours = getHoursWithAmounts(consumptions, i);
            days.put(i, hours);
        }

        return days;
    }

    public static Map<Integer, Integer> getDaysWithAmounts(List<Consumption> consumptions){
        Map<Integer, Integer> amountPerDay = new HashMap<Integer, Integer>();

        for(int i = 1; i <= 7; i++){
            amountPerDay.put(i, 0);
        }

        for (Consumption consumption : consumptions) {
            int day = new DateTime(consumption.getDate()).getDayOfWeek();
            if (amountPerDay.containsKey(day))
                amountPerDay.put(day, amountPerDay.get(day) + 1);
            else
                amountPerDay.put(day, 1);
        }

        return amountPerDay;
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

        long timeDifference = ((lastConsumption.getDate().getTime()) - (firstConsumption.getDate().getTime()));
        long averageTimeDifference = timeDifference / consumptions.size();
        return timeToString(averageTimeDifference);
    }

    private static String timeToString(long time) {
        final int MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;
        final int MILLIS_TO_HOURS = 1000 * 60 * 60;
        final int MILLIS_TO_MINS = 100 * 60 * 60;

        long daysInt = time / MILLIS_TO_DAYS;
        long hoursInt = time / MILLIS_TO_HOURS % 24;
        long minsInt = time / MILLIS_TO_MINS % 60;

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

    public static int getTotalConsumptions(List<Consumption> consumptions){
        int total = 0;
        for(Consumption consumption : consumptions){
            total += consumption.getQuantity();
        }
        return total;
    }

    public static int getLongestStreak(List<Consumption> consumptions){
        int longestStreak = 0;
        int currentStreak = 0;
        Collections.sort(consumptions);
        Collections.reverse(consumptions);

        DateTime previousConsumptionDate = null;
        DateTime firstStreakDate = null;
        for(Consumption consumption : consumptions){

            DateTime dateTime = new DateTime(consumption.getDate());

            if(firstStreakDate == null)
                firstStreakDate = dateTime;

            if(previousConsumptionDate != null) {
                if(!previousConsumptionDate.withTimeAtStartOfDay().plusDays(2).isAfter(consumption.getDate().getTime())){
                    firstStreakDate = dateTime;
                }

                currentStreak = Days.daysBetween(firstStreakDate.withTimeAtStartOfDay(), previousConsumptionDate.withTimeAtStartOfDay()).getDays();
            }
            previousConsumptionDate = dateTime;

            if(currentStreak > longestStreak)
                longestStreak = currentStreak;
        }

        return longestStreak;
    }

    public static int getCurrentStreak(List<Consumption> consumptions){
        int currentStreak = 0;
        Collections.sort(consumptions);

        DateTime previousConsumptionDate = null;
        DateTime firstStreakDate = null;
        for(Consumption consumption : consumptions){

            DateTime dateTime = new DateTime(consumption.getDate());

            if(firstStreakDate == null)
                firstStreakDate = dateTime;

            if(previousConsumptionDate != null) {
                if(!previousConsumptionDate.withTimeAtStartOfDay().minusDays(2).isBefore(consumption.getDate().getTime())){
                    return currentStreak;
                }

                currentStreak = Math.abs(Days.daysBetween(firstStreakDate.withTimeAtStartOfDay(), previousConsumptionDate.withTimeAtStartOfDay()).getDays());
            }
            previousConsumptionDate = dateTime;
        }


        return currentStreak;
    }
}
