package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import uk.co.pilllogger.R;

/**
 * Created by nick on 30/11/13.
 */
public class UnitAdapter extends ArrayAdapter<CharSequence> {

    private Typeface _openSans;
    private Activity _activity;
    private String[] _units = { "mg", "ml" };

    public UnitAdapter(Activity activity, int textViewResourceId, String[] units) {
        super(activity, textViewResourceId, units);
        _activity = activity;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView view = (TextView) v.findViewById(android.R.id.text1);
        view.setTypeface(_openSans);
        view.setTextSize(22);
        view.setTextColor(_activity.getResources().getColor(R.color.text_grey_medium));

        return v;
    }
}
