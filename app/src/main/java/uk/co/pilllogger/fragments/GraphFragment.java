package uk.co.pilllogger.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;

import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetConsumptionsTask;

/**
 * Created by alex on 25/11/2013.
 */
public class GraphFragment extends PillLoggerFragmentBase implements GetConsumptionsTask.ITaskComplete {
    View _layout;

    StackBarGraph _graph1;
    LineGraph _graph2;
    PieGraph _graph3;
    BarGraph _graph4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = inflater.inflate(R.layout.graph_fragment, container, false);

        _layout.setTag(R.id.tag_page_colour, Color.argb(120, 0, 106, 255));
        _layout.setTag(R.id.tag_tab_icon_position, 2);
        new GetConsumptionsTask(getActivity(), this, false).execute();

        _graph1 = (StackBarGraph)_layout.findViewById(R.id.graph1);
        _graph2 = (LineGraph)_layout.findViewById(R.id.graph2);
        _graph3 = (PieGraph)_layout.findViewById(R.id.graph3);
        _graph4 = (BarGraph)_layout.findViewById(R.id.graph4);

        TextView title = (TextView)_layout.findViewById(R.id.graph_fragment_title);
        title.setTypeface(State.getSingleton().getTypeface());

        return _layout;
    }

    private void managePaidCharts(){
        if(!State.getSingleton().isChartsPaid()){
            return;
        }

        // todo: hide the covers over the charts
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {

        int days = 7;
        Map<Pill, SparseIntArray> lastMonthOfConsumptions = ConsumptionMapper.mapByPillAndDate(consumptions, days);

        GraphHelper.plotStackBarGraph(lastMonthOfConsumptions, days, _graph1);
        GraphHelper.plotLineGraph(lastMonthOfConsumptions, days, _graph2);
        GraphHelper.plotPieChart(lastMonthOfConsumptions, days, _graph3);

        SparseIntArray pillConsumptionForDays = ConsumptionMapper.mapByTotalDayOfWeek(consumptions);
        GraphHelper.plotBarGraph(pillConsumptionForDays, _graph4, this.getActivity());
    }
}