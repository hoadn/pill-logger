package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.ConsumptionListAdapter;
import uk.co.pilllogger.adapters.ConsumptionListAdapterFactory;
import uk.co.pilllogger.adapters.ConsumptionRecyclerAdapter;
import uk.co.pilllogger.adapters.GraphPillListAdapter;
import uk.co.pilllogger.adapters.SimpleSectionedRecyclerViewAdapter;
import uk.co.pilllogger.decorators.DividerItemDecoration;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DeleteConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionGroupEvent;
import uk.co.pilllogger.events.LoadedConsumptionsEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.PreferencesChangedEvent;
import uk.co.pilllogger.events.RedrawGraphEvent;
import uk.co.pilllogger.events.TakeConsumptionAgainEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.DeleteConsumptionJob;
import uk.co.pilllogger.jobs.InsertConsumptionsJob;
import uk.co.pilllogger.jobs.LoadConsumptionsJob;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;

import static butterknife.ButterKnife.findById;

/**
 * Created by nick on 23/10/13.
 */
public class ConsumptionListFragment extends PillLoggerFragmentBase {
    @Inject
    Context _context;
    @Inject Statistics _statistics;
    @Inject JobManager _jobManager;

    public static final String TAG = "ConsumptionListFragment";
    RecyclerView _listView;
    StackBarGraph _graph;
    View _mainLayout;
    HashMap<Integer, Pill> _allPills = new HashMap<Integer, Pill>();
    Fragment _fragment;
    Activity _activity;
    private List<Pill> _pills = new ArrayList<Pill>();
    private List<Consumption> _consumptions;
    private View _loading;
    private ConsumptionRecyclerAdapter _adapter;
    private ContentLoadingProgressBar _progress;
    private TextView _noConsumption;

    @DebugLog
    public static ConsumptionListFragment newInstance(int num){
        ConsumptionListFragment f = new ConsumptionListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override @DebugLog
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

        _graph = findById(v, R.id.consumption_graph);

        _progress = findById(v, R.id.consumption_progress);

        _progress.show();

        _listView = (RecyclerView) (v != null ? v.findViewById(R.id.main_consumption_list) : null);
        _listView.setHasFixedSize(true);
        _listView.addItemDecoration(new DividerItemDecoration(_activity, DividerItemDecoration.VERTICAL_LIST));

        LinearLayoutManager layoutManager = new LinearLayoutManager(_context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _listView.setLayoutManager(layoutManager);
        _listView.setItemAnimator(new DefaultItemAnimator());

        if (_listView.getAdapter() != null) { //Trying this to make the list refresh after adding the new consumption
            _listView.getAdapter().notifyDataSetChanged();
        }

        _noConsumption = (TextView) v.findViewById(R.id.no_consumption_text);

        return v;
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

    @Override @DebugLog
    public void onDestroyView(){
        super.onDestroyView();

        if(_listView == null || _listView.getAdapter() == null){
            return;
        }

        try {
            _consumptions.clear();
            _consumptions.addAll(_adapter.getConsumptions());
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

        if(_listView != null && _listView.getAdapter() != null) {
            _listView.getAdapter().notifyDataSetChanged();
        }

        setupPills(_pills);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setupPills(_pills);
    }

    @Subscribe @DebugLog
    public void consumptionsReceived(LoadedConsumptionsEvent event) {
        _consumptions = event.getConsumptions();
        Activity activity = getActivity();

        if(activity == null || _mainLayout == null) // the method won't work without the activity, so let's not crash trying.
            return;

        if (_consumptions.size() == 0) {
            hideConsumptionList(_noConsumption);
        }
        else {
            if (_listView.getVisibility() == View.GONE) {
                showConsumptionList(_noConsumption);
            }

            if(_listView.getAdapter() == null) {
                Timber.d("Creating new ConsumptionRecyclerAdapter");
                _adapter = new ConsumptionRecyclerAdapter(_consumptions, _context);

                _adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        super.onItemRangeChanged(positionStart, itemCount);

                        plotGraph(_adapter.getConsumptions());
                    }

                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);

                        if (positionStart == 0) {
                            _listView.scrollToPosition(0);
                        }

                        showConsumptionList(_noConsumption);
                        plotGraph(_adapter.getConsumptions());
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);

                        Timber.d("onItemRangeRemoved");
                        plotGraph(_adapter.getConsumptions());
                        consumptionsReceived(new LoadedConsumptionsEvent(_adapter.getConsumptions()));
                    }

                    @Override
                    public void onChanged() {
                        super.onChanged();
                        plotGraph(_adapter.getConsumptions());
                    }
                });

                //This is the code to provide a sectioned list
                List<SimpleSectionedRecyclerViewAdapter.Section> sections = getConsumptionSections();

                //Add your adapter to the sectionAdapter
                SimpleSectionedRecyclerViewAdapter sectionedAdapter = new
                        SimpleSectionedRecyclerViewAdapter(_context, R.layout.section, R.id.section_text, _adapter);
                sectionedAdapter.setSections(sections);

                _bus.register(_adapter);

                if (_listView.getAdapter() != null) {
                    _bus.unregister(_listView.getAdapter());
                }

                _listView.setAdapter(_adapter);
                _listView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        View view = v.findViewById(R.id.graph_layout);
                        if (view != null)
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                });

                plotGraph(_consumptions);
                _progress.hide();
            }
        }
        TrackerHelper.updateUserProfile(activity, _pills.size(), _consumptions, _statistics);

        _statistics.refreshConsumptionCaches(_consumptions);
    }

    private void showConsumptionList(TextView noConsumption) {
        _listView.setVisibility(View.VISIBLE);
        noConsumption.setVisibility(View.GONE);
    }

    private void hideConsumptionList(TextView noConsumption) {
        noConsumption.setVisibility(View.VISIBLE);
        _listView.setVisibility(View.GONE);
    }

    @DebugLog
    private void plotGraph(List<Consumption> consumptions) {
        Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(consumptions, 7);
        if (xPoints != null)
            plotGraph(xPoints, 7, _graph);
    }

    @Subscribe
    public void consumptionTakenAgain(TakeConsumptionAgainEvent event){
        _listView.scrollToPosition(0);

        _jobManager.addJobInBackground(new InsertConsumptionsJob(event.getConsumptionsToInsert()));
    }

    @Subscribe
    public void consumptionDeleted(DeleteConsumptionEvent event){
        _jobManager.addJobInBackground(new DeleteConsumptionJob(event.getConsumption(), true));
    }

    private List<SimpleSectionedRecyclerViewAdapter.Section> getConsumptionSections() {
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        DateTime now = DateTime.now();
        DateTime startOfToday = now.withTimeAtStartOfDay();
        DateTime startOfTomorrow = startOfToday.plusDays(1);
        DateTime currentConsumptionDate = DateTime.now().plusDays(1).withTimeAtStartOfDay();

        for(int i = 0; i < _consumptions.size(); i++){
            Consumption consumption = _consumptions.get(i);

            DateTime consumptionDate = new DateTime(consumption.getDate());

            if(consumptionDate.withTimeAtStartOfDay().isBefore(currentConsumptionDate) == false){
                continue;
            }

            // new earlier date
            currentConsumptionDate = consumptionDate.withTimeAtStartOfDay();

            String text;

            if(consumptionDate.isBefore(startOfTomorrow)
                    && consumptionDate.isAfter(startOfToday)){
                text = "Today";
            }
            else{
                if(consumptionDate.plusDays(1).isBefore(startOfTomorrow)
                        && consumptionDate.plusDays(1).isAfter(startOfToday)) {
                    text = "Yesterday";
                }
                else{
                    text = DateHelper.getPrettyDayOfMonth(consumptionDate);
                }
            }

            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(i, text));
        }
        return sections;
    }

    private int getGraphDays(){
        DateTime aWeekAgo = new DateTime().minusWeeks(1);
        Days totalDays = Days.daysBetween(aWeekAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        return totalDays.getDays();
    }

    @Subscribe
    public void replotGraph(RedrawGraphEvent event){
        replotGraph();
    }

    public void replotGraph(){
        int dayCount = getGraphDays();

        Map<Pill, SparseIntArray> xPoints = (Map<Pill, SparseIntArray>) _graph.getTag();
        plotGraph(xPoints, dayCount, _graph);
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

    @Subscribe @DebugLog
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

    @DebugLog
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
            if(_consumptions == null || _consumptions.size() == 0) {
                _jobManager.addJobInBackground(new LoadConsumptionsJob(true));
            }
            else{
                if(_listView != null && _listView.getAdapter() == null){
                    consumptionsReceived(new LoadedConsumptionsEvent(_consumptions));
                }
            }
        }
    }

    @Subscribe @DebugLog
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
                    consumptionsReceived(new LoadedConsumptionsEvent(_adapter.getConsumptions()));
                }
            }
        };
        executeRunnable(runnable);
    }

    @Subscribe
    public void preferencesChanged(PreferencesChangedEvent event){
        if(_adapter != null){
            _adapter.notifyDataSetChanged();
        }
    }
}
