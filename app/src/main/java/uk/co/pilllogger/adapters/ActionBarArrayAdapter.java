package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 12/11/2013.
 */
public abstract class ActionBarArrayAdapter<T> extends ArrayAdapter<T>{
    protected Activity _activity;
    protected int _resourceId;
    protected List<T> _data;
    private List<Integer> _selectedItems;

    public ActionBarArrayAdapter(Activity activity, int resourceId, List<T> objects) {
        super(activity, resourceId, objects);
        _activity = activity;
        _resourceId = resourceId;
        _data = objects;
        _selectedItems = new ArrayList<Integer>();
    }

    protected abstract void initViewHolder(View v);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        View selector = v;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);

            initViewHolder(v);
        }
        if (v != null) {
            if (!v.getTag().equals("selector"))
                selector = v.findViewById(R.id.selector_container);
        }
        if (_selectedItems.contains(position)) {
            selector.setBackgroundColor(_activity.getResources().getColor(R.color.selector_background_pressed));
        }
        else
            selector.setBackgroundDrawable(_activity.getResources().getDrawable(State.getSingleton().getTheme().getConsumptionListItemBackgroundResourceId()));

        return v;
    }

    public void removeAtPosition(int pos) {
        if (_data == null || pos > _data.size() || pos < 0)
            return;

        _data.remove(pos);
        this.notifyDataSetChanged();
    }
}
