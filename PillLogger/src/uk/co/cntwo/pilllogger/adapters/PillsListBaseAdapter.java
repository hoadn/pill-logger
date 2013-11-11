package uk.co.cntwo.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.tasks.DeletePillTask;
import uk.co.cntwo.pilllogger.tasks.UpdatePillTask;
import uk.co.cntwo.pilllogger.views.ColourIndicator;

/**
 * Created by Nick on 11/11/13.
 */
public class PillsListBaseAdapter extends ArrayAdapter<Pill> {
    protected List<Pill> _pills;
    protected Activity _activity;
    protected Typeface _openSans;
    protected int _resouceId;

    public PillsListBaseAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
        _pills = pills;
        _resouceId = textViewResourceId;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    public static class ViewHolder {
        public Pill pill;
        public TextView name;
        public TextView size;
        public TextView units;
        public View favourite;
        public ColourIndicator colour;
        public ViewGroup pickerContainer;
        public ViewGroup colourContainer;
        public boolean open;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resouceId, null);
            holder = new ViewHolder();
            if (v != null) {
                holder.name = (TextView) v.findViewById(R.id.pill_list_name);
                holder.size = (TextView) v.findViewById(R.id.pill_list_size);
                holder.units = (TextView) v.findViewById(R.id.pill_list_units);
                holder.favourite = v.findViewById(R.id.pill_list_favourite);
                holder.colour = (ColourIndicator) v.findViewById(R.id.pill_list_colour);
                holder.pickerContainer = (ViewGroup) v.findViewById(R.id.pill_list_colour_picker_container);

                holder.name.setTypeface(_openSans);
                holder.size.setTypeface(_openSans);
                holder.units.setTypeface(_openSans);
                v.setTag(holder);
            }
        } else
            holder = (ViewHolder) v.getTag();

        final Pill pill = _pills.get(position);
        if (pill != null) {
            holder.name.setText(pill.getName());
            holder.size.setText(String.valueOf(pill.getSize()));

            int visibility = pill.isFavourite() ? View.VISIBLE : View.INVISIBLE;
            if (holder.favourite != null)
                holder.favourite.setVisibility(visibility);

            holder.colour.setColour(pill.getColour());

            holder.pill = pill;
        }

        return v;
    }

    @Override
    public int getCount() {
        if (_pills != null)
            return _pills.size();
        return 0;
    }

    public Pill getPillAtPosition(int pos) {
        if (_pills == null || pos > _pills.size() || pos < 0)
            return null;

        return _pills.get(pos);
    }

    public void removeAtPosition(int pos) {
        if (_pills == null || pos > _pills.size() || pos < 0)
            return;

        _pills.remove(pos);
        this.notifyDataSetChanged();
    }

    public void updateAdapter(List<Pill> pills) {
        _pills = pills;
        this.notifyDataSetChanged();
    }
}
