package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.activities.DialogActivity;
import uk.co.pilllogger.dialogs.ChangePillInfoDialog;
import uk.co.pilllogger.fragments.InfoDialogFragment;
import uk.co.pilllogger.fragments.PillInfoDialogFragment;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;
import uk.co.pilllogger.tasks.DeletePillTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.UpdatePillTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends PillsListBaseAdapter implements
        PillInfoDialogFragment.PillInfoDialogListener,
        ChangePillInfoDialog.ChangePillInfoDialogListener,
        Observer.IConsumptionAdded,
        Observer.IConsumptionDeleted {

    private static final String TAG = "PillsListAdapter";
    private final Activity _activity;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
        _activity = activity;
        Observer.getSingleton().registerConsumptionAddedObserver(this);
        Observer.getSingleton().registerConsumptionDeletedObserver(this);
        Observer.getSingleton().registerPillDialogObserver(this);
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

    @Override
    public void onDialogAddConsumption(Pill pill, InfoDialogFragment dialog) {
        if (pill != null) {
            Consumption consumption = new Consumption(pill, new Date());
            new InsertConsumptionTask(_activity, consumption).execute();
            TrackerHelper.addConsumptionEvent(_activity, "PillDialog");
            Toast.makeText(_activity, "Added consumption of " + pill.getName(), Toast.LENGTH_SHORT).show();
        }
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogDelete(Pill pill, InfoDialogFragment dialog) {
        AlertDialog cancelDialog = createCancelDialog(pill, "DialogDelete");
        cancelDialog.show();
        dialog.getActivity().finish();
    }

    @Override
    public void setDialogFavourite(Pill pill, InfoDialogFragment dialog) {
        if (pill != null) {
            if (pill.isFavourite())
                pill.setFavourite(false);
            else
                pill.setFavourite(true);
            new UpdatePillTask(_activity, pill).execute();
        }
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogChangePillColour(Pill pill, InfoDialogFragment dialog) {
        new UpdatePillTask(_activity, pill).execute();
        dialog.getActivity().finish();
    }

    @Override
    public void onDialogChangeNameDosage(Pill pill, InfoDialogFragment dialog) {
        DialogFragment editDialog = new ChangePillInfoDialog(_activity, pill, this);
        dialog.getActivity().finish();
        if (editDialog != null) {
            editDialog.show(_activity.getFragmentManager(), pill.getName());
        }
    }

    @Override
    public void onDialogInfomationChanged(Pill pill) {
        new UpdatePillTask(_activity, pill).execute();
    }

    @Override
    public void consumptionAdded(Consumption consumption) {
        notifyDataSetChangedOnUiThread();
    }

    @Override
    public void consumptionDeleted(Consumption consumption) {
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

    @Override
    public void consumptionPillGroupDeleted(String group, int pillId) {
        notifyDataSetChangedOnUiThread();
    }
}
