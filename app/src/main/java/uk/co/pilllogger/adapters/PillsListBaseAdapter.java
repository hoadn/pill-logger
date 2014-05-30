package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Nick on 11/11/13.
 */
public class PillsListBaseAdapter extends ActionBarArrayAdapter<Pill> {

    public PillsListBaseAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
    }

    public static class ViewHolder extends ActionBarArrayAdapter.ViewHolder {
        public Pill pill;
        public TextView name;
        public TextView lastTaken;
        public TextView size;
        public TextView units;
        public View favourite;
        public ColourIndicator colour;
        public ViewGroup pickerContainer;
        public boolean open;
    }

    @Override
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.pill_list_name);
        holder.lastTaken = (TextView) v.findViewById(R.id.pill_list_last_taken);
        holder.size = (TextView) v.findViewById(R.id.pill_list_size);
        holder.favourite = v.findViewById(R.id.pill_list_favourite);
        holder.colour = (ColourIndicator) v.findViewById(R.id.pill_list_colour);
        holder.pickerContainer = (ViewGroup) v.findViewById(R.id.pill_list_colour_picker_container);

        holder.name.setTypeface(State.getSingleton().getTypeface());
        holder.size.setTypeface(State.getSingleton().getTypeface());
        holder.lastTaken.setTypeface(State.getSingleton().getTypeface());
        v.setTag(holder);

        return holder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ViewHolder holder;
        if (v != null) {
            holder = (ViewHolder)v.getTag();

            final Pill pill = _data.get(position);
            if (pill != null) {
                holder.name.setText(pill.getName());

                Consumption latest = pill.getLatestConsumption(_activity);
                if(latest != null){
                    String prefix = _activity.getString(R.string.last_taken_message_prefix);
                    String lastTaken = DateHelper.getRelativeDateTime(_activity, latest.getDate(), true);
                    holder.lastTaken.setText(prefix + " " + lastTaken);
                }
                else{
                    holder.lastTaken.setText(_activity.getString(R.string.no_consumptions_message));
                }

                if(pill.getSize() <= 0){
                    holder.size.setVisibility(View.INVISIBLE);
                }
                else{
                    holder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()) + pill.getUnits());
                    holder.size.setVisibility(View.VISIBLE);
                }

                int visibility = pill.isFavourite() ? View.VISIBLE : View.INVISIBLE;
                if (holder.favourite != null)
                    holder.favourite.setVisibility(visibility);

                holder.colour.setColour(pill.getColour());

                holder.pill = pill;
            }
        }

        return v;
    }

    @Override
    public int getCount() {
        if (_data != null)
            return _data.size();
        return 0;
    }

    public Pill getPillAtPosition(int pos) {
        if (_data == null || pos > _data.size() || pos < 0)
            return null;

        return _data.get(pos);
    }

    public void updateAdapter(List<Pill> pills) {
        _data = pills;
        this.notifyDataSetChanged();
    }
}
