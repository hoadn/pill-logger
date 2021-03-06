package uk.co.pilllogger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 27/11/13.
 */
public class GraphPillListAdapter extends ArrayAdapter<Pill> {

    private List<Pill> _pills;
    private Context _context;
    private int _resourceId;
    private List<Integer> _graphPills;
    private static String TAG = "GraphPillsListAdapter";

    public GraphPillListAdapter(Context context, int textViewResourceId, List<Pill> pills) {
        super(context, textViewResourceId, pills);
        _context = context;
        _pills = pills;
        _resourceId = textViewResourceId;
        _graphPills = State.getSingleton().getGraphExcludePills();
    }

    public static class ViewHolder {
        public TextView name;
        public TextView size;
        public TextView units;
        public View color;
        public CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);
            if(v != null){
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.pill_list_name);
                holder.size = (TextView) v.findViewById(R.id.pill_list_size);
                holder.units = (TextView) v.findViewById(R.id.pill_list_units);
                holder.color = v.findViewById(R.id.graph_list_pill_colour);
                holder.checkbox = (CheckBox) v.findViewById(R.id.graph_list_check_box);
                holder.name.setTypeface(State.getSingleton().getTypeface());
                holder.size.setTypeface(State.getSingleton().getTypeface());
                holder.units.setTypeface(State.getSingleton().getTypeface());
                v.setTag(holder);
            }
        }
        else
            holder = (ViewHolder) v.getTag();

        Pill pill = _pills.get(position);
        if (pill != null && holder != null) {
            holder.name.setText(pill.getName());
            holder.size.setText(pill.getFormattedSize());
            holder.units.setText(pill.getUnits());
            holder.color.setBackgroundColor(pill.getColour());

            boolean included = !State.getSingleton().isPillExcluded(pill);
            holder.checkbox.setChecked(included);

        }
        else {
            Timber.v("pill or holder is null");
        }
        return v;
    }

    @Override
    public int getCount() {
        if (_pills != null)
            return _pills.size();
        return 0;
    }
}
