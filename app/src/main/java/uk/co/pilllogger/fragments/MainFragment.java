package uk.co.pilllogger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.listeners.AddConsumptionClickListener;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetFavouritePillsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InitTestDbTask;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by nick on 23/10/13.
 */
public class MainFragment extends Fragment implements InitTestDbTask.ITaskComplete, GetConsumptionsTask.ITaskComplete, GetFavouritePillsTask.ITaskComplete, GetPillsTask.ITaskComplete {

    private static final String TAG = "MainFragment";
    ListView _listView;
    ViewGroup _favouriteContainer;
    View _mainLayout;
    HashMap<Integer, Pill> _allPills = new HashMap<Integer, Pill>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        _mainLayout = v;

        Logger.v(TAG, "onCreateView Called");
        //Doing this to test - will not be needed when working fully
        new InitTestDbTask(this.getActivity(), this).execute();

        _listView = (ListView) (v != null ? v.findViewById(R.id.main_consumption_list) : null);
        _favouriteContainer = (ViewGroup) (v!=null ? v.findViewById(R.id.button_container):null);
        ImageView addConsumption = (ImageView) v.findViewById(R.id.main_add);
        addConsumption.setOnClickListener(new AddConsumptionClickListener(getActivity()));

        if (_listView.getAdapter() != null) //Trying this to make the list refresh after adding the new consumption
            ((ConsumptionListAdapter)_listView.getAdapter()).notifyDataSetChanged();

        return v;
    }


    @Override
    public void initComplete() {
        new GetPillsTask(this.getActivity(), this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        new GetPillsTask(this.getActivity(), this).execute();
        new GetFavouritePillsTask(this.getActivity(), this).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if(consumptions != null && consumptions.size() > 0){
            List<Consumption> grouped = ConsumptionRepository.getSingleton(getActivity()).groupConsumptions(consumptions);
            _listView.setAdapter(new ConsumptionListAdapter(getActivity(), R.layout.consumption_list_item, grouped));

            HashMap<Integer, SparseIntArray> xPoints = new HashMap<Integer, SparseIntArray>();

            DateTime aMonthAgo = new DateTime().minusMonths(1);

            for (Consumption c : consumptions) {
                int pillId = c.get_pill_id();
                Days days = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime(c.get_date()).plusDays(1).withTimeAtStartOfDay());
                int x = days.getDays();

                SparseIntArray currentLineValues;
                if(xPoints.containsKey(pillId))
                    currentLineValues = xPoints.get(pillId);
                else{
                    currentLineValues = new SparseIntArray();
                    xPoints.put(pillId, currentLineValues);
                }

                int value = 1;
                if(currentLineValues.indexOfKey(x) >= 0)
                    value += currentLineValues.get(x);

                currentLineValues.put(x, value);
            }


            Days totalDays = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime().plusDays(1).withTimeAtStartOfDay());
            int dayCount = totalDays.getDays();

            View view = _mainLayout.findViewById(R.id.main_graph);

            if(view instanceof LineGraph)
                plotLineGraph(xPoints, dayCount, (LineGraph)view);

            if(view instanceof BarGraph)
                plotBarGraph(xPoints, dayCount, (BarGraph)view);

            if(view instanceof PieGraph)
                plotPieChart(xPoints, dayCount, (PieGraph)view);
        }
    }

    private void plotLineGraph(HashMap<Integer, SparseIntArray> consumptionData, int days, LineGraph li){

        li.removeAllLines();
        
        for(int pillId : consumptionData.keySet()){
            Line line = new Line();
            Pill p = _allPills.get(pillId);
            line.setColor(p.getColour());
            SparseIntArray points = consumptionData.get(pillId);
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

    private void plotBarGraph(HashMap<Integer, SparseIntArray> consumptionData, int days, BarGraph g){

        ArrayList<Bar> bars = new ArrayList<Bar>();

        for(int i = 0; i <= days; i++){
            for(int pillId : consumptionData.keySet()){
                SparseIntArray points = consumptionData.get(pillId);

                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                Bar b = new Bar();
                Pill p = _allPills.get(pillId);
                b.setColor(p.getColour());
                b.setValue(value);
                b.setName("");
                bars.add(b);
            }
        }
        g.setShowBarText(false);
        g.setBars(bars);
    }

    private void plotPieChart(HashMap<Integer, SparseIntArray> consumptionData, int days, PieGraph pie){

        pie.getSlices().clear();
        for(int pillId : consumptionData.keySet()){
            Line line = new Line();
            Pill p = _allPills.get(pillId);
            line.setColor(p.getColour());
            SparseIntArray points = consumptionData.get(pillId);
            int sliceValue = 0;
            for(int i = 0; i <= days; i++){
                int value = 0;
                if(points.indexOfKey(i) >= 0)
                    value = points.get(i);

                sliceValue += value;
            }

            PieSlice ps = new PieSlice();
            ps.setValue(sliceValue);
            ps.setColor(p.getColour());
            pie.addSlice(ps);
        }
    }

    @Override
    public void favouritePillsReceived(List<Pill> pills) {
        if(_favouriteContainer == null)
            return;

        int children = _favouriteContainer.getChildCount();
        int start = 1;

        if(pills.size() == 0) //remove customise button
            start = 2;

        _favouriteContainer.removeViews(start, children -start);

        for(Pill p : pills){
            LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(R.layout.favourite_pill, null);

            if(p.getName().length() > 0){
                TextView letter = (TextView) v.findViewById(R.id.pill_letter);
                letter.setText(p.getName().substring(0,1));
                Logger.d(TAG, "Adding favourite for: " + p.getName());
            }

            _favouriteContainer.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        for(Pill p : pills){
            _allPills.put(p.getId(), p);
        }

        new GetConsumptionsTask(this.getActivity(), this, false).execute();
    }
}
