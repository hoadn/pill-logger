package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.events.CreateConsumptionEvent;
import uk.co.pilllogger.events.CreatedPillEvent;
import uk.co.pilllogger.events.DeletePillEvent;
import uk.co.pilllogger.events.UpdatePillEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.DeletePillJob;
import uk.co.pilllogger.jobs.InsertConsumptionsJob;
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
    private final JobManager _jobManager;
    private final Activity _activity;
    private final ConsumptionRepository _consumptionRepository;

    public PillRecyclerAdapter(List<Pill> pills, Context context, JobManager jobManager, Activity activity, ConsumptionRepository consumptionRepository){
        _pills = pills;
        _context = context;
        _jobManager = jobManager;
        _activity = activity;
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

            if (pill.getNotes().size() > 0) {
                viewHolder.noteIcon.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.noteIcon.setVisibility(View.GONE);
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
            viewHolder.noteIcon.setVisibility(View.GONE);
            viewHolder.size.setVisibility(View.GONE);

            viewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNewPillDialog();
                }
            });
        }
    }

    private void startDialog(int pillId) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Pill.ordinal());
        intent.putExtra("PillId", pillId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    private void showNewPillDialog() {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.NewPill.ordinal());
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
        @InjectView(R.id.pill_list_notes_icon) public ImageView noteIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener clickListener){
            itemView.setOnClickListener(clickListener);
        }
    }

    @Subscribe
    public void pillAdded(CreatedPillEvent event){
        _pills.add(0, event.getPill());
        notifyItemRangeInserted(0, 1);
    }

    @Subscribe
    public void dialogCreateConsumption(CreateConsumptionEvent event){
        if(event.getPill() != null){
            Consumption consumption = new Consumption(event.getPill(), new Date());
            _jobManager.addJobInBackground(new InsertConsumptionsJob(consumption));
            TrackerHelper.addConsumptionEvent(_context, "PillDialog");
            Toast.makeText(_context, "Added consumption of " + event.getPill().getName(), Toast.LENGTH_SHORT).show();
        }

    }

    @Subscribe @DebugLog
    public void dialogDeletePill(DeletePillEvent event) {
        AlertDialog cancelDialog = createCancelDialog(event.getPill(), "DialogDelete");
        try {
            cancelDialog.show();
        } catch (WindowManager.BadTokenException ex) {
            Timber.e(ex, "Error showing dialog");
        }
    }

    @Subscribe @DebugLog
    public void updatedPillEvent(UpdatedPillEvent event){
        int indexOf = -1;

        int i = 0;
        for(Pill p : _pills){
            if(p.getId() == event.getPill().getId()){
                indexOf = i;
                p.updateFromPill(event.getPill());
                break;
            }
            i++;
        }
        if(indexOf < 0) {
            return;
        }

        notifyItemRangeChanged(indexOf, 1);
    }

    private AlertDialog createCancelDialog(Pill pill, String deleteTrackerType) {
        final Pill finalPill = pill;
        final String deleteTrackerType1 = deleteTrackerType;
        if (finalPill != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
            builder.setTitle(_context.getString(R.string.confirm_delete_title));
            builder.setMessage(_context.getString(R.string.confirm_delete_message));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _jobManager.addJobInBackground(new DeletePillJob(finalPill));
                    TrackerHelper.deletePillEvent(_context, deleteTrackerType1);

                    int indexOf = _pills.indexOf(finalPill);
                    _pills.remove(finalPill);
                    notifyItemRangeRemoved(indexOf, 1);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }
        return null;
    }

}
