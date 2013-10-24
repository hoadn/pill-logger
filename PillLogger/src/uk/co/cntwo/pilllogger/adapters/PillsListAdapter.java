package uk.co.cntwo.pilllogger.adapters;

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

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
    private Activity _activity;
    private Typeface _openSans;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
        _pills = pills;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    public static class ViewHolder {
        public TextView name;
        public TextView size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.pill_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.pill_list_name);
            holder.size = (TextView) v.findViewById(R.id.pill_list_size);
            holder.name.setTypeface(_openSans);
            holder.size.setTypeface(_openSans);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder) v.getTag();

        Pill pill = _pills.get(position);
        if (pill != null) {
            holder.name.setText(pill.getName());
            holder.size.setText(String.valueOf(pill.getSize()));
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
}
