package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.pilllogger.adapters.GraphPillListAdapter;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.GetPillsTask;
import uk.co.pilllogger.tasks.InitTestDbTask;

/**
 * Created by nick on 23/10/13.
 */
public class ConsumptionListFragment extends PillLoggerFragmentBase implements
        InitTestDbTask.ITaskComplete,
        GetConsumptionsTask.ITaskComplete,
        GetPillsTask.ITaskComplete,
        Observer.IPillsUpdated,
        Observer.IConsumptionAdded,
        Observer.IConsumptionDeleted{

    public static final String TAG = "ConsumptionListFragment";
    ListView _listView;
    View _mainLayout;
    HashMap<Integer, Pill> _allPills = new HashMap<Integer, Pill>();
    Fragment _fragment;
    Activity _activity;
    private List<Pill> _pills;
    private List<Consumption> _consumptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.setRetainInstance(true);
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        v.setTag(R.id.tag_tab_icon_position, 0);
        v.setTag(R.id.tag_page_colour ,Color.argb(120, 0, 233, 255));
        _mainLayout = v;
        _fragment = this;
        _activity = getActivity();

        Logger.v(TAG, "onCreateView Called");
        //Doing this to test - will not be needed when working fully
        //new InitTestDbTask(this.getActivity(), this).execute();

        _listView = (ListView) (v != null ? v.findViewById(R.id.main_consumption_list) : null);

        if (_listView.getAdapter() != null) //Trying this to make the list refresh after adding the new consumption
            ((ConsumptionListAdapter)_listView.getAdapter()).notifyDataSetChanged();

        TextView noConsumptions = (TextView) v.findViewById(R.id.no_consumption_text);
        TextView title = (TextView)v.findViewById(R.id.consumption_fragment_title);
        Typeface typeface = State.getSingleton().getTypeface();
        if (typeface != null){
            noConsumptions.setTypeface(typeface);
            title.setTypeface(typeface);
        }

        Observer.getSingleton().registerConsumptionAddedObserver(this);
        Observer.getSingleton().registerConsumptionDeletedObserver(this);
        return v;
    }


    @Override
    public void initComplete() {
    }

    @Override
    public void onStart(){
        super.onStart();

        Observer.getSingleton().registerPillsUpdatedObserver(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Observer.getSingleton().unregisterPillsUpdatedObserver(this);
        Observer.getSingleton().unregisterConsumptionAddedObserver(this);
        Observer.getSingleton().unregisterConsumptionDeletedObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        new GetPillsTask(this.getActivity(), this).execute();
    }

    @Override
    public void consumptionsReceived(List<Consumption> consumptions) {
        _consumptions = consumptions;
        Activity activity = getActivity();

        if(activity == null) // the method won't work without the activity, so let's not crash trying.
            return;

        TextView noConsumption = (TextView) activity.findViewById(R.id.no_consumption_text);
        if (consumptions.size() == 0) {
            noConsumption.setVisibility(View.VISIBLE);
            _listView.setVisibility(View.GONE);
        }
        else if(consumptions != null && consumptions.size() > 0){
            if (_listView.getVisibility() == View.GONE) {
                _listView.setVisibility(View.VISIBLE);
                noConsumption.setVisibility(View.GONE);
            }
            //List<Consumption> grouped = ConsumptionRepository.getSingleton(activity).groupConsumptions(consumptions);
            ConsumptionListAdapter adapter;

            adapter = new ConsumptionListAdapter(activity, this, R.layout.consumption_list_item, consumptions, _pills);

            _listView.setAdapter(adapter);
            _listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    View view = v.findViewById(R.id.graph_layout);
                    if (view != null)
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });
//            int dayCount = getGraphDays();
//
//            Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(consumptions, dayCount);
//
//            View view = _mainLayout.findViewById(R.id.main_graph);
//            plotGraph(xPoints, dayCount, view);
        }
    }

    private int getGraphDays(){
        DateTime aWeekAgo = new DateTime().minusWeeks(1);
        Days totalDays = Days.daysBetween(aWeekAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        return totalDays.getDays();
    }

    public void replotGraph(){
        int dayCount = getGraphDays();

        View view = _mainLayout.findViewById(R.id.main_graph);

        Map<Pill, SparseIntArray> xPoints = (Map<Pill, SparseIntArray>) view.getTag();
        plotGraph(xPoints, dayCount, view);
    }

    public void plotGraph(Map<Pill, SparseIntArray> data, int dayCount, View view){
        if(view instanceof LineGraph)
            GraphHelper.plotLineGraph(data, dayCount, (LineGraph) view);

        if(view instanceof BarGraph)
            GraphHelper.plotBarGraph(data, dayCount, (BarGraph)view);

        if(view instanceof PieGraph)
            GraphHelper.plotPieChart(data, dayCount, (PieGraph)view);

        if(view instanceof StackBarGraph)
            GraphHelper.plotStackBarGraph(data, dayCount, (StackBarGraph)view);

        view.setTag(data);
    }

    @Override
    public void pillsReceived(List<Pill> pills) {
        _pills = pills;
        List<Integer> graphPills = State.getSingleton().getGraphExcludePills();
        if (graphPills == null) {
            graphPills = new ArrayList<Integer>();
        }
        for(Pill p : pills){
            _allPills.put(p.getId(), p);
        }
        State.getSingleton().setGraphExcludePills(graphPills);

        final List<Pill> pillList = pills;
        Activity activity = getActivity();
        if (activity != null) {
            ListView list = (ListView) activity.findViewById(R.id.graph_drawer);
            if (list != null){ //we need to init the adapter
                GraphPillListAdapter adapter = new GraphPillListAdapter(getActivity(), R.layout.graph_pill_list, pills);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Pill pill = pillList.get(position);
                        List<Integer> graphPills = State.getSingleton().getGraphExcludePills();
                        CheckBox checkbox = (CheckBox)view.findViewById(R.id.graph_list_check_box);
                        if (!checkbox.isChecked()) {
                            checkbox.setChecked(true);
                            if (graphPills.contains(pill.getId())) {
                                graphPills.remove((Object) pill.getId());
                            }
                        }
                        else {
                            checkbox.setChecked(false);
                            if (!graphPills.contains(pill.getId())) {
                                graphPills.add(pill.getId());
                            }
                        }
                        replotGraph();
                    }
                });
            }
            new GetConsumptionsTask(this.getActivity(), this, true).execute();
        }
    }

    @Override
    public void pillsUpdated(Pill pill) {
        new GetPillsTask(this.getActivity(), this).execute();
    }

    @Override
    public void consumptionAdded(Consumption consumption) {
        final Consumption consumption1 = consumption;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (_consumptions != null && !(_consumptions.contains(consumption1))) {
                    _consumptions.add(consumption1);
                    Collections.sort(_consumptions);
                    _consumptions = ConsumptionRepository.getSingleton(_activity).groupConsumptions(_consumptions);
                    consumptionsReceived(_consumptions);
                }
            }
        };

        executeRunnable(runnable);
    }

    @Override
    public void consumptionDeleted(Consumption consumption) {
        final Consumption consumption1 = consumption;
        Runnable runnable = new Runnable(){
            public void run(){
                if (_consumptions != null && (_consumptions.contains(consumption1))) {
                    _consumptions.remove(consumption1);
                    Collections.sort(_consumptions);
                    consumptionsReceived(_consumptions);
                }
            }
        };
        executeRunnable(runnable);
    }

    @Override
    public void consumptionPillGroupDeleted(String group, int pillId) {

        if(_consumptions == null || group == null)
            return;

        final List<Consumption> toRemove = new ArrayList<Consumption>();

        Consumption[] consumptions = new Consumption[_consumptions.size()];
        for(Consumption c : _consumptions.toArray(consumptions)){
            String consumptionGroup = c.getGroup();
            if(consumptionGroup == null)
                continue;

            if(c.getGroup().equals(group) && c.getPillId() == pillId)
                toRemove.add(c);
        }

        Runnable runnable = new Runnable(){
            public void run(){
                _consumptions.removeAll(toRemove);
                Collections.sort(_consumptions);
                consumptionsReceived(_consumptions);
            }
        };

        executeRunnable(runnable);
    }
}
