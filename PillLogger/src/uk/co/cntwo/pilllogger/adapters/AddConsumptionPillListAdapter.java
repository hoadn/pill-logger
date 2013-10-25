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

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Pill;


/**
 * Created by nick on 22/10/13.
 */
public class AddConsumptionPillListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
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
            holder.name.setTypeface(_openSans);
            holder.size.setTypeface(_openSans);
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
        }
        TextView add = (TextView)v.findViewById(R.id.add_consumption_add);
        add.setOnClickListener(new buttonClick(true, holder.amount));
        TextView minus = (TextView)v.findViewById(R.id.add_consumption_minus);
        minus.setOnClickListener(new buttonClick(false, holder.amount));
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

    private class buttonClick implements View.OnClickListener {

        private boolean _add;
        private TextView _amount;

        public buttonClick(boolean add, TextView amount) {
            _add = add;
            _amount = amount;
        }

        @Override
        public void onClick(View view) {
            if (_add) {
                int amount = Integer.parseInt(_amount.getText().toString()) + 1;
                _amount.setText(String.valueOf(amount));
            }
            else {
                int amount = Integer.parseInt(_amount.getText().toString()) - 1;
                if (amount >= 0)
                    _amount.setText(String.valueOf(amount));
            }

        }
    }
}

