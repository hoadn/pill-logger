package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.dialogs.ConsumptionInfoDialog;
import uk.co.pilllogger.dialogs.InfoDialog;
import uk.co.pilllogger.dialogs.PillInfoDialog;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.DeleteConsumptionTask;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.views.ColourIndicator;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> implements
        ConsumptionInfoDialog.ConsumptionInfoDialogListener, Observer.IConsumptionAdded {

    private static String TAG = "ConsumptionListAdapter";
    private List<Consumption> _consumptions;
    private Activity _activity;
    private Fragment _fragment;
    private List<Pill> _pills;

    public ConsumptionListAdapter(Activity activity, Fragment fragment, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, consumptions);
        _activity = activity;
        _fragment = fragment;
        _consumptions = consumptions;

        Observer.getSingleton().registerPillsUpdatedObserver(new Observer.IPillsUpdated() {
            @Override
            public void pillsUpdated(Pill pill) {
                notifyDataSetChanged();
            }
        });

        Observer.getSingleton().registerConsumptionAddedObserver(this);

        Observer.getSingleton().registerConsumptionDialogObserver(this);
    }

    public ConsumptionListAdapter(Activity activity, Fragment fragment, int textViewResourceId, List<Consumption> consumptions, List<Pill> pills) {
        this(activity, fragment, textViewResourceId, consumptions);
        _pills = pills;
    }

    @Override
    public void onDialogTakeAgain(Consumption consumption, InfoDialog dialog) {
        Date consumptionDate = new Date();
        String consumptionGroup = UUID.randomUUID().toString();

        for(int i = 0; i < consumption.getQuantity(); i++){
            Consumption newC = new Consumption(consumption);
            newC.setId(0);
            newC.setGroup(consumptionGroup);
            newC.setDate(consumptionDate);
            newC.setQuantity(1);

            new InsertConsumptionTask(_activity, newC).execute();
        }

        TrackerHelper.addConsumptionEvent(_activity, "DialogTakeAgain");
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogIncrease(Consumption consumption, InfoDialog dialog) {
        Consumption newC = new Consumption(consumption);
        newC.setId(0);
        newC.setQuantity(1);
        new InsertConsumptionTask(_activity, newC).execute();
        TrackerHelper.addConsumptionEvent(_activity, "DialogIncrease");
        Toast.makeText(_activity, R.string.consumption_info_dialog_increase_toast, Toast.LENGTH_SHORT).show();
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogDecrease(Consumption consumption, InfoDialog dialog) {
        new DeleteConsumptionTask(_activity, consumption, false).execute();
        TrackerHelper.deleteConsumptionEvent(_activity, "DialogDecrease");
        Toast.makeText(_activity, R.string.consumption_info_dialog_decrease_toast, Toast.LENGTH_SHORT).show();
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogDelete(Consumption consumption, InfoDialog dialog) {
        new DeleteConsumptionTask(_activity, consumption, true).execute();
        TrackerHelper.deleteConsumptionEvent(_activity, "DialogDelete");
        dialog.getActivity().finish();
    }

    @Override
    public void consumptionAdded(Consumption consumption) {
        if(_activity != null) {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG, "consumptionAdded");
                    notifyDataSetChanged();
                }
            });
        }
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
        holder.dayText = (TextView) v.findViewById(R.id.day_text);
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
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.consumption_graph_layout, null);

                int dayCount = getGraphDays();

                View view = v.findViewById(R.id.main_graph);
                setUpSlidingPane(v);
                setUpGraphPillsList(v);

                if (_consumptions.size() > 0) {
                    Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(_consumptions, dayCount);
                    if (xPoints != null)
                        plotGraph(xPoints, dayCount, view);
                }
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
                                TrackerHelper.showInfoDialogEvent(_activity, TAG);
                                startDialog(consumption.getId());
                        }
                    });

                    if(consumption.getPill() != null){
                        holder.name.setText(consumption.getPill().getName());

                        if(consumption.getPill().getSize() == 0) {
                            holder.size.setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.size.setText(NumberHelper.getNiceFloatString(consumption.getPill().getSize()) + consumption.getPill().getUnits());
                            holder.size.setVisibility(View.VISIBLE);
                        }
                        holder.colour.setColour(consumption.getPill().getColour());
                    }
                    holder.date.setText(DateHelper.getRelativeDateTime(_fragment.getActivity(), consumption.getDate()));
                    holder.quantity.setText(String.valueOf(consumption.getQuantity()));
                    holder.consumption = consumption;

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.container.getLayoutParams();
                    DateTime currentDate = new DateTime(consumption.getDate());

                    holder.dayText.setText("");

                    if(params != null){
                        params.topMargin = 10;
                        params.bottomMargin = 0;

                        boolean setText = false;

                        int padding = _activity.getResources().getDimensionPixelSize(R.dimen.list_item_padding);
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

    private void setUpGraphPillsList(View v) {
        List<Integer> graphPills = State.getSingleton().getGraphExcludePills();
        if (graphPills == null) {
            graphPills = new ArrayList<Integer>();
        }
        State.getSingleton().setGraphExcludePills(graphPills);

        ListView list = (ListView) v.findViewById(R.id.graph_drawer);
        if (list != null){ //we need to init the adapter
            Logger.v(TAG, "Pills have been recieved and the list is not null");
            if (_pills != null) {
                GraphPillListAdapter adapter = new GraphPillListAdapter(_activity, R.layout.graph_pill_list, _pills);
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
                        TrackerHelper.filterGraphEvent(_activity, TAG);
                        ((ConsumptionListFragment)_fragment).replotGraph();
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

    private void startDialog(int consumptionId) {
        Intent intent = new Intent(_activity, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Consumption.ordinal());
        intent.putExtra("ConsumptionId", consumptionId);
        _activity.startActivity(intent);
    }

    private void setUpSlidingPane(View v) {
        final SlidingPaneLayout slidingView = (SlidingPaneLayout)v.findViewById(R.id.graph_drawer_layout);

        Display display = _activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)slidingView.getLayoutParams();
        params.width = width;

        slidingView.setSliderFadeColor(_activity.getResources().getColor(android.R.color.transparent));
        final ImageView graphSettings  = (ImageView) v.findViewById(R.id.graph_settings);
        slidingView.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            boolean moving = false;
            @Override
            public void onPanelSlide(View view, float v) {
                Logger.v(TAG, "moving = " + moving + " V = " + v);
                if (moving == false) {
                    if (v < 0.5) {
                        Logger.v(TAG, "V = Setting to previous");
                        graphSettings.setImageDrawable(_activity.getResources().getDrawable(R.drawable.previous));
                        moving = true;
                    }
                    else {
                        Logger.v(TAG, "V = Setting to next");
                        graphSettings.setImageDrawable(_activity.getResources().getDrawable(R.drawable.next));
                        moving = true;
                    }
                }
            }

            @Override
            public void onPanelOpened(View view) {
                graphSettings.setImageDrawable(_activity.getResources().getDrawable(R.drawable.previous));
                moving = false;
            }

            @Override
            public void onPanelClosed(View view) {
                graphSettings.setImageDrawable(_activity.getResources().getDrawable(R.drawable.next));
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
