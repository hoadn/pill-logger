package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 12/11/2013.
 */
public abstract class ActionBarArrayAdapter<T> extends ArrayAdapter<T>{
    protected Context _context;
    protected int _resourceId;
    protected List<T> _data;
    private List<Integer> _selectedItems;

    public ActionBarArrayAdapter(Context context, int resourceId, List<T> objects) {
        super(context, resourceId, objects);
        _context = context;
        _resourceId = resourceId;
        _data = objects;
        _selectedItems = new ArrayList<Integer>();
    }

    protected abstract ViewHolder initViewHolder(View v);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        View selector = v;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);

            ViewHolder viewHolder = initViewHolder(v);

            viewHolder.shadow = v.findViewById(R.id.shadow);
        }
        if (v != null) {
            if (!v.getTag().equals("selector"))
                selector = v.findViewById(R.id.selector_container);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        selector.setBackgroundResource(State.getSingleton().getTheme().getListItemBackgroundResourceId());

        //if(holder != null && holder.shadow != null)
            //holder.shadow.setVisibility(State.getSingleton().getTheme().isListItemShadowed() ? View.VISIBLE : View.GONE);

        return v;
    }

    public void removeAtPosition(int pos) {
        if (_data == null || pos > _data.size() || pos < 0)
            return;

        _data.remove(pos);
        this.notifyDataSetChanged();
    }

    public abstract void destroy();

    public static class ViewHolder{

        public View shadow;
    }
}
