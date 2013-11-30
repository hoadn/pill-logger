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
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Nick on 11/11/13.
 */
public class PillsListBaseAdapter extends ActionBarArrayAdapter<Pill> {
    protected Typeface _openSans;

    public PillsListBaseAdapter(Activity activity, int textViewResourceId, List<Pill> pills){
        this(activity, textViewResourceId, 0, pills);
    }

    public PillsListBaseAdapter(Activity activity, int textViewResourceId, int menu, List<Pill> pills) {
        super(activity, textViewResourceId, menu, pills);
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    @Override
    protected boolean actionItemClicked(ActionMode mode, MenuItem item) {
        return false;
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


    @Override
    protected void initViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.pill_list_name);
        holder.size = (TextView) v.findViewById(R.id.pill_list_size);
        holder.favourite = v.findViewById(R.id.pill_list_favourite);
        holder.colour = (ColourIndicator) v.findViewById(R.id.pill_list_colour);
        holder.pickerContainer = (ViewGroup) v.findViewById(R.id.pill_list_colour_picker_container);

        holder.name.setTypeface(_openSans);
        holder.size.setTypeface(_openSans);
        v.setTag(holder);
    }

    @Override
    protected boolean onClickListenerSet(View view, Menu menu) {
        return false;
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
                holder.size.setText(String.valueOf(pill.getSize()) + pill.getUnits());

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
