package uk.co.pilllogger.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DecreaseConsumptionEvent;
import uk.co.pilllogger.events.IncreaseConsumptionEvent;
import uk.co.pilllogger.events.RedrawGraphEvent;
import uk.co.pilllogger.events.TakeConsumptionAgainEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.events.DeleteConsumptionEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.DeleteConsumptionJob;
import uk.co.pilllogger.jobs.InsertConsumptionJob;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> {

    private static String TAG = "ConsumptionListAdapter";
    private JobManager _jobManager;
    private final ConsumptionRepository _consumptionRepository;
    private final Bus _bus;
    private List<Pill> _pills;

    public ConsumptionListAdapter(Context context, JobManager jobManager, ConsumptionRepository consumptionRepository, Bus bus, int textViewResourceId, List<Consumption> consumptions) {
        super(context, textViewResourceId, consumptions);
        _jobManager = jobManager;
        _consumptionRepository = consumptionRepository;
        _bus = bus;
    }
    public ConsumptionListAdapter(Context context, JobManager jobManager, ConsumptionRepository consumptionRepository, Bus bus, int textViewResourceId, List<Consumption> consumptions, List<Pill> pills) {
        this(context, jobManager, consumptionRepository, bus, textViewResourceId, consumptions);
        _pills = pills;
    }

    @Subscribe
    public void onDialogTakeAgain(TakeConsumptionAgainEvent event) {
        Date consumptionDate = new Date();
        String consumptionGroup = UUID.randomUUID().toString();

        for(int i = 0; i < event.getConsumption().getQuantity(); i++){
            Consumption newConsumption = new Consumption(event.getConsumption());
            newConsumption.setId(0);
            newConsumption.setGroup(consumptionGroup);
            newConsumption.setDate(consumptionDate);
            newConsumption.setQuantity(1);

            _jobManager.addJobInBackground(new InsertConsumptionJob(newConsumption));
        }

        TrackerHelper.addConsumptionEvent(_context, "DialogTakeAgain");
        event.getConsumptionInfoDialogFragment().getActivity().finish();
    }

    @Subscribe
    public void onDialogIncrease(IncreaseConsumptionEvent event) {
        Consumption newConsumption = new Consumption(event.getConsumption());
        newConsumption.setId(0);
        newConsumption.setQuantity(1);

        _jobManager.addJobInBackground(new InsertConsumptionJob(newConsumption));
        TrackerHelper.addConsumptionEvent(_context, "DialogIncrease");
        Toast.makeText(_context, R.string.consumption_info_dialog_increase_toast, Toast.LENGTH_SHORT).show();
        event.getConsumptionInfoDialogFragment().getActivity().finish();
    }

    @Subscribe
    public void onDialogDecrease(DecreaseConsumptionEvent event) {
        _jobManager.addJobInBackground(new DeleteConsumptionJob(event.getConsumption(), false));
        TrackerHelper.deleteConsumptionEvent(_context, "DialogDecrease");
        Toast.makeText(_context, R.string.consumption_info_dialog_decrease_toast, Toast.LENGTH_SHORT).show();
        event.getConsumptionInfoDialogFragment().getActivity().finish();
    }

    @Subscribe
    public void onDialogDelete(DeleteConsumptionEvent event) {
        _jobManager.addJobInBackground(new DeleteConsumptionJob(event.getConsumption(), true));
        TrackerHelper.deleteConsumptionEvent(_context, "DialogDelete");
        event.getConsumptionInfoDialogFragment().getActivity().finish();
    }

    @Subscribe @DebugLog
    public void consumptionAdded(CreatedConsumptionEvent event) {
        _data.add(event.getConsumption());
        Collections.sort(_data);
        _data = _consumptionRepository.groupConsumptions(_data);

        Timber.d(TAG, "Consumption list updated");
        notifyDataSetChanged();
    }

    @Subscribe @DebugLog
    public void pillsUpdated(UpdatedPillEvent event) {
        if(event.wasDeleted()) {
            _data.removeAll(event.getPill().getConsumptions());
        }

        for(Consumption c : _data){
            if(c.getPillId() == event.getPill().getId()){
                c.setPill(event.getPill());
            }
        }

        Timber.d("Updated: " + event.getPill().getColour());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends ActionBarArrayAdapter.ViewHolder{
        public TextView name;
        public TextView date;
        public TextView quantity;
        public ColourIndicator colour;
        public TextView size;
        public TextView dayText;
        public RelativeLayout container;
        Consumption consumption;
    }

    @Override
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.consumption_list_name);
        holder.date = (TextView) v.findViewById(R.id.consumption_list_date);
        holder.quantity = (TextView) v.findViewById(R.id.consumption_list_quantity);
        holder.colour = (ColourIndicator) v.findViewById(R.id.consumption_list_colour);
        holder.size = (TextView) v.findViewById(R.id.consumption_list_size);
        holder.container = (RelativeLayout) v.findViewById(R.id.selector_container);
        if(holder.name != null){
            holder.name.setTypeface(State.getSingleton().getTypeface());
            holder.date.setTypeface(State.getSingleton().getTypeface());
            //holder.quantity.setTypeface(State.getSingleton().getTypeface());
            holder.size.setTypeface(State.getSingleton().getTypeface());
            holder.dayText.setTypeface(State.getSingleton().getTypeface());
        }
        v.setTag(holder);

        return holder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (getItemViewType(position) == 0) {

            int dayCount = getGraphDays();
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.consumption_graph_layout, null);
                setUpSlidingPane(v);
                setUpGraphPillsList(v);

                View view = v.findViewById(R.id.main_graph);

                if (_data.size() > 0) {
                    Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(_data, dayCount);
                    if (xPoints != null)
                        plotGraph(xPoints, dayCount, view);
                }
            }
            else {
                View view = v.findViewById(R.id.main_graph);

                Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(_data, dayCount);
                plotGraph(xPoints, dayCount, view);
            }
        }
        else {
            position = position -1;
            v = super.getView(position, convertView, parent);
            if(v != null){
                ViewHolder holder = (ViewHolder) v.getTag();
                final Consumption consumption = _data.get(position);

                if (consumption != null) {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                TrackerHelper.showInfoDialogEvent(_context, TAG);
                                startDialog(consumption.getPillId(), consumption.getGroup());
                        }
                    });

                    Pill pill = consumption.getPill();
                    if(pill != null){
                        holder.name.setText(pill.getName());

                        if(pill.getSize() == 0) {
                            holder.size.setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()) + pill.getUnits());
                            holder.size.setVisibility(View.VISIBLE);
                        }
                        holder.colour.setColour(pill.getColour());
                        Timber.d(pill.getColour() + "");
                    }
                    holder.date.setText(DateHelper.getRelativeDateTime(_context, consumption.getDate()));
                    holder.quantity.setText(String.valueOf(consumption.getQuantity()));
                    holder.consumption = consumption;

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.container.getLayoutParams();
                    DateTime currentDate = new DateTime(consumption.getDate());

                    holder.dayText.setText("");

                    if(params != null){
                        params.topMargin = 10;
                        params.bottomMargin = 0;

                        boolean setText = false;

                        int padding = _context.getResources().getDimensionPixelSize(R.dimen.list_item_padding);
                        holder.container.setPadding(padding,padding,padding,padding);

                        if(position == 0)
                        {
                            setText = true;
                        }
                        else{
                            Consumption previousConsumption = _data.get(position - 1);
                            DateTime prevDate = new DateTime(previousConsumption.getDate());

                            if(prevDate.getYear() != currentDate.getYear() ||
                                    prevDate.getDayOfYear() != currentDate.getDayOfYear()){

                                setText = true;
                            }
                        }

                        if(setText){
                            holder.dayText.setText(DateHelper.getPrettyDayOfMonth(currentDate));

                            params.topMargin = params.topMargin + (int) (holder.dayText.getLineHeight() * 1.5f);
                        }
                    }
                }
            }
        }
        return v;
    }

    @Override
    public void destroy() {

    }

    private void setUpGraphPillsList(View v) {
        List<Integer> graphPills = State.getSingleton().getGraphExcludePills();
        if (graphPills == null) {
            graphPills = new ArrayList<Integer>();
        }
        State.getSingleton().setGraphExcludePills(graphPills);

        ListView list = (ListView) v.findViewById(R.id.graph_drawer);
        if (list != null){ //we need to init the adapter
            Timber.v("Pills have been recieved and the list is not null");
            if (_pills != null) {
                GraphPillListAdapter adapter = new GraphPillListAdapter(_context, R.layout.graph_pill_list, _pills);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Pill pill = _pills.get(position);
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
                        TrackerHelper.filterGraphEvent(_context, TAG);

                        _bus.post(new RedrawGraphEvent());
                        // todo: send event to get the plot redrawn
                        //((ConsumptionListFragment)_fragment).replotGraph();
                    }
                });

                list.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            }
        }
    }

    private void startDialog(int pillId, String consumptionGroup) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Consumption.ordinal());
        intent.putExtra("ConsumptionGroup", consumptionGroup);
        intent.putExtra("PillId", pillId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    private void setUpSlidingPane(View v) {
        final SlidingPaneLayout slidingView = (SlidingPaneLayout)v.findViewById(R.id.graph_drawer_layout);

        WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)slidingView.getLayoutParams();
        params.width = width;

        slidingView.setSliderFadeColor(_context.getResources().getColor(android.R.color.transparent));
        final ImageView graphSettings  = (ImageView) v.findViewById(R.id.graph_settings);
        slidingView.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            boolean moving = false;
            @Override
            public void onPanelSlide(View view, float v) {
                if (moving == false) {
                    if (v < 0.5) {
                        graphSettings.setImageDrawable(_context.getResources().getDrawable(R.drawable.previous));
                        moving = true;
                    }
                    else {
                        graphSettings.setImageDrawable(_context.getResources().getDrawable(R.drawable.next));
                        moving = true;
                    }
                }
            }

            @Override
            public void onPanelOpened(View view) {
                graphSettings.setImageDrawable(_context.getResources().getDrawable(R.drawable.previous));
                moving = false;
            }

            @Override
            public void onPanelClosed(View view) {
                graphSettings.setImageDrawable(_context.getResources().getDrawable(R.drawable.next));
                moving = false;
            }
        });

        graphSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingView.isOpen()) {
                    slidingView.closePane();
                }
                else {
                    slidingView.openPane();
                }
            }
        });
    }

    private int getGraphDays(){
        DateTime aWeekAgo = new DateTime().minusWeeks(1);
        Days totalDays = Days.daysBetween(aWeekAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        return totalDays.getDays();
    }

    public void plotGraph(Map<Pill, SparseIntArray> data, int dayCount, View view){
        int lineColour = _context.getResources().getColor(State.getSingleton().getTheme().getStackBarGraphLineColourResourceId());

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

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    @Override
    public int getCount() {
        return _data.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        if (position > 0)
            return position - 1;
        return  super.getItemId(position);
    }

    @Override
    public Consumption getItem(int position) {
        if (position > 0)
            return _data.get(position - 1);
        return super.getItem(position);
    }
}
