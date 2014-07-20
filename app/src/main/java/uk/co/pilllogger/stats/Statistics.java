package uk.co.pilllogger.stats;

import android.content.Context;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetConsumptionsTask;

/**
 * Created by nick on 07/03/14.
 */
public class Statistics implements Observer.IConsumptionDeleted {

    private Context _context;
    // caches
    private List<PillAmount> _pillAmountsCache = null;
    private int _dayWithMostConsumptionsCache = -1;
    private int _hourWithMostConsumptionsCache = -1;
    private Map<Integer, Integer> _daysWithAmountsCache = null;
    private Map<Integer, Map<Integer, Integer>> _hoursWithAmountsDayCache = null;
    private Map<Integer, Map<Integer, Integer>> _daysWithHourAmountsCache = null;
    private Consumption _latestConsumptionCache = null;
    private String _averageTimeBetweenConsumptionsCache = null;
    private String _longestTimeBetweenConsumptionsCache = null;
    private int _totalConsumptionsCache = -1;
    private int _longestStreakCache = -1;
    private int _currentStreakCache = -1;

    private static Statistics _instance;

    public static Statistics getInstance(Context context) {
        if(_instance == null) {
            _instance = new Statistics(context);
            State.getSingleton().getBus().register(_instance);
        }
        return _instance;
    }

    private Statistics(Context context){
        _context = context;
        Observer.getSingleton().registerConsumptionDeletedObserver(this);
    }

    private List<Consumption> filterConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        List<Consumption> filteredConsumptions = new ArrayList<Consumption>();
        for (Consumption consumption : consumptions) {
            if (consumption.getDate().compareTo(startDate) > 0 && consumption.getDate().compareTo(endDate) < 0)
                filteredConsumptions.add(consumption);
        }
        return filteredConsumptions;
    }

    public Pill getMostTakenPill(Date startDate, Date endDate, List<Consumption> consumptions){
        return getMostTakenPill(filterConsumptions(startDate, endDate, consumptions));
    }

    public Pill getMostTakenPill(List<Consumption> consumptions){
        List<PillAmount> amountOfPill = getPillsWithAmounts(consumptions);

        return amountOfPill.get(0).getPill();
    }

    public List<PillAmount> getPillsWithAmounts(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getPillsWithAmounts(filterConsumptions(startDate, endDate, consumptions));
    }

    public List<PillAmount> getPillsWithAmounts(List<Consumption> consumptions) { //Does not support grouped consumptions (Use ungrouped)

        if (_pillAmountsCache != null) {
            return _pillAmountsCache;
        }

        if (consumptions == null)
            return null;

        Map<Pill, Integer> amountOfPill = new HashMap<Pill, Integer>();
        for (Consumption consumption : consumptions) {
            Pill pill = consumption.getPill();
            if(pill == null)
                continue;

            if (amountOfPill.containsKey(pill))
                amountOfPill.put(pill, (amountOfPill.get(pill)) + 1);
            else
                amountOfPill.put(pill, 1);
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

        _pillAmountsCache = pillAmounts;

        return pillAmounts;
    }

    public int getDayWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getDayWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public int getDayWithMostConsumptions(List<Consumption> consumptions) {

        if(_dayWithMostConsumptionsCache >= 0)
            return _dayWithMostConsumptionsCache;

        Map<Integer, Integer> days = getDaysWithAmounts(consumptions);

        Map.Entry<Integer, Integer> dayWithMostConsumptions = null;
        for (Map.Entry<Integer, Integer> entry : days.entrySet()) {
            if (dayWithMostConsumptions == null || entry.getValue() > dayWithMostConsumptions.getValue())
                dayWithMostConsumptions = entry;
        }

        int day = dayWithMostConsumptions.getKey();

        _dayWithMostConsumptionsCache = day;

        return day;
    }

    public int getHourWithMostConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getHourWithMostConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public int getHourWithMostConsumptions(List<Consumption> consumptions) {

        if(_hourWithMostConsumptionsCache >= 0)
            return _hourWithMostConsumptionsCache;

        Map<Integer, Integer> hours =  getHoursWithAmounts(consumptions);

        Map.Entry<Integer, Integer> timeWithMostConsumptions = null;
        for (Map.Entry<Integer, Integer> entry : hours.entrySet()) {
            if (timeWithMostConsumptions == null || entry.getValue() > timeWithMostConsumptions.getValue())
                timeWithMostConsumptions = entry;
        }

        Integer hour = timeWithMostConsumptions.getKey();

        _hourWithMostConsumptionsCache = hour;

        return hour;
    }

    public Map<Integer, Integer> getHoursWithAmounts(List<Consumption> consumptions){
        return getHoursWithAmounts(consumptions, -1);
    }

    public Map<Integer, Integer> getHoursWithAmounts(List<Consumption> consumptions, int dayOfWeek) {

        if (_hoursWithAmountsDayCache != null
                && _hoursWithAmountsDayCache.get(dayOfWeek) != null) {
            return _hoursWithAmountsDayCache.get(dayOfWeek);
        }

        _hoursWithAmountsDayCache = new HashMap<Integer, Map<Integer, Integer>>();

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

        _hoursWithAmountsDayCache.put(dayOfWeek, amountPerTime);

        return amountPerTime;
    }

    public Map<Integer, Map<Integer, Integer>> getDaysWithHourAmounts(List<Consumption> consumptions){

        if(_daysWithHourAmountsCache != null)
            return _daysWithHourAmountsCache;

        Map<Integer, Map<Integer, Integer>> days = new HashMap<Integer, Map<Integer, Integer>>();
        for(int i = 1; i <= 7; i++){
            Map<Integer, Integer> hours = getHoursWithAmounts(consumptions, i);
            days.put(i, hours);
        }

        _daysWithHourAmountsCache = days;

        return days;
    }

    public Map<Integer, Integer> getDaysWithAmounts(List<Consumption> consumptions){

        if(_daysWithAmountsCache != null)
            return _daysWithAmountsCache;

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

        _daysWithAmountsCache = amountPerDay;

        return amountPerDay;
    }

    public Consumption getLastConsumption(List<Consumption> consumptions) {

        if (_latestConsumptionCache != null) {
            return _latestConsumptionCache;
        }

        Consumption mostRecentConsumption = null;
        for (Consumption consumption : consumptions) {
            if (mostRecentConsumption == null || consumption.getDate().compareTo(mostRecentConsumption.getDate()) > 0)
                mostRecentConsumption = consumption;
        }

        _latestConsumptionCache = mostRecentConsumption;

        return mostRecentConsumption;
    }

    public String getAverageTimeBetweenConsumptions(Date startDate, Date endDate, List<Consumption> consumptions, Context context) {
        return getAverageTimeBetweenConsumptions(filterConsumptions(startDate, endDate, consumptions), context);
    }

    public String getAverageTimeBetweenConsumptions(List<Consumption> consumptions, Context context) {

        if(_averageTimeBetweenConsumptionsCache != null)
            return _averageTimeBetweenConsumptionsCache;

        if (!(consumptions.size() > 0))
            return null;
        Collections.sort(consumptions);
        consumptions = ConsumptionRepository.getSingleton(context).groupConsumptions(consumptions); //Need grouped consumptions for this to be accurate
        Consumption lastConsumption = consumptions.get(0);
        Consumption firstConsumption = consumptions.get(consumptions.size() - 1);

        long timeDifference = ((lastConsumption.getDate().getTime()) - (firstConsumption.getDate().getTime()));
        long averageTimeDifference = timeDifference / consumptions.size();
        String averageTimeString = timeToString(averageTimeDifference);

        _averageTimeBetweenConsumptionsCache = averageTimeString;

        return averageTimeString;
    }

    private String timeToString(long time) {
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

    public String getLongestTimeBetweenConsumptions(Date startDate, Date endDate, List<Consumption> consumptions) {
        return getLongestTimeBetweenConsumptions(filterConsumptions(startDate, endDate, consumptions));
    }

    public String getLongestTimeBetweenConsumptions(List<Consumption> consumptions) {

        if(_longestTimeBetweenConsumptionsCache != null)
            return _longestTimeBetweenConsumptionsCache;

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


        String time = timeToString(longestTimeBetweenConsumptions);

        _longestTimeBetweenConsumptionsCache = time;

        return time;
    }

    public int getTotalConsumptions(List<Consumption> consumptions){

        if(_totalConsumptionsCache >= 0)
            return _totalConsumptionsCache;

        int total = 0;
        for(Consumption consumption : consumptions){
            total += consumption.getQuantity();
        }

        _totalConsumptionsCache = total;

        return total;
    }

    public int getLongestStreak(List<Consumption> consumptions){

        if(_longestStreakCache >= 0)
            return _longestStreakCache;

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

                // add 1 to include the bound
                currentStreak = Days.daysBetween(firstStreakDate.withTimeAtStartOfDay(), dateTime.withTimeAtStartOfDay()).getDays() + 1;
            }
            previousConsumptionDate = dateTime;

            if(currentStreak > longestStreak)
                longestStreak = currentStreak;
        }

        _longestStreakCache = longestStreak;

        return longestStreak;
    }

    public int getCurrentStreak(List<Consumption> consumptions){

        if(_currentStreakCache >= 0)
            return _currentStreakCache;

        int currentStreak = 0;
        Collections.sort(consumptions);

        DateTime previousConsumptionDate = null;
        DateTime firstStreakDate = null;
        for(Consumption consumption : consumptions){
            DateTime dateTime = new DateTime(consumption.getDate());

            if(firstStreakDate == null)
                firstStreakDate = dateTime;

            if(previousConsumptionDate != null) {
                if(!previousConsumptionDate.withTimeAtStartOfDay().minusDays(1).isBefore(dateTime)){
                    return currentStreak;
                }

                // add 1 to be inclusive of consumption
                currentStreak = Math.abs(Days.daysBetween(firstStreakDate.withTimeAtStartOfDay(), dateTime.withTimeAtStartOfDay()).getDays()) + 1;
            }
            previousConsumptionDate = dateTime;
        }

        _currentStreakCache = currentStreak;
        return currentStreak;
    }

    public void refreshConsumptionCaches(List<Consumption> consumptions){
        _pillAmountsCache = null;
        _dayWithMostConsumptionsCache = -1;
        _hourWithMostConsumptionsCache = -1;
        _daysWithAmountsCache = null;
        _hoursWithAmountsDayCache = null;
        _daysWithHourAmountsCache = null;
        _latestConsumptionCache = null;
        _averageTimeBetweenConsumptionsCache = null;
        _longestTimeBetweenConsumptionsCache = null;
        _totalConsumptionsCache = -1;
        _longestStreakCache = -1;
        _currentStreakCache = -1;

        getPillsWithAmounts(consumptions);
        getDayWithMostConsumptions(consumptions);
        getHourWithMostConsumptions(consumptions);
        getDaysWithAmounts(consumptions);
        getHoursWithAmounts(consumptions);
        getDaysWithHourAmounts(consumptions);
        getAverageTimeBetweenConsumptions(consumptions, _context);
        getLongestTimeBetweenConsumptions(consumptions);
        getTotalConsumptions(consumptions);
        getLongestStreak(consumptions);
        getCurrentStreak(consumptions);
    }

    private void refreshPillCaches(List<Consumption> consumptions){
        _pillAmountsCache = null;

        getPillsWithAmounts(consumptions);
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event) {
        new GetConsumptionsTask(_context, new GetConsumptionsTask.ITaskComplete() {
            @Override
            public void consumptionsReceived(List<Consumption> consumptions) {
                refreshConsumptionCaches(consumptions);
            }
        }, false).execute();
    }

    @Override
    public void consumptionDeleted(Consumption consumption) {
        new GetConsumptionsTask(_context, new GetConsumptionsTask.ITaskComplete() {
            @Override
            public void consumptionsReceived(List<Consumption> consumptions) {
                refreshConsumptionCaches(consumptions);
            }
        }, false).execute();
    }

    @Override
    public void consumptionPillGroupDeleted(String group, int pillId) {
        new GetConsumptionsTask(_context, new GetConsumptionsTask.ITaskComplete() {
            @Override
            public void consumptionsReceived(List<Consumption> consumptions) {
                refreshConsumptionCaches(consumptions);
            }
        }, false).execute();
    }

    @Subscribe
    public void pillsUpdated(UpdatedPillEvent event) {
        new GetConsumptionsTask(_context, new GetConsumptionsTask.ITaskComplete() {
            @Override
            public void consumptionsReceived(List<Consumption> consumptions) {
                refreshPillCaches(consumptions);
            }
        }, false).execute();
    }
}
