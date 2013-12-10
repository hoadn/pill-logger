package uk.co.pilllogger.adapters;

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

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Nick on 14/11/13.
 */
public class WidgetListAdapter extends ArrayAdapter{
    private List<Pill> _data = new ArrayList<Pill>();
    private Activity _activity;
    private int _resourceId;

    public WidgetListAdapter(Activity activity, int textViewResourceId, List<Pill> pills){
        super(activity, textViewResourceId, pills);
        _activity = activity;
        _resourceId = textViewResourceId;
        _data = pills;
    }

    public static class ViewHolder {
        public Pill pill;
        public TextView name;
        public TextView size;
        public TextView units;
        public View favourite;
        public ColourIndicator colour;
        public ViewGroup pickerContainer;
        public boolean open;
    }

    protected void initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.pill_list_name);
        holder.size = (TextView) v.findViewById(R.id.pill_list_size);
        holder.units = (TextView) v.findViewById(R.id.pill_list_units);
        holder.favourite = v.findViewById(R.id.pill_list_favourite);
        holder.colour = (ColourIndicator) v.findViewById(R.id.pill_list_colour);
        holder.pickerContainer = (ViewGroup) v.findViewById(R.id.pill_list_colour_picker_container);

        holder.name.setTypeface(State.getSingleton().getTypeface());
        holder.size.setTypeface(State.getSingleton().getTypeface());
        holder.units.setTypeface(State.getSingleton().getTypeface());
        v.setTag(holder);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);

            initViewHolder(v);
        }
        ViewHolder holder;
        if (v != null) {
            holder = (ViewHolder)v.getTag();

            final Pill pill = _data.get(position);
            if (pill != null) {
                holder.name.setText(pill.getName());
                holder.size.setText(String.valueOf(pill.getSize()));

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
