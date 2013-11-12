package uk.co.cntwo.pilllogger.adapters;

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

import java.util.List;

import uk.co.cntwo.pilllogger.R;

/**
 * Created by alex on 12/11/2013.
 */
public abstract class ActionBarArrayAdapter<T> extends ArrayAdapter<T>{
    protected Activity _activity;
    protected int _resourceId;
    private int _menuId;
    protected List<T> _data;
    private ActionMode _actionMode;

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
            _actionMode = null;
        }

    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);

            initViewHolder(v);

            if (v != null) {
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    // Called when the user long-clicks on someView
                    public boolean onLongClick(View view) {
                        if (_actionMode != null) {
                            return false;
                        }

                        // Start the CAB using the ActionMode.Callback defined above
                        _actionMode = _activity.startActionMode(actionModeCallback);
                        view.setSelected(true);

                        Menu actionModeMenu = _actionMode.getMenu();

                        return onClickListenerSet(view, actionModeMenu);
                    }
                });
            }
        }

        return v;
    }

    public void removeAtPosition(int pos) {
        if (_data == null || pos > _data.size() || pos < 0)
            return;

        _data.remove(pos);
        this.notifyDataSetChanged();
    }
}
