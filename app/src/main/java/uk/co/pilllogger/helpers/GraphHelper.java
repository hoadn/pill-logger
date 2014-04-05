package uk.co.pilllogger.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseIntArray;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.echo.holographlibrary.StackBar;
import com.echo.holographlibrary.StackBarGraph;
import com.echo.holographlibrary.StackBarSection;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.PillAmount;

/**
 * Created by alex on 25/11/2013.
 */
public class GraphHelper {
    public static void plotLineGraph(Map<Pill, SparseIntArray> consumptionData, int days, LineGraph li){

        li.removeAllLines();
        double maxY = 0;

        for(Pill pill : consumptionData.keySet()){
            if(State.getSingleton().isPillExcluded(pill))
                continue;
            Line line = new Line();
            line.setShowingPoints(false);
            line.setColor(pill.getColour());
            SparseIntArray points = consumptionData.get(pill);
            for(int i = 0; i <= days; i++){
                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                if (value > maxY)
                    maxY = value;

                LinePoint linePoint = new LinePoint(i, value);

                line.addPoint(linePoint);
            }

            li.addLine(line);
        }

        li.setRangeY(0, (float)(maxY * 1.05));
    }

    public static void plotBarGraph(Map<Pill, SparseIntArray> consumptionData, int days, BarGraph g){
        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i <= days; i++){
            for(Pill pill : consumptionData.keySet()){
                if(State.getSingleton().isPillExcluded(pill))
                    continue;
                SparseIntArray points = consumptionData.get(pill);

                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                Bar b = new Bar();
                b.setColor(pill.getColour());
                b.setValue(value);
                b.setName("");
                bars.add(b);
            }
        }
        g.setShowBarText(false);
        g.setBars(bars);
    }

    public static void plotBarGraph(SparseIntArray consumptionData, BarGraph g, Context context){
        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i < consumptionData.size(); i++){
            int value = consumptionData.valueAt(i);
            int day = i+1;
            Bar b = new Bar();
            b.setColor(getColourOfDay(day, context));
            b.setValue(value);
            b.setName(getDayOfWeek(day, context));
            bars.add(b);
        }
        g.setShowBarText(false);
        g.setBars(bars);
    }

    private static String getDayOfWeek(int dayOfWeek, Context context){
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

    private static int getColourOfDay(int dayOfWeek, Context context){
        int resId = R.color.white;
        switch(dayOfWeek){
            case 1:
                resId = R.color.monday;
                break;
            case 2:
                resId = R.color.tuesday;
                break;
            case 3:
                resId = R.color.wednesday;
                break;
            case 4:
                resId = R.color.thursday;
                break;
            case 5:
                resId = R.color.friday;
                break;
            case 6:
                resId = R.color.saturday;
                break;
            case 7:
                resId = R.color.sunday;
                break;
        }

        if(context == null)
            return Color.WHITE;

        return context.getResources().getColor(resId);
    }

    public static void plotPieChart(Map<Pill, SparseIntArray> consumptionData, int days, PieGraph pie){

        pie.getSlices().clear();
        for(Pill pill : consumptionData.keySet()){
            if(State.getSingleton().isPillExcluded(pill))
                continue;
            SparseIntArray points = consumptionData.get(pill);
            int sliceValue = 0;
            for(int i = 0; i <= days; i++){
                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                sliceValue += value;
            }

            PieSlice ps = new PieSlice();
            ps.setValue(sliceValue);
            ps.setColor(pill.getColour());
            ps.setStrokeColor(ColourHelper.getLighter(pill.getColour()));
            pie.addSlice(ps);
        }
    }

    public static void plotPieChart(List<PillAmount> pills, PieGraph pie){
        pie.getSlices().clear();

        for(PillAmount pa : pills){
            PieSlice ps = new PieSlice();
            ps.setValue(pa.getAmount());
            ps.setColor(pa.getPill().getColour());
            ps.setStrokeColor(ColourHelper.getLighter(pa.getPill().getColour()));
            pie.addSlice(ps);
        }
    }

    public static void plotStackBarGraph(Map<Pill, SparseIntArray> data, int days, StackBarGraph view, int lineColour) {
        view.setNoData(data.size() == 0);

        view.setLineColour(lineColour);

        List<StackBar> bars = new ArrayList<StackBar>();

        for(int i = 1; i <= days; i++){
            StackBar sb = new StackBar();
            DateTime dateTime = new DateTime().plusDays((7-i) * -1);
            sb.setName(dateTime.dayOfWeek().getAsShortText());


            List<Pill> pills = new ArrayList<Pill>(data.keySet());

            Collections.sort(pills, new Comparator<Pill>() {
                public int compare(Pill pill1, Pill pill2) {
                    if(pill1 == null && pill2 == null)
                        return 0;

                    if(pill1 == null && pill2 != null)
                        return 1;

                    if(pill1 != null && pill2 == null)
                        return -1;

                    if(pill1.getSortOrder() < pill2.getSortOrder())
                        return -1;

                    if(pill1.getSortOrder() > pill2.getSortOrder())
                        return 1;

                    return 0;
                }
            });

            for(Pill pill : pills){
                if(State.getSingleton().isPillExcluded(pill))
                    continue;

                SparseIntArray points = data.get(pill);

                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                StackBarSection sbs = new StackBarSection();
                sbs.setColor(pill.getColour());
                sbs.setStrokeColor(ColourHelper.getDarker(pill.getColour()));
                sbs.setValue(value);

                sb.getSections().add(sbs);
            }
            bars.add(sb);

            view.setShowBarText(false);
            view.setBars(bars);
            view.setShouldDrawGrid(true);
        }
    }
}
