package uk.co.pilllogger.helpers;

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

import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 25/11/2013.
 */
public class GraphHelper {
    public static void plotLineGraph(Map<Pill, SparseIntArray> consumptionData, int days, LineGraph li){

        li.removeAllLines();

        for(Pill pill : consumptionData.keySet()){
            Line line = new Line();
            line.setColor(pill.getColour());
            SparseIntArray points = consumptionData.get(pill);
            for(int i = 0; i <= days; i++){
                LinePoint linePoint = new LinePoint();
                linePoint.setX(i);
                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                for (Line line1 : li.getLines()) {
                    value += line1.getPoint(i).getY();
                }

                linePoint.setY(value);

                line.addPoint(linePoint);
            }

            li.addLine(line);
        }

        double maxY = li.getMaxY();
        li.setRangeY(0, (float)(maxY * 1.05));
    }

    public static void plotBarGraph(Map<Pill, SparseIntArray> consumptionData, int days, BarGraph g){

        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i <= days; i++){
            for(Pill pill : consumptionData.keySet()){
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

    public static void plotBarGraph(SparseIntArray consumptionData, BarGraph g){
        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i < consumptionData.size(); i++){
            int value = consumptionData.keyAt(i);

            Bar b = new Bar();
            b.setColor(0);
            b.setValue(value);
            b.setName("");
            bars.add(b);
        }
        g.setShowBarText(false);
        g.setBars(bars);
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
