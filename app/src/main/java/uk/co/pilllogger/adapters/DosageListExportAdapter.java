package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 05/06/14.
 */
public class DosageListExportAdapter extends ArrayAdapter<String> {

    private Activity _activity;
    private int _resourceId;
    private List<String> _dosageTypes;
    private Map<String, Float> _dosageMax = new HashMap<String, Float>();

    public DosageListExportAdapter(Activity activity, int resource, List<String> objects, Map<Integer, Integer> consumptions) {
        super(activity, resource, objects);
        _activity = activity;
        _resourceId = resource;
        _dosageTypes = objects;

        for (Integer pillId : consumptions.keySet()) {
            if(PillRepository.getSingleton(activity).isCached()) {
                Pill pill = PillRepository.getSingleton(activity).get(pillId);
                float dosage = pill.getSize() * consumptions.get(pillId);
                if (_dosageMax.get(pill.getUnits()) == null || _dosageMax.get(pill.getUnits()) < dosage)
                    _dosageMax.put(pill.getUnits(), dosage);
            }
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
        holder.minSize.setText("0");

        if(_dosageTypes.size() > position) {
            String type = _dosageTypes.get(position);
            holder.name.setText(type);
            if(_dosageMax.containsKey(type)) {
                holder.maxSize.setText(NumberHelper.getNiceFloatString(_dosageMax.get(type)));
            }
        }

        return v;
    }
}
