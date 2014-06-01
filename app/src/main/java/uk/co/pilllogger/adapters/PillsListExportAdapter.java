package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.adapters.
 */
public class PillsListExportAdapter extends PillsListBaseAdapter {

    Set<Pill> _selectedPills = new HashSet<Pill>();

    public PillsListExportAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
    }

    @Override
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        final PillsListBaseAdapter.ViewHolder holder = (ViewHolder) super.initViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selected = !holder.selected;
                if(holder.selected){
                    _selectedPills.remove(holder.pill);
                }
                else{
                    _selectedPills.add(holder.pill);
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
        if(holder.selected) {
            holder.container.setBackgroundColor(_activity.getResources().getColor(State.getSingleton().getTheme().getSelectedBackgroundColourResourceId()));
        }

        return v;
    }
}
