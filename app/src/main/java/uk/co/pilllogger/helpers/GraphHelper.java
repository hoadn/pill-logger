package uk.co.pilllogger.helpers;

import android.content.Context;
import android.util.SparseIntArray;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/11/2013.
 */
public class GraphHelper {
    public static void plotLineGraph(Map<Pill, SparseIntArray> consumptionData, int days, LineGraph li){

        List<Integer> graphPills = State.getSingleton().getGraphPills();
        Boolean all = (graphPills == null);
        li.removeAllLines();
        double maxY = 0;

        for(Pill pill : consumptionData.keySet()){
            if (pill == null || ((!all) && (!graphPills.contains(pill.getId()))))
                continue;
            Line line = new Line();
            line.setShowingPoints(false);
            line.setColor(pill.getColour());
            SparseIntArray points = consumptionData.get(pill);
            for(int i = 0; i <= days; i++){
                LinePoint linePoint = new LinePoint();
                linePoint.setX(i);
                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                linePoint.setY(value);
                if (value > maxY)
                    maxY = value;

                line.addPoint(linePoint);
            }

            li.addLine(line);
        }

        li.setRangeY(0, (float)(maxY * 1.05));
    }

    public static void plotBarGraph(Map<Pill, SparseIntArray> consumptionData, int days, BarGraph g){
        List<Integer> graphPills = State.getSingleton().getGraphPills();
        Boolean all = (graphPills == null);
        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i <= days; i++){
            for(Pill pill : consumptionData.keySet()){
                if (pill == null || ((!all) && (!graphPills.contains(pill.getId()))))
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

        return context.getResources().getColor(resId);
    }

    public static void plotPieChart(Map<Pill, SparseIntArray> consumptionData, int days, PieGraph pie){

        pie.getSlices().clear();
        for(Pill pill : consumptionData.keySet()){
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
            pie.addSlice(ps);
        }
    }
}
