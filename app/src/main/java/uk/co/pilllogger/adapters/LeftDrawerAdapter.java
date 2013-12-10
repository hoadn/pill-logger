package uk.co.pilllogger.adapters;

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

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 23/10/13.
 */
public class LeftDrawerAdapter extends BaseAdapter{

    Activity _activity;
    List<String> _source;

    public LeftDrawerAdapter(Activity activity, List<String> source) {
        _activity = activity;
        _source = source;
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

        LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.left_drawer_list, viewGroup, false);
        if(v != null){
            TextView drawerText = (TextView) v.findViewById(R.id.drawer_item_text);
            ImageView drawerIcon = (ImageView) v.findViewById(R.id.drawer_item_icon);
            drawerText.setText(_source.get(position));
            drawerText.setTypeface(State.getSingleton().getTypeface());

            if(_source.get(position).equals(_activity.getString(R.string.drawer_consumption))){
                drawerIcon.setImageDrawable(_activity.getResources().getDrawable(R.drawable.list));
            }

            if (_source.get(position).equals(_activity.getString(R.string.drawer_pills))){
                drawerIcon.setImageDrawable(_activity.getResources().getDrawable(R.drawable.medkit));
            }

            if(_source.get(position).equals(_activity.getString(R.string.drawer_charts))){
                drawerIcon.setImageDrawable(_activity.getResources().getDrawable(R.drawable.bar_chart));
            }

            if(_source.get(position).equals(_activity.getString(R.string.drawer_settings))){
                drawerIcon.setImageDrawable(_activity.getResources().getDrawable(R.drawable.cogs));
            }
        }

        return v;
    }


}
