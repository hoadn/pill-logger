package uk.co.cntwo.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.models.Consumption;

/**
 * Created by nick on 22/10/13.
 */
public class ConsumptionListAdapter extends ArrayAdapter<Consumption> {

    private List<Consumption> _consumptions;
    private Activity _activity;
    private Typeface _openSans;

    public ConsumptionListAdapter(Activity activity, int textViewResourceId, List<Consumption> consumptions) {
        super(activity, textViewResourceId, consumptions);
        this._consumptions = consumptions;
        this._activity = activity;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    public static class ViewHolder {
        public TextView name;
        public TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.consumption_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.consumption_list_name);
            holder.date = (TextView) v.findViewById(R.id.consumption_list_date);
            holder.name.setTypeface(_openSans);
            holder.date.setTypeface(_openSans);
            v.setTag(holder);
        }
        else
            holder = (ViewHolder) v.getTag();

        Consumption consumption = _consumptions.get(position);
        if (consumption != null) {
            holder.name.setText(consumption.get_pill().getName());
            holder.date.setText(consumption.get_date());
        }
        return v;
    }
}
