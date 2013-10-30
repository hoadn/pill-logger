package uk.co.cntwo.pilllogger.adapters;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.helpers.LayoutHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.state.State;


/**
 * Created by nick on 22/10/13.
 */
public class AddConsumptionPillListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
    private List<Pill> _consumptionPills = new ArrayList<Pill>();
    private Activity _activity;
    private Typeface _openSans;
    private int _resouceId;

    public AddConsumptionPillListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
        _pills = pills;
        _resouceId = textViewResourceId;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    public static class ViewHolder {
        public TextView name;
        public TextView size;
        public TextView units;
        public View buttonLayout;
        public View container;
        public TextView amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resouceId, null);
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.pill_list_name);
            holder.size = (TextView) v.findViewById(R.id.pill_list_size);
            holder.units = (TextView) v.findViewById(R.id.pill_list_units);
            holder.name.setTypeface(_openSans);
            holder.size.setTypeface(_openSans);
            holder.units.setTypeface(_openSans);
            holder.buttonLayout = v.findViewById(R.id.add_consumption_after_click_layout);
            holder.container = v;
            holder.amount = (TextView) v.findViewById(R.id.add_consumption_amount);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder) v.getTag();

        Pill pill = _pills.get(position);
        if (pill != null) {
            holder.name.setText(pill.getName());
            holder.size.setText(String.valueOf(pill.getSize()));
            Map<Pill, Integer> openPills = State.getSingleton().getOpenPillsList();
            Logger.v("Testing", "openPillsList size: " + openPills.size());
            if (State.getSingleton().getOpenPillsList().containsKey(pill)) {
                v = open(v);
                holder.amount.setText(openPills.get(pill).toString());
            }
            else {
                v = close(v);
            }
        }
        TextView add = (TextView)v.findViewById(R.id.add_consumption_add);
        add.setOnClickListener(new buttonClick(true, holder.amount, position, this));
        TextView minus = (TextView)v.findViewById(R.id.add_consumption_minus);
        minus.setOnClickListener(new buttonClick(false, holder.amount, position, this));
        return v;
    }

    private View open(View v) {
        v.setBackgroundColor(_activity.getResources().getColor(R.color.done_cancel_grey));
        View view = v.findViewById(R.id.add_consumption_after_click_layout);
        view.getLayoutParams().width = (int) LayoutHelper.dpToPx(_activity, 125);
        return v;
    }

    private View close(View v) {
        v.setBackgroundColor(_activity.getResources().getColor(android.R.color.transparent));
        View view = v.findViewById(R.id.add_consumption_after_click_layout);
        view.getLayoutParams().width = (int) LayoutHelper.dpToPx(_activity, 0);
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

