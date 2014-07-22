package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

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
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.DeletePillTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.UpdatePillTask;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends PillsListBaseAdapter {

    private static final String TAG = "PillsListAdapter";
    private final Activity _activity;

    @DebugLog
    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
    }

    private AlertDialog createCancelDialog(Pill pill, String deleteTrackerType) {
        final Pill pill1 = pill;
        final String deleteTrackerType1 = deleteTrackerType;
        if (pill1 != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
            builder.setTitle(_activity.getString(R.string.confirm_delete_title));
            builder.setMessage(_activity.getString(R.string.confirm_delete_message));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DeletePillTask(_activity, pill1).execute();
                    TrackerHelper.deletePillEvent(_activity, deleteTrackerType1);
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
        Intent intent = new Intent(_activity, DialogActivity.class);
        intent.putExtra("DialogType", DialogActivity.DialogType.Pill.ordinal());
        intent.putExtra("PillId", pillId);
        _activity.startActivity(intent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (v != null) {
            final Pill pill = _data.get(position);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pill != null) {
                        TrackerHelper.showInfoDialogEvent(_activity, TAG);
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
            new InsertConsumptionTask(_activity, consumption).execute();
            TrackerHelper.addConsumptionEvent(_activity, "PillDialog");
            Toast.makeText(_activity, "Added consumption of " + event.getPill().getName(), Toast.LENGTH_SHORT).show();
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
        new UpdatePillTask(_activity, event.getPill()).execute();
    }

    @Subscribe
    public void consumptionAdded(CreatedConsumptionEvent event) {
        notifyDataSetChangedOnUiThread();
    }

    @Subscribe
    public void consumptionDeleted(DeletedConsumptionEvent event) {
        notifyDataSetChangedOnUiThread();
    }

    private void notifyDataSetChangedOnUiThread(){
        if(_activity != null) {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(_data, new Comparator<Pill>(){
                        @Override
                        public int compare(Pill lhs, Pill rhs) {
                            if(lhs == null && rhs == null)
                                return 0;

                            if(lhs == null)
                                return -1;

                            if(rhs == null)
                                return 1;

                            if(rhs.getLatestConsumption(_activity) == null || lhs.getLatestConsumption(_activity) == null)
                                return 0;

                            return rhs.getLatestConsumption(_activity).getDate().compareTo(lhs.getLatestConsumption(_activity).getDate());
                        }
                    });
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe
    public void consumptionPillGroupDeleted(DeletedConsumptionGroupEvent event) {
        notifyDataSetChangedOnUiThread();
    }
}
