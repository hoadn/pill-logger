package uk.co.cntwo.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.tasks.DeleteConsumptionTask;
import uk.co.cntwo.pilllogger.tasks.DeletePillTask;
import uk.co.cntwo.pilllogger.tasks.UpdatePillTask;
import uk.co.cntwo.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> {

    private Typeface _openSans;
    private Consumption _selectedConsumption;

    public ConsumptionListAdapter(Activity activity, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, R.menu.consumption_list_item_menu, consumptions);
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    public static class ViewHolder {
        public TextView name;
        public TextView date;
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
        holder.colour = (ColourIndicator) v.findViewById(R.id.consumption_list_colour);
        if(holder.name != null){
            holder.name.setTypeface(_openSans);
            holder.date.setTypeface(_openSans);
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
                holder.consumption = consumption;
                holder.colour.setColour(consumption.get_pill().getColour());
            }
        }
        return v;
    }
}
