package uk.co.pilllogger.adapters;

/**
 * Created by nick on 25/10/13.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;


/**
 * Created by nick on 22/10/13.
 */
public class
        AddConsumptionPillListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
    private IConsumptionSelected _consumptionSelectedListener;
    private Context _context;
    private int _resourceId;


    public void addOpenPill(Pill pill) {
        State.getSingleton().addOpenPill(pill);
        _consumptionSelectedListener.setDoneEnabled(true);
    }

    public void removeOpenPill(Pill pill) {
        State.getSingleton().removeOpenPill(pill);
        if (!(State.getSingleton().getOpenPills().size() > 0))
            _consumptionSelectedListener.setDoneEnabled(false);
    }

    public void clearOpenPillsList() {
        State.getSingleton().clearOpenPillsList();
        State.getSingleton().clearConsumpedPills();
        _consumptionSelectedListener.setDoneEnabled(false);
    }

    public AddConsumptionPillListAdapter(Context context, IConsumptionSelected activity, int textViewResourceId, List<Pill> pills) {
        super(context, textViewResourceId, pills);
        _consumptionSelectedListener = activity;
        _context = context;
        _pills = pills;
        _resourceId = textViewResourceId;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView size;
        public TextView units;
        public TextView lastTaken;
        public View buttonLayout;
        public View container;
        public TextView amount;
        public ColourIndicator color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);
            if(v != null){
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.pill_list_name);
                holder.size = (TextView) v.findViewById(R.id.pill_list_size);
                holder.units = (TextView) v.findViewById(R.id.pill_list_units);
                holder.lastTaken = (TextView) v.findViewById(R.id.pill_list_last_taken);
                holder.color = (ColourIndicator) v.findViewById(R.id.add_consumption_pill_colour);
                Typeface typeface = State.getSingleton().getTypeface();
                holder.name.setTypeface(typeface);
                holder.size.setTypeface(typeface);
                holder.units.setTypeface(typeface);
                holder.lastTaken.setTypeface(typeface);
                holder.buttonLayout = v.findViewById(R.id.add_consumption_after_click_layout);
                holder.container = v;
                holder.amount = (TextView) v.findViewById(R.id.add_consumption_amount);
                v.setTag(holder);
            }
        }
        else
            holder=(ViewHolder) v.getTag();

        Pill pill = _pills.get(position);
        if (pill != null && holder != null) {
            holder.name.setText(pill.getName());
            if(pill.getSize() <= 0){
                holder.size.setVisibility(View.INVISIBLE);
                holder.units.setVisibility(View.INVISIBLE);
            }
            else{
                holder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()));
                holder.units.setText(pill.getUnits());
                holder.size.setVisibility(View.VISIBLE);
                holder.units.setVisibility(View.VISIBLE);
            }
            holder.color.setColour(pill.getColour());

            Consumption latest = pill.getLatestConsumption(_context);
            if(latest != null){
                String prefix = _context.getString(R.string.last_taken_message_prefix);
                String lastTaken = DateHelper.getRelativeDateTime(_context, latest.getDate());
                holder.lastTaken.setText(prefix + " " + lastTaken);
            }
            else{
                holder.lastTaken.setText(_context.getString(R.string.no_consumptions_message));
            }
        }
        View add = v.findViewById(R.id.add_consumption_add);
        add.setOnClickListener(new buttonClick(true, holder.amount, position, this));
        View minus = v.findViewById(R.id.add_consumption_minus);
        minus.setOnClickListener(new buttonClick(false, holder.amount, position, this));
        return v;
    }

    @Override
    public int getCount() {
        if (_pills != null)
            return _pills.size();
        return 0;
    }

    public void updateAdapter(List<Pill> pills) {
        _pills = pills;
        this.notifyDataSetChanged();
    }

    public void clearConsumedPills() {
        State.getSingleton().clearConsumpedPills();
    }
    
    public void addConsumedPill(Pill pill) {
        State.getSingleton().addConsumedPill(pill);

        _consumptionSelectedListener.setDoneEnabled(State.getSingleton().getConsumptionPills().size() > 0);
    }

    public void removeConsumedPill(Pill pill) {
        State.getSingleton().removeConsumedPill(pill);

        _consumptionSelectedListener.setDoneEnabled(State.getSingleton().getConsumptionPills().size() > 0);
    }

    /*
    This is only used when pill selected first time and the pill is auto
    added to the conumption
     */
    public void addConsumedPillAtStart(int position) {
        Pill pill = _pills.get(position);
        State.getSingleton().addConsumedPillAtStart(pill);
    }

    /*
    This is only used when the pill is deselected to remove all instances
     */
    public void removeAllInstancesOfPill(int position) {
        Pill removePill = _pills.get(position);
        State.getSingleton().removeAllInstancesOfPill(removePill);
    }

    public List<Pill> getPillsConsumed() {
        return State.getSingleton().getConsumptionPills();
    }

    private class buttonClick implements View.OnClickListener {

        private boolean _add;
        private TextView _amount;
        private int _position;
        private AddConsumptionPillListAdapter _adapter;

        public buttonClick(boolean add, TextView amount, int position, AddConsumptionPillListAdapter adapter) {
            _add = add;
            _amount = amount;
            _position = position;
            _adapter = adapter;
        }

        @Override
        public void onClick(View view) {
            Pill pill = _pills.get(_position);
            Integer value = State.getSingleton().getOpenPills().get(pill);
            if(value == null) value = 0;

            if (_add) {
                int amount = Integer.parseInt(_amount.getText().toString()) + 1;
                State.getSingleton().getOpenPills().put(pill, value + 1);
                _amount.setText(String.valueOf(amount));
                _adapter.addConsumedPill(pill);
            }
            else {
                int amount = Integer.parseInt(_amount.getText().toString()) - 1;
                if (amount >= 0 && value > 0) {
                    State.getSingleton().getOpenPills().put(pill, value - 1);
                    _amount.setText(String.valueOf(amount));
                    _adapter.removeConsumedPill(pill);
                }
            }
        }
    }

    public interface IConsumptionSelected{
        void setDoneEnabled(boolean enabled);
    }
}

