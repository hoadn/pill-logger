package uk.co.pilllogger.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DecreaseConsumptionEvent;
import uk.co.pilllogger.events.DeleteConsumptionEvent;
import uk.co.pilllogger.events.IncreaseConsumptionEvent;
import uk.co.pilllogger.events.TakeConsumptionAgainEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by Alex on 25/08/2014
 * in uk.co.pilllogger.adapters.
 */
public class ConsumptionRecyclerAdapter extends RecyclerView.Adapter<ConsumptionRecyclerAdapter.ViewHolder> {

    private final List<Consumption> _consumptions;

    Context _context;

    public ConsumptionRecyclerAdapter(List<Consumption> consumptions, Context context){
        _consumptions = consumptions;
        _context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.consumption_list_item, null);

        // create ViewHolder

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Consumption consumption = _consumptions.get(position);

        if (consumption == null) {
            return;
        }

        Pill pill = consumption.getPill();
        if(pill != null){
            holder.name.setText(pill.getName());

            if(pill.getSize() == 0) {
                holder.size.setVisibility(View.INVISIBLE);
            }
            else {
                holder.size.setText(NumberHelper.getNiceFloatString(pill.getSize()) + pill.getUnits());
                holder.size.setVisibility(View.VISIBLE);
            }
            holder.colour.setColour(pill.getColour());
        }
        holder.date.setText(DateHelper.getRelativeDateTime(_context, consumption.getDate()));
        holder.quantity.setText(String.valueOf(consumption.getQuantity()));

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog(consumption);
            }
        });
    }

    private void startDialog(Consumption consumption) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Consumption.ordinal());
        intent.putExtra("ConsumptionGroup", consumption.getGroup());
        intent.putExtra("PillId", consumption.getPillId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return _consumptions.size();
    }

    public List<Consumption> getConsumptions() {
        return _consumptions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.consumption_list_name) public TextView name;
        @InjectView(R.id.consumption_list_date) public TextView date;
        @InjectView(R.id.consumption_list_quantity) public TextView quantity;
        @InjectView(R.id.consumption_list_colour) public ColourIndicator colour;
        @InjectView(R.id.consumption_list_size) public TextView size;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }
    }

    @Subscribe
    public void consumptionTakenAgain(TakeConsumptionAgainEvent event){
        Consumption eventConsumption = event.getConsumption();

        _consumptions.add(0, eventConsumption);

        notifyItemRangeInserted(0, 1);
    }

    @Subscribe
    public void consumptionDeleted(DeleteConsumptionEvent event){
        String group = event.getConsumption().getGroup();

        List<Consumption> toRemove = new ArrayList<Consumption>();

        int i = 0;
        int count = 0;
        int indexOf = -1;
        for(Consumption c : _consumptions){
            if(c.getGroup().equals(group)){
                ++count;

                if(indexOf == -1) {
                    indexOf = i;
                }

                toRemove.add(c);
            }

            ++i;
        }
        _consumptions.removeAll(toRemove);
        notifyItemRangeRemoved(indexOf, count);
    }

    @Subscribe
    public void consumptionIncreased(IncreaseConsumptionEvent event){
        int indexOf = _consumptions.indexOf(event.getConsumption());

        _consumptions.get(indexOf).incrementQuantity();

        notifyItemRangeChanged(indexOf, 1);
    }

    @Subscribe
    public void consumptionDecreased(DecreaseConsumptionEvent event){
        int indexOf = _consumptions.indexOf(event.getConsumption());

        _consumptions.get(indexOf).decrementQuantity();

        notifyItemRangeChanged(indexOf, 1);
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event){
        List<Consumption> addedConsumptions = event.getConsumptions();

        if(addedConsumptions.size() == 0){
            return;
        }

        DateTime whenAdded = new DateTime(addedConsumptions.get(0).getDate());

        int i = 0;
        int indexOf = 0;
        for(Consumption c : _consumptions) {
            DateTime consumptionDate = new DateTime(c.getDate());

            if (consumptionDate.isBefore(whenAdded)) {
                indexOf = i;
                break;
            }

            ++i;
        }

        _consumptions.addAll(indexOf, addedConsumptions);
        notifyItemRangeInserted(indexOf, addedConsumptions.size());
    }
}
