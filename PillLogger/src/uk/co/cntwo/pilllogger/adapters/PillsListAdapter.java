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
    private ActionMode _actionMode;

    private Pill _selectedPill;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
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

            holder.pill = pill;
        }

        v.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (_actionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                _actionMode = _activity.startActionMode(actionModeCallback);
                ViewHolder viewHolder = (ViewHolder)view.getTag();
                _selectedPill = viewHolder.pill;
                view.setSelected(true);

                Menu actionModeMenu = _actionMode.getMenu();
                actionModeMenu.findItem(R.id.pill_list_item_menu_favourite).setVisible(!_selectedPill.isFavourite());
                actionModeMenu.findItem(R.id.pill_list_item_menu_unfavourite).setVisible(_selectedPill.isFavourite());

                return true;
            }
        });

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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.pills_list_item_menu, menu);
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
            switch (item.getItemId()) {
                case R.id.pill_list_item_menu_favourite:
                case R.id.pill_list_item_menu_unfavourite:
                    boolean setFavourite = item.getItemId() == R.id.pill_list_item_menu_favourite;
                    if(_selectedPill != null)
                        _selectedPill.setFavourite(setFavourite);

                    notifyDataSetChanged();
                    mode.finish(); // Action picked, so close the CAB
                    return true;


                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            _actionMode = null;
        }
    };
}
