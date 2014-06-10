package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 05/06/14.
 */
public class DosageListExportAdapter extends ArrayAdapter<String> {

    private Activity _activity;
    private int _resourceId;
    private List<String> _dosageTypes;
    private Map<String, Float> _dosageMax = new HashMap<String, Float>();

    public DosageListExportAdapter(Activity activity, int resource, List<String> objects, List<Consumption> consumptions) {
        super(activity, resource, objects);
        _activity = activity;
        _resourceId = resource;
        _dosageTypes = objects;
        for (Consumption consumption : consumptions) {
            Pill pill = consumption.getPill();
            float dosage = pill.getSize() * consumption.getQuantity();
            if (_dosageMax.get(pill.getUnits()) != null && _dosageMax.get(pill.getUnits()) < dosage)
                _dosageMax.put(pill.getUnits(), dosage);
        }

    }

    public static class ViewHolder {
        public TextView name;
        public TextView maxSize;
        public TextView minSize;
        public TextView maxSizeText;
        public TextView minSizeText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(_resourceId, null);
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView)v.findViewById(R.id.export_dosage_name);
            holder.maxSize = (TextView)v.findViewById(R.id.export_dosage_max);
            holder.minSize = (TextView)v.findViewById(R.id.export_dosage_min);
            holder.minSizeText = (TextView) v.findViewById(R.id.export_dosage_min_text);
            holder.maxSizeText = (TextView) v.findViewById(R.id.export_dosage_max_text);
            holder.name.setTypeface(State.getSingleton().getRobotoTypeface());
            holder.maxSize.setTypeface(State.getSingleton().getRobotoTypeface());
            holder.minSize.setTypeface(State.getSingleton().getRobotoTypeface());
            holder.maxSizeText.setTypeface(State.getSingleton().getRobotoTypeface());
            holder.minSizeText.setTypeface(State.getSingleton().getRobotoTypeface());
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder)v.getTag();
        holder.name.setText(_dosageTypes.get(position));
        holder.minSize.setText("0");
        holder.maxSize.setText(_dosageMax.get(_dosageTypes.get(position)).toString());

        return v;
    }
}
