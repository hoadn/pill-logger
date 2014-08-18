package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.pilllogger.adapters.GraphPillListAdapter;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionGroupEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.InitTestDbTask;

/**
 * Created by nick on 23/10/13.
 */
public class ConsumptionListFragment extends PillLoggerFragmentBase implements
        InitTestDbTask.ITaskComplete,
        GetConsumptionsTask.ITaskComplete{

    public static final String TAG = "ConsumptionListFragment";
    ListView _listView;
    View _mainLayout;
    HashMap<Integer, Pill> _allPills = new HashMap<Integer, Pill>();
    Fragment _fragment;
    Activity _activity;
    private List<Pill> _pills = new ArrayList<Pill>();
    private List<Consumption> _consumptions;
    private View _loading;

    @DebugLog
    public static ConsumptionListFragment newInstance(int num){
        ConsumptionListFragment f = new ConsumptionListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        this.setRetainInstance(true);
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        if(getActivity() == null || v == null){
            return null;
        }
        int color = getActivity().getResources().getColor(State.getSingleton().getTheme().getConsumptionListBackgroundResourceId());
        v.setTag(R.id.tag_page_colour, color);
        v.setTag(R.id.tag_tab_icon_position, 0);

        _mainLayout = v;
        _fragment = this;
        _activity = getActivity();

        //Doing this to test - will not be needed when working fully
        //new InitTestDbTask(this.getActivity(), this).execute();

        _listView = (ListView) (v != null ? v.findViewById(R.id.main_consumption_list) : null);
        _loading = v.findViewById(R.id.consumptions_loading);

        _listView.setEmptyView(_loading);

        if (_listView.getAdapter() != null) //Trying this to make the list refresh after adding the new consumption
            ((ConsumptionListAdapter)_listView.getAdapter()).notifyDataSetChanged();

        TextView noConsumptions = (TextView) v.findViewById(R.id.no_consumption_text);
        TextView title = (TextView)v.findViewById(R.id.consumption_fragment_title);
        Typeface typeface = State.getSingleton().getTypeface();
        if (typeface != null){
            noConsumptions.setTypeface(typeface);
            title.setTypeface(typeface);
        }

        return v;
    }


    @Override
    public void initComplete() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        if(_listView == null || _listView.getAdapter() == null){
            return;
        }

        try {
            _bus.unregister(_listView.getAdapter());
        }
        catch(IllegalArgumentException ignored){} // if this throws, we're not registered anyway
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setupPills(_pills);
    }

    @Override @DebugLog
    public void consumptionsReceived(List<Consumption> consumptions) {
        _consumptions = consumptions;
        Activity activity = getActivity();

        if(activity == null) // the method won't work without the activity, so let's not crash trying.
            return;

        TextView noConsumption = (TextView) activity.findViewById(R.id.no_consumption_text);
        if (consumptions.size() == 0) {
            noConsumption.setVisibility(View.VISIBLE);
            _loading.setVisibility(View.INVISIBLE);
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

            if (_listView.getAdapter() != null) {
                ConsumptionListAdapter currentAdapter = (ConsumptionListAdapter) _listView.getAdapter();
                currentAdapter.destroy(); // tidy up Observer events
            }

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
        }
        TrackerHelper.updateUserProfile(activity, _pills.size(), _consumptions);

        Statistics.getInstance(activity).refreshConsumptionCaches(consumptions);
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

        int lineColour = _activity.getResources().getColor(State.getSingleton().getTheme().getStackBarGraphLineColourResourceId());

        if(view instanceof LineGraph)
            GraphHelper.plotLineGraph(data, dayCount, (LineGraph) view);

        if(view instanceof BarGraph)
            GraphHelper.plotBarGraph(data, dayCount, (BarGraph)view);

        if(view instanceof PieGraph)
            GraphHelper.plotPieChart(data, dayCount, (PieGraph)view);

        if(view instanceof StackBarGraph)
            GraphHelper.plotStackBarGraph(data, dayCount, (StackBarGraph)view, lineColour);

        view.setTag(data);
    }

    @Subscribe
    public void pillsLoaded(LoadedPillsEvent event) {
        _pills = event.getPills();
        List<Integer> graphPills = State.getSingleton().getGraphExcludePills();
        if (graphPills == null) {
            graphPills = new ArrayList<Integer>();
        }
        for(Pill p : _pills){
            _allPills.put(p.getId(), p);
        }
        State.getSingleton().setGraphExcludePills(graphPills);

        final List<Pill> pillList = _pills;
        setupPills(pillList);
    }

    private void setupPills(final List<Pill> pillList) {
        Activity activity = getActivity();
        if (activity != null) {
            ListView list = (ListView) activity.findViewById(R.id.graph_drawer);
            if (list != null){ //we need to init the adapter
                GraphPillListAdapter adapter = new GraphPillListAdapter(getActivity(), R.layout.graph_pill_list, _pills);
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

    @Subscribe
    public void pillsUpdated(UpdatedPillEvent event) {
        //new GetPillsTask(this.getActivity(), this).execute();
        Timber.d("Pills Updated");
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event) {
        final Consumption consumption1 = event.getConsumption();

        Timber.d("Consumption added. Updating consumption list.");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (_consumptions != null && !(_consumptions.contains(consumption1))) {
                    _consumptions.add(consumption1);
                    Collections.sort(_consumptions);
                    _consumptions = ConsumptionRepository.getSingleton(_activity).groupConsumptions(_consumptions);
                    consumptionsReceived(_consumptions);

                    Timber.d(TAG, "Consumption list updated");
                }
            }
        };

        executeRunnable(runnable);
    }

    @Subscribe
    public void consumptionDeleted(DeletedConsumptionEvent event) {
        final Consumption consumption1 = event.getConsumption();
        Runnable runnable = new Runnable(){
            public void run(){
                if (_consumptions != null) {
                    Collections.sort(_consumptions);
                    for (Consumption consumption2 : _consumptions) {
                        if (consumption2.getGroup().equals(consumption1.getGroup()) && consumption2.getPillId() == consumption1.getPillId()) {
                            consumption2.setQuantity(consumption2.getQuantity() - 1);
                        }
                    }
                    consumptionsReceived(_consumptions);
                }
            }
        };
        executeRunnable(runnable);
    }

    @Subscribe
    public void consumptionPillGroupDeleted(DeletedConsumptionGroupEvent event) {

        if(_consumptions == null || event.getGroup() == null)
            return;

        final List<Consumption> toRemove = new ArrayList<Consumption>();

        Consumption[] consumptions = new Consumption[_consumptions.size()];
        for(Consumption c : _consumptions.toArray(consumptions)){
            if(c == null){
                continue;
            }

            String consumptionGroup = c.getGroup();
            if(consumptionGroup == null) {
                continue;
            }

            if(c.getGroup().equals(event.getGroup()) && c.getPillId() == event.getPillId()) {
                toRemove.add(c);
            }
        }

        Runnable runnable = new Runnable(){
            public void run(){
                _consumptions.removeAll(toRemove);
                Collections.sort(_consumptions);
                //_consumptions = ConsumptionRepository.getSingleton(_activity).groupConsumptions(_consumptions);
                consumptionsReceived(_consumptions);
            }
        };

        executeRunnable(runnable);
    }
}
