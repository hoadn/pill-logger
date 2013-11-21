package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.PieGraph;

import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.MainFragment;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.tasks.DeleteConsumptionTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> {

    private Typeface _openSans;
    private Consumption _selectedConsumption;
    private Fragment _fragment;

    public ConsumptionListAdapter(Activity activity, Fragment fragment, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, R.menu.consumption_list_item_menu, consumptions);
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
        _fragment = fragment;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView date;
        public TextView quantity;
        public ColourIndicator colour;
        Consumption consumption;
    }

    @Override
    protected boolean actionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pill_list_item_menu_delete:
                int index = _data.indexOf(_selectedConsumption);
                removeAtPosition(index); //remove() doesn't like newly created pills, so remove manually

                new DeleteConsumptionTask(_activity, _selectedConsumption).execute();
                if (_fragment instanceof MainFragment) {
                    View view = _fragment.getView().findViewById(R.id.main_graph);

                    if(view instanceof LineGraph)
                        ((LineGraph) view).refreshDrawableState();

                    if(view instanceof BarGraph)
                        ((BarGraph) view).refreshDrawableState();

                    if(view instanceof PieGraph)
                        ((PieGraph) view).refreshDrawableState();
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
        if(holder.name != null){
            holder.name.setTypeface(_openSans);
            holder.date.setTypeface(_openSans);
            holder.quantity.setTypeface(_openSans);
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
        View v = super.getView(position, convertView, parent);
        if(v != null){
            ViewHolder holder = (ViewHolder) v.getTag();

            Consumption consumption = _data.get(position);
            if (consumption != null) {
                holder.name.setText(consumption.get_pill().getName());
                String date = new SimpleDateFormat("HH:mm dd/MM").format(consumption.get_date());
                holder.date.setText(date);
                holder.quantity.setText(consumption.getQuantity() + "x");
                holder.consumption = consumption;
                holder.colour.setColour(consumption.get_pill().getColour());
            }
        }
        return v;
    }
}
