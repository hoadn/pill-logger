package uk.co.pilllogger.adapters;

/**
 * Created by nick on 25/10/13.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;
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
    private List<Pill> _consumptionPills = new ArrayList<Pill>();
    private Activity _activity;
    private Typeface _openSans;
    private int _resourceId;

    public AddConsumptionPillListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
        _pills = pills;
        _resourceId = textViewResourceId;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
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
            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);
            if(v != null){
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.pill_list_name);
                holder.size = (TextView) v.findViewById(R.id.pill_list_size);
                holder.units = (TextView) v.findViewById(R.id.pill_list_units);
                holder.lastTaken = (TextView) v.findViewById(R.id.pill_list_last_taken);
                holder.color = (ColourIndicator) v.findViewById(R.id.add_consumption_pill_colour);
                holder.name.setTypeface(_openSans);
                holder.size.setTypeface(_openSans);
                holder.units.setTypeface(_openSans);
                holder.lastTaken.setTypeface(_openSans);
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
            holder.size.setText(String.valueOf(pill.getSize()));
            holder.color.setColour(pill.getColour());
            Map<Pill, Integer> openPills = State.getSingleton().getOpenPillsList();
            if (State.getSingleton().getOpenPillsList().containsKey(pill)) {
                v = open(v);
                holder.amount.setText(openPills.get(pill).toString());
            }
            else {
                Logger.v("PillName", "pill: " + pill.getName() + " pill id: " + pill.getId());
                v = close(v);
                for (Pill aPill : openPills.keySet()) {
                    Logger.v("PillName", "open pill " + aPill.getName() + " id: " + aPill.getId());
                }
            }

            Consumption latest = pill.getLatestConsumption();
            if(latest != null){
                String prefix = _activity.getString(R.string.last_taken_message_prefix);
                String lastTaken = DateHelper.getRelativeDateTime(_activity, latest.getDate());
                holder.lastTaken.setText(prefix + " " + lastTaken);
            }
            else{
                holder.lastTaken.setText(_activity.getString(R.string.no_consumptions_message));
            }
        }
        TextView add = (TextView)v.findViewById(R.id.add_consumption_add);
        add.setOnClickListener(new buttonClick(true, holder.amount, position, this));
        TextView minus = (TextView)v.findViewById(R.id.add_consumption_minus);
        minus.setOnClickListener(new buttonClick(false, holder.amount, position, this));
        return v;
    }

    private View open(View v) {
        int backgroundColor = _activity.getResources().getColor(R.color.done_cancel_grey);
        v.setBackgroundColor(backgroundColor);
        View view = v.findViewById(R.id.add_consumption_after_click_layout);
        if(view != null){
            view.setBackgroundColor(backgroundColor);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if(layoutParams != null)
                layoutParams.width = (int) LayoutHelper.dpToPx(_activity, 125);
        }
        return v;
    }

    private View close(View v) {
        int backgroundColor = _activity.getResources().getColor(android.R.color.transparent);
        v.setBackgroundColor(backgroundColor);
        View view = v.findViewById(R.id.add_consumption_after_click_layout);
        if(view != null){
            view.setBackgroundColor(backgroundColor);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if(layoutParams != null)
                layoutParams.width = (int) LayoutHelper.dpToPx(_activity, 0);
        }
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

    public void addConsumedPill(Pill pill) {
        _consumptionPills.add(pill);
    }

    public void removeConsumedPill(Pill pill) {
        if (_consumptionPills.contains(pill))
            _consumptionPills.remove(pill);
    }

    /*
    This is only used when pill selected first time and the pill is auto
    added to the conumption
     */
    public void addConsumedPillAtStart(int position) {
        Pill pill = _pills.get(position);
        if (!_consumptionPills.contains(pill))
        _consumptionPills.add(pill);
    }

    /*
    This is only used when the pill is deselected to remove all instances
     */
    public void removeAllInstancesOfPill(int position) {
        Pill removePill = _pills.get(position);
        List<Pill> pillsToRemove = new ArrayList<Pill>();
        for(Pill pill : _consumptionPills) {
            if (pill.getId() == removePill.getId())
                pillsToRemove.add(pill);
        }
        for (Pill pill : pillsToRemove) {
            _consumptionPills.remove(pill);
        }
    }

    public List<Pill> getPillsConsumed() {
        return _consumptionPills;
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
            Map<Pill, Integer> openPills = State.getSingleton().getOpenPillsList();
            if (_add) {
                int amount = Integer.parseInt(_amount.getText().toString()) + 1;
                openPills.put(pill, openPills.get(pill) + 1);
                _amount.setText(String.valueOf(amount));
                _adapter.addConsumedPill(pill);
            }
            else {
                int amount = Integer.parseInt(_amount.getText().toString()) - 1;
                if (amount >= 0) {
                    openPills.put(pill, openPills.get(pill) - 1);
                    _amount.setText(String.valueOf(amount));
                    _adapter.removeConsumedPill(pill);
                }
            }
            State.getSingleton().setOpenPillsList(openPills);
        }
    }
}

