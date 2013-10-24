package uk.co.cntwo.pilllogger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.cntwo.pilllogger.R;

/**
 * Created by nick on 23/10/13.
 */
public class LeftDrawerAdapter extends BaseAdapter{

    Activity _activity;
    List<String> _source;
    Typeface _openSans;

    public LeftDrawerAdapter(Activity activity, List<String> source) {
        _activity = activity;
        _source = source;
        _openSans = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
    }


    @Override
    public int getCount() {
        if (_source != null)
            return _source.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (_source != null)
            return _source.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;

        LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.left_drawer_list, viewGroup, false);
        TextView drawerText = (TextView) v.findViewById(R.id.drawer_item_text);
        ImageView drawerIcon = (ImageView) v.findViewById(R.id.drawer_item_icon);
        drawerText.setText(_source.get(position));
        drawerText.setTypeface(_openSans);

        if (_source.get(position).equals(_activity.getResources().getString(R.string.drawer_pills)))
            drawerIcon.setBackgroundColor(_activity.getResources().getColor(R.color.light_blue));


        return v;
    }


}
