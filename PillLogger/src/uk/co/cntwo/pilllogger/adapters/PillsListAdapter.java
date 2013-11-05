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
import uk.co.cntwo.pilllogger.listeners.DeletePillClickListener;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
    private Activity _activity;
    private Typeface _openSans;
    private int _resouceId;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
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
        public View favourite;
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
            holder.favourite = v.findViewById(R.id.pill_list_favourite);

            holder.name.setTypeface(_openSans);
            holder.size.setTypeface(_openSans);
            holder.units.setTypeface(_openSans);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder) v.getTag();

        Pill pill = _pills.get(position);
        if (pill != null) {
            holder.name.setText(pill.getName());
            holder.size.setText(String.valueOf(pill.getSize()));

            int visibility = pill.isFavourite() ? View.VISIBLE : View.INVISIBLE;
            holder.favourite.setVisibility(visibility);
        }
        return v;
    }

    @Override
    public int getCount() {
        if (_pills != null)
            return _pills.size();
        return 0;
    }

    public Pill getPillAtPosition(int pos){
        if(_pills == null || pos > _pills.size() || pos < 0)
            return null;

        return _pills.get(pos);
    }

    public void removeAtPosition(int pos){
        if(_pills == null || pos > _pills.size() || pos < 0)
            return;

        _pills.remove(pos);
        this.notifyDataSetChanged();
    }

    public void updateAdapter(List<Pill> pills) {
        _pills = pills;
        this.notifyDataSetChanged();
    }
}
