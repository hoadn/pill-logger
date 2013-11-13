package uk.co.cntwo.pilllogger.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseIntArray;
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

            HashMap<Integer, SparseIntArray> xPoints = new HashMap<Integer, SparseIntArray>();

            DateTime aMonthAgo = new DateTime().minusMonths(1);

            for (Consumption c : consumptions) {
                int pillId = c.get_pill_id();
                Days days = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime(c.get_date()).withTimeAtStartOfDay());
                int x = days.getDays();

                SparseIntArray currentLineValues;
                if(xPoints.containsKey(pillId))
                    currentLineValues = xPoints.get(pillId);
                else{
                    currentLineValues = new SparseIntArray();
                    xPoints.put(pillId, currentLineValues);
                }

                int value = 1;
                if(currentLineValues.indexOfKey(x) > 0)
                    value += currentLineValues.get(x);

                currentLineValues.put(x, value);
            }

            Days totalDays = Days.daysBetween(aMonthAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
            int dayCount = totalDays.getDays();

            for(int pillId : xPoints.keySet()){
                Line line = new Line();
                line.setColor(Color.parseColor("#FF3333"));
                SparseIntArray points = xPoints.get(pillId);
                for(int i = 0; i < dayCount; i++){
                    LinePoint linePoint = new LinePoint();
                    linePoint.setX(i);
                    if(points.indexOfKey(i) > 0)
                        linePoint.setY(points.get(i));
                    else
                        linePoint.setY(0);

                    line.addPoint(linePoint);
                }

                li.addLine(line);
            }

            double maxY = li.getMaxY();
            li.setRangeY(0, (float) Math.ceil(maxY * 1.4 ));
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
