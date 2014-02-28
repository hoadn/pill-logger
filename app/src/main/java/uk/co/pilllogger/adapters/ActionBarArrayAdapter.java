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

/**
 * Created by alex on 12/11/2013.
 */
public abstract class ActionBarArrayAdapter<T> extends ArrayAdapter<T>{
    protected Activity _activity;
    protected int _resourceId;
    private int _menuId;
    protected List<T> _data;
    private ActionMode _actionMode;
    private List<Integer> _selectedItems = new ArrayList<Integer>();
    private int _position;
    private View _selector;
    private ListView _list;

    public ActionBarArrayAdapter(Activity activity, int resourceId, int menuId, List<T> objects) {
        super(activity, resourceId, objects);
        _activity = activity;
        _resourceId = resourceId;
        _menuId = menuId;
        _data = objects;
    }

    protected abstract boolean actionItemClicked(ActionMode mode, MenuItem item);

    protected abstract void initViewHolder(View v);

    protected abstract boolean onClickListenerSet(View view, Menu menu);

    protected ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            if (inflater != null) {
                inflater.inflate(_menuId, menu);
            }
            _selectedItems.add(_position);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return actionItemClicked(mode, item);
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            _selectedItems.clear();
            notifyDataSetChanged();
            _list.invalidateViews();
            _actionMode = null;
        }

    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        _list = (ListView) parent;
        final int pos = position;
        _selector = v;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);

            initViewHolder(v);
        }
        if (v != null) {
            if (!v.getTag().equals("selector"))
                _selector = v.findViewById(R.id.selector_container);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                // Called when the user long-clicks on someView
                int position = pos;
                public boolean onLongClick(View view) {
                    _position = position;
                    if (_actionMode != null) {
                        return false;
                    }

                    // Start the CAB using the ActionMode.Callback defined above
                    _actionMode = _activity.startActionMode(actionModeCallback);
                    view.setSelected(true);
                    View selector = view;
                    if (!view.getTag().equals("selector"))
                        selector = view.findViewById(R.id.selector_container);

                    selector.setBackgroundColor(_activity.getResources().getColor(R.color.selector_background_pressed));

                    Menu actionModeMenu = _actionMode.getMenu();

                    return onClickListenerSet(view, actionModeMenu);
                }
            });
        }
        if (_selectedItems.contains(position)) {
            _selector.setBackgroundColor(_activity.getResources().getColor(R.color.selector_background_pressed));
        }
        else
            _selector.setBackgroundDrawable(_activity.getResources().getDrawable(R.drawable.list_selector));

        return v;
    }

    public void removeAtPosition(int pos) {
        if (_data == null || pos > _data.size() || pos < 0)
            return;

        _data.remove(pos);
        this.notifyDataSetChanged();
    }
}
