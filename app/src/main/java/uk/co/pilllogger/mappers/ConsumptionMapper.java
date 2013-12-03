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

    public static Map<Pill, SparseIntArray> mapByPillAndDate(List<Consumption> consumptions, int days){

        DateTime to = new DateTime();
        DateTime from = to.minusDays(days);

        return mapByPillAndDate(consumptions, from, to);
    }

    public static Map<Pill, SparseIntArray> mapByPillAndDate(List<Consumption> consumptions, DateTime from){
        return mapByPillAndDate(consumptions, from, new DateTime());
    }

    public static Map<Pill, SparseIntArray> mapByPillAndDate(List<Consumption> consumptions, DateTime from, DateTime to){
        HashMap<Pill, SparseIntArray> xPoints = new HashMap<Pill, SparseIntArray>();

        for (Consumption c : consumptions) {
            Pill pill = c.getPill();
            DateTime consumptionDate = new DateTime(c.getDate());

            if(consumptionDate.isBefore(from) || consumptionDate.isAfter(to)) continue;

            Days days = Days.daysBetween(from.withTimeAtStartOfDay(), consumptionDate.withTimeAtStartOfDay());
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

    public static SparseIntArray mapByTotalDayOfWeek(List<Consumption> consumptions){
        return mapByTotalDayOfWeek(consumptions, null, null);
    }

    public static SparseIntArray mapByTotalDayOfWeek(List<Consumption> consumptions, DateTime from, DateTime to){
        SparseIntArray data = new SparseIntArray();

        data.put(1, 0); //MONDAY
        data.put(2, 0); //TUESDAY
        data.put(3, 0); //WEDNESDAY
        data.put(4, 0); //THURSDAY
        data.put(5, 0); //FRIDAY
        data.put(6, 0); //SATURDAY
        data.put(7, 0); //SUNDAY

        for(Consumption c : consumptions){
            DateTime consumptionDate = new DateTime(c.getDate());

            if(from != null && to != null &&
                    (consumptionDate.isBefore(from) || consumptionDate.isAfter(to)))
                break;

            int dayOfWeek = consumptionDate.dayOfWeek().get();

            if(data.indexOfKey(dayOfWeek) < 0)
                data.put(dayOfWeek, 1);
            else{
                int current = data.get(dayOfWeek);
                data.put(dayOfWeek, current + 1);
            }
        }

        return data;
    }
}
