package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.List;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Nick on 11/11/13.
 */
public abstract class PillsListBaseAdapter extends ActionBarArrayAdapter<Pill> {

    protected ConsumptionRepository _consumptionRepository;

    @DebugLog
    public PillsListBaseAdapter(Context context, int textViewResourceId, List<Pill> pills, ConsumptionRepository consumptionRepository) {
        super(context, textViewResourceId, pills);
        _consumptionRepository = consumptionRepository;
    }

    @Override
    public void destroy() {

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
        public boolean selected;
        public ViewGroup container;
        public View shadow;
    }

    @Override @DebugLog
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.container = (ViewGroup) v.findViewById(R.id.selector_container);
        holder.name = (TextView) v.findViewById(R.id.pill_list_name);
        holder.lastTaken = (TextView) v.findViewById(R.id.pill_list_last_taken);
        holder.size = (TextView) v.findViewById(R.id.pill_list_size);
        holder.favourite = v.findViewById(R.id.pill_list_favourite);
        holder.colour = (ColourIndicator) v.findViewById(R.id.pill_list_colour);
        holder.pickerContainer = (ViewGroup) v.findViewById(R.id.pill_list_colour_picker_container);
        holder.shadow = v.findViewById(R.id.shadow);

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

                Consumption latest = pill.getLatestConsumption(_consumptionRepository);
                if(latest != null){
                    String prefix = _context.getString(R.string.last_taken_message_prefix);
                    String lastTaken = DateHelper.getRelativeDateTime(_context, latest.getDate(), true);
                    holder.lastTaken.setText(prefix + " " + lastTaken);
                }
                else{
                    holder.lastTaken.setText(_context.getString(R.string.no_consumptions_message));
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
