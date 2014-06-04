package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.adapters.
 */
public class PillsListExportAdapter extends PillsListBaseAdapter {

    Set<Pill> _selectedPills = new HashSet<Pill>();
    Set<Pill> _previouslySelected = new HashSet<Pill>();
    ExportActivity _activity;

    public Set<Pill> getSelectedPills(){ return _selectedPills; }

    public PillsListExportAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = (ExportActivity) activity;
        _previouslySelected = _activity.getSelectedPills();
    }

    @Override
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        final PillsListBaseAdapter.ViewHolder holder = (ViewHolder) super.initViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selected = !holder.selected;
                if(holder.selected){
                    _selectedPills.add(holder.pill);
                }
                else{
                    _selectedPills.remove(holder.pill);
                }

                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        PillsListBaseAdapter.ViewHolder holder = (PillsListBaseAdapter.ViewHolder)v.getTag();
        holder.shadow.setVisibility(View.GONE);
        if (_previouslySelected != null) {
            _selectedPills = _previouslySelected;
            if (_previouslySelected.contains(holder.pill))
                holder.selected = true;
        }
        if(holder.selected) {
            holder.container.setBackgroundColor(_activity.getResources().getColor(R.color.highlight_blue));
        }
        holder.name.setTypeface(State.getSingleton().getRobotoTypeface());
        holder.lastTaken.setTypeface(State.getSingleton().getRobotoTypeface());
        holder.size.setTypeface(State.getSingleton().getRobotoTypeface());

        return v;
    }
}
