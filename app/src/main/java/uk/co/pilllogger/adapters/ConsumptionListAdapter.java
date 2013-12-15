package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.fragments.ConsumptionListFragment;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.tasks.DeleteConsumptionTask;
import uk.co.pilllogger.tasks.GetConsumptionsTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ActionBarArrayAdapter<Consumption> {

    private Consumption _selectedConsumption;
    private Fragment _fragment;

    public ConsumptionListAdapter(Activity activity, Fragment fragment, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, R.menu.consumption_list_item_menu, consumptions);
        _fragment = fragment;
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
        View v = super.getView(position, convertView, parent);
        if(v != null){
            ViewHolder holder = (ViewHolder) v.getTag();

            Consumption consumption = _data.get(position);
            if (consumption != null && consumption.getPill() != null) {
                holder.name.setText(consumption.getPill().getName());
                holder.date.setText(DateHelper.getRelativeDateTime(_fragment.getActivity(), consumption.getDate()));
                int quantity = consumption.getQuantity();
                holder.quantity.setText(String.valueOf(consumption.getQuantity()));
                holder.consumption = consumption;
                holder.colour.setColour(consumption.getPill().getColour());
                holder.size.setText(consumption.getPill().getSize() + consumption.getPill().getUnits());
            }
        }
        return v;
    }
}
