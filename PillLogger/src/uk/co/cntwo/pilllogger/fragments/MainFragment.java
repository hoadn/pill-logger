package uk.co.cntwo.pilllogger.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.listeners.AddConsumptionClickListener;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.GetConsumptionsTask;
import uk.co.cntwo.pilllogger.tasks.GetFavouritePillsTask;
import uk.co.cntwo.pilllogger.tasks.InitTestDbTask;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by nick on 23/10/13.
 */
public class MainFragment extends Fragment implements InitTestDbTask.ITaskComplete, GetConsumptionsTask.ITaskComplete, GetFavouritePillsTask.ITaskComplete {

    private static final String TAG = "MainFragment";
    ListView _listView;
    ViewGroup _favouriteContainer;
    View _mainLayout;

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
        new GetConsumptionsTask(this.getActivity(), this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        new GetConsumptionsTask(this.getActivity(), this).execute();
        new GetFavouritePillsTask(this.getActivity(), this).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        if(consumptions != null && consumptions.size() > 0){
            _listView.setAdapter(new ConsumptionListAdapter(getActivity(), R.layout.consumption_list_item, consumptions));


            LineGraph li = (LineGraph)_mainLayout.findViewById(R.id.main_graph);

            HashMap<Integer, Line> lines = new HashMap<Integer, Line>();
            HashMap<Integer, HashMap<Integer, Integer>> xPoints = new HashMap<Integer, HashMap<Integer, Integer>>();

            Line l = new Line();
            boolean newLine = false;
            boolean found = false;

            DateTime aMonthAgo = new DateTime().minusMonths(1);


            for (Consumption c : consumptions) {
                Date consumptionDate = c.get_date();
                int pillId = c.get_pill_id();
                Days days = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime(c.get_date()).withTimeAtStartOfDay());
                int x = days.getDays();
                Logger.v(TAG, "days = " + x + " date " + c.get_date());
                newLine = false;

                if(lines.containsKey(pillId))
                    l = lines.get(pillId);
                else{
                    l = new Line();
                    lines.put(pillId, l);
                    l.setColor(Color.parseColor("#FFBB33"));
                    newLine = true;
                }

                List<LinePoint> points = l.getPoints();
                boolean alreadyPoint = false;
                HashMap<Integer, Integer> xList = new HashMap<Integer, Integer>();

                for(LinePoint point : points){
                    if(point.getX() == x){
                        point.setY(point.getY() + 1);
                        alreadyPoint = true;
                        xList = xPoints.get(c.get_pill_id());
                        xList.put(x, xList.get(x) + 1);
                        break;
                    }
                }
                if(!alreadyPoint){
                    LinePoint lp = new LinePoint();
                    lp.setX(x);
                    lp.setY(1);

                    l.addPoint(lp);

                    if ((xPoints.get(c.get_pill_id())) != null) {
                        xList = xPoints.get(c.get_pill_id());
                        xList.put(x, 1);
                    }
                    else
                        xList.put(x, 1);
                    xPoints.put(c.get_pill_id(), xList);
                }
                if (newLine)
                    li.addLine(l);
            }

            Days totalDays = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
            int allXPoints = totalDays.getDays();
            Logger.v(TAG, "totalDays: " + allXPoints + " lines: " + lines.size() + " xPoints: " + xPoints.size());

            li.removeAllLines();
            int color = 1; //this is used to give the lines a different colour until we have implemented it properly
            int firstLine = -1;
            int j = 0;
            int largestY = 0; //this is used to dynamically set the y range of the graph
            for (Integer pillId : xPoints.keySet()) {
                HashMap<Integer, Integer> xPointsList = xPoints.get(pillId);
                Line line = new Line();
                if (color % 2 == 0) // TODO: make this use pill colour
                    line.setColor(Color.parseColor("#FF3333"));
                else
                    line.setColor(Color.parseColor("#FFBB33"));
                boolean started = false; //this is so the graph starts at the first value we have (so we don't have lots of 0's at the start)
                for (int i = 0; i <= allXPoints; i++) {
                    if (xPointsList.keySet().contains(i)) {
                        LinePoint lp = new LinePoint();
                        int y = xPointsList.get(i);
                        if (y > largestY)
                            largestY = y;
                        lp.setX(i);
                        lp.setY(y);
                        line.addPoint(lp);
                        started = true;
                        if (firstLine == -1)
                            firstLine = j;
                    }
                    else {
                        LinePoint lp = new LinePoint();
                        lp.setX(i);
                        lp.setY(0);
                        if (started)
                            line.addPoint(lp);
                    }
                }
                li.addLine(line);
                color++;
                j++;
            }
            double yRange = largestY + (largestY * 0.4);
            if (Math.round(yRange) == largestY)
                yRange++;
            Logger.d(TAG, "yRange: " + yRange + " largestY: " + largestY);
            li.setRangeY(0, Math.round(yRange));
            if (firstLine != -1)
                li.setLineToFill(firstLine);
        }
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
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
}
