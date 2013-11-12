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
            _listView.setAdapter(new ConsumptionListAdapter(getActivity(), R.layout.pill_list_item, consumptions));


            LineGraph li = (LineGraph)_mainLayout.findViewById(R.id.main_graph);

            HashMap<Integer, Line> lines = new HashMap<Integer, Line>();
            int i = 0;

            DateTime aMonthAgo = new DateTime().minusMonths(1);
            Days someDays = Days.daysBetween(aMonthAgo, new DateTime());
            int numDays = someDays.getDays();
            for (int j = 0; j < numDays; j++) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                Date newDate = new Date(date.getTime() - TimeUnit.DAYS.toMillis((numDays - 1) - j));

                Line l = new Line();
                boolean newline = false;
                boolean found = false;
                for (Consumption c : consumptions) {
                    Date consumptionDate = c.get_date();
                    int pillId = c.get_pill_id();
                    try {
                        newDate = formatter.parse(formatter.format(newDate));
                        consumptionDate = formatter.parse(formatter.format(consumptionDate));
                    }
                    catch (ParseException e) {
                        Logger.e(TAG, "Error :" + e.getMessage());
                        e.printStackTrace();
                    }
                    if (newDate.toString().equals(consumptionDate.toString())) {
                        Logger.v(TAG, "I have found a pill");
                        found = true;
                        newline = false;

                        if(lines.containsKey(pillId))
                            l = lines.get(pillId);
                        else{
                            lines.put(pillId, l);
                            l.setColor(Color.parseColor("#FFBB33"));
                            newline = true;
                        }

                        List<LinePoint> points = l.getPoints();
                        boolean alreadyPoint = false;
                        for(LinePoint point : points){
                            if(point.getX() == j){
                                point.setY(point.getY() + 1);
                                alreadyPoint = true;
                                break;
                            }
                        }
                        if(!alreadyPoint){
                            LinePoint lp = new LinePoint();
                            lp.setX(j);
                            lp.setY(1);

                            l.addPoint(lp);
                        }

                    }
                }
                if(!found){
                    Logger.d(TAG, "No comsumption today so adding a 0 to X " + j);
                    LinePoint lp = new LinePoint();
                    lp.setX(j);
                    lp.setY(0);

                    l.addPoint(lp);
                }
                for (Integer lineKey : lines.keySet())
                    li.addLine(lines.get(lineKey));
            }

            li.setRangeY(0, 10);
            li.setLineToFill(0);
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
