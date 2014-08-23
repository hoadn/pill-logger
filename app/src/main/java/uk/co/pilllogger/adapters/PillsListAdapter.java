package uk.co.pilllogger.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.events.CreateConsumptionEvent;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.events.DeletePillEvent;
import uk.co.pilllogger.events.DeletedConsumptionEvent;
import uk.co.pilllogger.events.DeletedConsumptionGroupEvent;
import uk.co.pilllogger.events.UpdatePillEvent;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.jobs.InsertConsumptionJob;
import uk.co.pilllogger.jobs.UpdatePillJob;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.tasks.DeletePillTask;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends PillsListBaseAdapter {

    private static final String TAG = "PillsListAdapter";
    JobManager _jobManager;

    @DebugLog
    public PillsListAdapter(Context context, JobManager jobManager, int textViewResourceId, List<Pill> pills, ConsumptionRepository consumptionRepository) {
        super(context, textViewResourceId, pills, consumptionRepository);
        _jobManager = jobManager;
    }

    private AlertDialog createCancelDialog(Pill pill, String deleteTrackerType) {
        final Pill finalPill = pill;
        final String deleteTrackerType1 = deleteTrackerType;
        if (finalPill != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setTitle(_context.getString(R.string.confirm_delete_title));
            builder.setMessage(_context.getString(R.string.confirm_delete_message));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DeletePillTask(finalPill).execute();
                    TrackerHelper.deletePillEvent(_context, deleteTrackerType1);
                    notifyDataSetChanged();
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

    private void startDialog(int pillId) {
        Intent intent = new Intent(_context, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Pill.ordinal());
        intent.putExtra("PillId", pillId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    @Override @DebugLog
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (v != null) {
            final Pill pill = _data.get(position);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pill != null) {
                        TrackerHelper.showInfoDialogEvent(_context, TAG);
                        startDialog(pill.getId());
                    }
                }
            });
        }

        return v;
    }

    @Subscribe @DebugLog
    public void onDialogAddConsumption(CreateConsumptionEvent event) {
        if (event.getPill() != null) {
            Consumption consumption = new Consumption(event.getPill(), new Date());
            _jobManager.addJobInBackground(new InsertConsumptionJob(consumption));
            TrackerHelper.addConsumptionEvent(_context, "PillDialog");
            Toast.makeText(_context, "Added consumption of " + event.getPill().getName(), Toast.LENGTH_SHORT).show();
        }
        event.getPillInfoDialogFragment().getActivity().finish();
    }

    @Subscribe
    public void onDialogDelete(DeletePillEvent event) {
        AlertDialog cancelDialog = createCancelDialog(event.getPill(), "DialogDelete");
        try {
            cancelDialog.show();
        }
        catch(WindowManager.BadTokenException ex){
            Timber.e("Error showing dialog", ex);
        }
        event.getPillInfoDialogFragment().getActivity().finish();
    }

    @Subscribe
    public void onDialogInfomationChanged(UpdatePillEvent event) {
        _jobManager.addJobInBackground(new UpdatePillJob(event.getPill()));
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event) {
        sortThenUpdate();
    }

    @Subscribe
    public void consumptionDeleted(DeletedConsumptionEvent event) {
        sortThenUpdate();
    }

    private void sortThenUpdate() {
        Collections.sort(_data, new Comparator<Pill>() {
            @Override
            public int compare(Pill lhs, Pill rhs) {
                if (lhs == rhs)
                    return 0;

                if (lhs == null)
                    return -1;

                if (rhs == null)
                    return 1;

                if (rhs.getLatestConsumption(_consumptionRepository) == null && lhs.getLatestConsumption(_consumptionRepository) == null)
                    return 0;

                if (lhs.getLatestConsumption(_consumptionRepository) == null)
                    return -1;

                if (rhs.getLatestConsumption(_consumptionRepository) == null)
                    return 1;

                return rhs.getLatestConsumption(_consumptionRepository).getDate().compareTo(lhs.getLatestConsumption(_consumptionRepository).getDate());
            }
        });
        notifyDataSetChanged();
    }

    @Subscribe
    public void consumptionPillGroupDeleted(DeletedConsumptionGroupEvent event) {
        sortThenUpdate();
    }
}
