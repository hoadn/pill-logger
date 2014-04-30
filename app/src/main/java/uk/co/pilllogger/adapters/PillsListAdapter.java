package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.analytics.tracking.android.Tracker;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.dialogs.ChangePillInfoDialog;
import uk.co.pilllogger.dialogs.InfoDialog;
import uk.co.pilllogger.dialogs.PillInfoDialog;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.TrackerHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.stats.Statistics;
import uk.co.pilllogger.tasks.DeletePillTask;
import uk.co.pilllogger.tasks.InsertConsumptionTask;
import uk.co.pilllogger.tasks.UpdatePillTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends PillsListBaseAdapter implements PillInfoDialog.PillInfoDialogListener, ChangePillInfoDialog.ChangePillInfoDialogListener {

    private static final String TAG = "PillsListAdapter";
    private Pill _selectedPill;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, pills);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);


        final View listItem = v;
        if (v != null) {
            ViewHolder holder = (ViewHolder) v.getTag();
            final Pill pill = _data.get(position);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pill != null) {
                        InfoDialog dialog = new PillInfoDialog(pill, PillsListAdapter.this);
                        TrackerHelper.showInfoDialogEvent(_activity, TAG);
                        dialog.show(_activity.getFragmentManager(), pill.getName());
                    }
                }
            });
            holder.colour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ViewHolder viewholder = (ViewHolder) listItem.getTag();
                    if (viewholder.open) {
                        viewholder.pickerContainer.setVisibility(View.GONE);
                        ViewGroup colourContainer = (ViewGroup) viewholder.pickerContainer.findViewById(R.id.colour_container);
                        int colourCount = colourContainer.getChildCount();
                        for (int i = 0; i < colourCount; i++) {
                            View colourView = colourContainer.getChildAt(i);
                            if (colourView != null) {
                                colourView.setOnClickListener(null);
                            }
                        }
                    } else {
                        viewholder.pickerContainer.setVisibility(View.VISIBLE);
                        ViewGroup colourContainer = (ViewGroup) viewholder.pickerContainer.findViewById(R.id.colour_container);
                        int colourCount = colourContainer.getChildCount();
                        for (int i = 0; i < colourCount; i++) {
                            View colourView = colourContainer.getChildAt(i);
                            if (colourView != null) {
                                colourView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int colour = ((ColourIndicator) view).getColour();
                                        if (pill != null) {
                                            pill.setColour(colour);

                                            new UpdatePillTask(_activity, pill).execute();

                                            TrackerHelper.updatePillColourEvent(_activity, TAG);

                                            notifyDataSetChanged();
                                            viewholder.pickerContainer.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    viewholder.open = !viewholder.open;
                }
            });
        }

        return v;
    }

    @Override
    public void onDialogAddConsumption(Pill pill, InfoDialog dialog) {
        if (pill != null) {
            Consumption consumption = new Consumption(pill, new Date());
            new InsertConsumptionTask(_activity, consumption).execute();
            TrackerHelper.addConsumptionEvent(_activity, "PillDialog");
            Toast.makeText(_activity, "Added consumption of " + pill.getName(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogDelete(Pill pill, InfoDialog dialog) {
        AlertDialog cancelDialog = createCancelDialog(pill, "DialogDelete");
        cancelDialog.show();
        dialog.dismiss();
    }

    @Override
    public void setDialogFavourite(Pill pill, InfoDialog dialog) {
        if (pill != null) {
            if (pill.isFavourite())
                pill.setFavourite(false);
            else
                pill.setFavourite(true);
            new UpdatePillTask(_activity, pill).execute();
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogChangePillColour(Pill pill, InfoDialog dialog) {
        new UpdatePillTask(_activity, pill).execute();
        dialog.dismiss();
    }

    @Override
    public void onDialogChangeNameDosage(Pill pill, InfoDialog dialog) {
        DialogFragment editDialog = new ChangePillInfoDialog(_activity, pill, this);
        if (editDialog != null)
            editDialog.show(_activity.getFragmentManager(), pill.getName());
        dialog.dismiss();
    }

    @Override
    public void onDialogInfomationChanged(Pill pill, ChangePillInfoDialog dialog) {
        new UpdatePillTask(_activity, pill).execute();
    }
}
