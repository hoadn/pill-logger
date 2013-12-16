package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.StackBarGraph;

import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.GraphHelper;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.mappers.ConsumptionMapper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.DeleteConsumptionTask;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.views.ColourIndicator;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> {

    private static String TAG = "ConsumptionListAdapter";
    private Consumption _selectedConsumption;
    private List<Consumption> _consumptions;
    private Fragment _fragment;

    public ConsumptionListAdapter(Activity activity, Fragment fragment, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, R.menu.consumption_list_item_menu, consumptions);
        _fragment = fragment;
        _consumptions = consumptions;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView date;
        public TextView quantity;
        public ColourIndicator colour;
        public TextView size;
        Consumption consumption;
    }

    @Override
    protected boolean actionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pill_list_item_menu_delete:
                int index = _data.indexOf(_selectedConsumption);
                removeAtPosition(index); //remove() doesn't like newly created pills, so remove manually

                new DeleteConsumptionTask(_activity, _selectedConsumption).execute();
                if (_fragment instanceof ConsumptionListFragment) {
                    new GetConsumptionsTask(_activity, (GetConsumptionsTask.ITaskComplete)_fragment, false).execute();
                }
                notifyDataSetChanged();
                mode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.consumption_list_name);
        holder.date = (TextView) v.findViewById(R.id.consumption_list_date);
        holder.quantity = (TextView) v.findViewById(R.id.consumption_list_quantity);
        holder.colour = (ColourIndicator) v.findViewById(R.id.consumption_list_colour);
        holder.size = (TextView) v.findViewById(R.id.consumption_list_size);
        if(holder.name != null){
            holder.name.setTypeface(State.getSingleton().getTypeface());
            holder.date.setTypeface(State.getSingleton().getTypeface());
            //holder.quantity.setTypeface(State.getSingleton().getTypeface());
            holder.size.setTypeface(State.getSingleton().getTypeface());
        }
        v.setTag(holder);
    }

    @Override
    protected boolean onClickListenerSet(View view, Menu menu) {
        ViewHolder viewHolder = (ConsumptionListAdapter.ViewHolder) view.getTag();
        _selectedConsumption = viewHolder.consumption;

        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (getItemViewType(position) == 0) {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.consumption_graph_layout, null);

                int dayCount = getGraphDays();

                Map<Pill, SparseIntArray> xPoints = ConsumptionMapper.mapByPillAndDate(_consumptions, dayCount);

                View view = v.findViewById(R.id.main_graph);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(_activity, "Graph has been clicked", Toast.LENGTH_SHORT).show();
                        Logger.v(TAG, "Graph has been clicked");
                    }
                });
                plotGraph(xPoints, dayCount, view);
            }
        }
        else {
            position = position -1;
            v = super.getView(position, convertView, parent);
            if(v != null){
                ViewHolder holder = (ViewHolder) v.getTag();
                Consumption consumption = _data.get(position);
                if (consumption != null) {
                    if(consumption.getPill() != null){
                        holder.name.setText(consumption.getPill().getName());
                        holder.size.setText(consumption.getPill().getSize() + consumption.getPill().getUnits());
                        holder.colour.setColour(consumption.getPill().getColour());
                    }
                    holder.date.setText(DateHelper.getRelativeDateTime(_fragment.getActivity(), consumption.getDate()));
                    holder.quantity.setText(String.valueOf(consumption.getQuantity()));
                    holder.consumption = consumption;
                }
            }
        }
        return v;
    }

    private int getGraphDays(){
        DateTime aWeekAgo = new DateTime().minusWeeks(1);
        Days totalDays = Days.daysBetween(aWeekAgo.withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        return totalDays.getDays();
    }

//    public void replotGraph(){
//        int dayCount = getGraphDays();
//
//        View view = _mainLayout.findViewById(R.id.main_graph);
//
//        Map<Pill, SparseIntArray> xPoints = (Map<Pill, SparseIntArray>) view.getTag();
//        plotGraph(xPoints, dayCount, view);
//    }

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
        return super.getCount() + 1;
    }
}
