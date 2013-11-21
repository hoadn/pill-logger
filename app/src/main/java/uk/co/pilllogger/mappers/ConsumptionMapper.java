package uk.co.pilllogger.mappers;

import android.util.SparseIntArray;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 21/11/2013.
 */
public class ConsumptionMapper {

    public static Map<Pill, SparseIntArray> mapByPillAndDay(List<Consumption> consumptions, int days){

        DateTime to = new DateTime();
        DateTime from = to.minusDays(days);

        return mapByPillAndDay(consumptions, from, to);
    }

    public static Map<Pill, SparseIntArray> mapByPillAndDay(List<Consumption> consumptions, DateTime from){
        return mapByPillAndDay(consumptions, from, new DateTime());
    }

    public static Map<Pill, SparseIntArray> mapByPillAndDay(List<Consumption> consumptions, DateTime from, DateTime to){
        HashMap<Pill, SparseIntArray> xPoints = new HashMap<Pill, SparseIntArray>();

        for (Consumption c : consumptions) {
            Pill pill = c.get_pill();
            DateTime consumptionDate = new DateTime(c.get_date());

            if(consumptionDate.isBefore(from) || consumptionDate.isAfter(to)) break;

            Days days = Days.daysBetween(from.withTimeAtStartOfDay(), consumptionDate.plusDays(1).withTimeAtStartOfDay());
            int x = days.getDays();

            SparseIntArray currentLineValues;
            if(xPoints.containsKey(pill)) {
                currentLineValues = xPoints.get(pill);
            }
            else{
                currentLineValues = new SparseIntArray();
                xPoints.put(pill, currentLineValues);
            }

            int value = 1;
            if(currentLineValues.indexOfKey(x) >= 0)
                value += currentLineValues.get(x);

            currentLineValues.put(x, value);
        }

        return xPoints;
    }
}
