package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.ExportActivity;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.services.IExportService;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 01/06/2014
 * in uk.co.pilllogger.adapters.
 */
public class PillsListExportAdapter extends PillsListBaseAdapter {

    private final IExportService _exportService;
    Set<Pill> _selectedPills = new HashSet<Pill>();
    Set<Pill> _previouslySelected;
    ExportActivity _activity;

    public Set<Pill> getSelectedPills(){ return _selectedPills; }

    public PillsListExportAdapter(Activity activity, int textViewResourceId, List<Pill> pills, IExportService exportService, ConsumptionRepository consumptionRepository) {
        super(activity, textViewResourceId, pills, consumptionRepository);
        _exportService = exportService;
        _activity = (ExportActivity) activity;
        _previouslySelected = _exportService.getExportSettings().getSelectedPills();
    }

    @Override
    protected ActionBarArrayAdapter.ViewHolder initViewHolder(View v) {
        final PillsListBaseAdapter.ViewHolder holder = (ViewHolder) super.initViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selected = !holder.selected;
                if(holder.selected){
                    _exportService.getExportSettings().getSelectedPills().add(holder.pill);
                }
                else{
                    _exportService.getExportSettings().getSelectedPills().remove(holder.pill);
                }

                _exportService.getPillSummary(_exportService.getSummaryTextView());

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
        holder.selected = (_exportService.getExportSettings().getSelectedPills().contains(holder.pill));

        int backgroundColour = (holder.selected) ? _activity.getResources().getColor(R.color.highlight_blue) : _activity.getResources().getColor(R.color.transparent);
        holder.container.setBackgroundColor(backgroundColour);

        holder.name.setTypeface(State.getSingleton().getRobotoTypeface());
        holder.lastTaken.setTypeface(State.getSingleton().getRobotoTypeface());
        holder.size.setTypeface(State.getSingleton().getRobotoTypeface());

        return v;
    }

    @Override
    public void destroy() {

    }
}
