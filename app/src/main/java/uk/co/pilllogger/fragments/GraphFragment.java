package uk.co.pilllogger.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.GetConsumptionsTask;

/**
 * Created by alex on 25/11/2013.
 */
public class GraphFragment extends Fragment implements GetConsumptionsTask.ITaskComplete {
    View _layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = inflater.inflate(R.layout.graph_fragment, container, false);

        _layout.setTag(R.id.tag_page_colour, Color.argb(120, 0, 106, 255));
        _layout.setTag(R.id.tag_tab_icon_position, 2);
        new GetConsumptionsTask(getActivity(), this, false).execute();

        return _layout;
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        StackBarGraph g1 = (StackBarGraph)_layout.findViewById(R.id.graph1);
        LineGraph g2 = (LineGraph)_layout.findViewById(R.id.graph2);
        PieGraph g3 = (PieGraph)_layout.findViewById(R.id.graph3);
        BarGraph g4 = (BarGraph)_layout.findViewById(R.id.graph4);

        int days = 7;
        Map<Pill, SparseIntArray> lastMonthOfConsumptions = ConsumptionMapper.mapByPillAndDate(consumptions, days);

        GraphHelper.plotStackBarGraph(lastMonthOfConsumptions, days, g1);
        GraphHelper.plotLineGraph(lastMonthOfConsumptions, days, g2);
        GraphHelper.plotPieChart(lastMonthOfConsumptions, days, g3);

        SparseIntArray pillConsumptionForDays = ConsumptionMapper.mapByTotalDayOfWeek(consumptions);
        GraphHelper.plotBarGraph(pillConsumptionForDays, g4, this.getActivity());
    }
}