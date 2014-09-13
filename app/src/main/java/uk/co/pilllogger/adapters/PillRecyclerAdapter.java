package uk.co.pilllogger.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 11/09/2014
 * in uk.co.pilllogger.adapters.
 */
public class PillRecyclerAdapter extends RecyclerView.Adapter<PillRecyclerAdapter.ViewHolder> {
    private static final int NEW = 0;
    private static final int EXISTING = 1;

    private final List<Pill> _pills;

    Context _context;
    private final ConsumptionRepository _consumptionRepository;

    public PillRecyclerAdapter(List<Pill> pills, Context context, ConsumptionRepository consumptionRepository){
        _pills = pills;
        _context = context;
        _consumptionRepository = consumptionRepository;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pill_list_item, null);

        // create ViewHolder
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == EXISTING) {
            final Pill pill = _pills.get(position);
            if (pill == null) {
                return;
            }

            viewHolder.name.setText(pill.getName());

            Consumption latest = pill.getLatestConsumption(_consumptionRepository);
            if (latest != null) {
                String prefix = _context.getString(R.string.last_taken_message_prefix);
                String lastTaken = DateHelper.getRelativeDateTime(_context, latest.getDate(), true);
                viewHolder.lastTaken.setText(lastTaken);
            } else {
                viewHolder.lastTaken.setText(_context.getString(R.string.no_consumptions_message));
            }

            if (pill.getSize() <= 0) {
                viewHolder.size.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()) + pill.getUnits());
                viewHolder.size.setVisibility(View.VISIBLE);
            }

            viewHolder.colour.setColour(pill.getColour());

            viewHolder.pill = pill;

            viewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDialog(pill.getId());
                }
            });
        }
        else{
            viewHolder.name.setText("Create new...");
            viewHolder.colour.setColour(Color.TRANSPARENT);
            viewHolder.size.setVisibility(View.GONE);
        }
    }

    private void startDialog(int pillId) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Pill.ordinal());
        intent.putExtra("PillId", pillId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (_pills != null) {
            count = _pills.size();
        }

        return count + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == _pills.size() ? NEW : EXISTING;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public Pill pill;
        public boolean open;
        public boolean selected;
        public ViewGroup container;
        @InjectView(R.id.pill_list_name) public TextView name;
        @InjectView(R.id.pill_list_last_taken) public TextView lastTaken;
        @InjectView(R.id.pill_list_colour) public ColourIndicator colour;
        @InjectView(R.id.pill_list_size) public TextView size;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }
    }
}
