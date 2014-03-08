package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 30/11/13.
 */
public class UnitAdapter extends ArrayAdapter<CharSequence> {

    private Activity _activity;

    public UnitAdapter(Activity activity, int textViewResourceId, String[] units) {
        super(activity, textViewResourceId, units);
        _activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView view = (TextView) v.findViewById(android.R.id.text1);
        view.setTypeface(State.getSingleton().getTypeface());
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        view.setTextColor(_activity.getResources().getColor(R.color.text_grey_light));

        return v;
    }
}
