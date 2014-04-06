package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.analytics.tracking.android.Tracker;

import java.util.Date;
import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.dialogs.InfoDialog;
import uk.co.pilllogger.dialogs.PillInfoDialog;
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
public class PillsListAdapter extends PillsListBaseAdapter implements PillInfoDialog.PillInfoDialogListener {

    private static final String TAG = "PillsListAdapter";
    private Pill _selectedPill;

    public PillsListAdapter(Activity activity, int textViewResourceId, List<Pill> pills) {
        super(activity, textViewResourceId, R.menu.pills_list_item_menu, pills);
    }

    @Override
    protected boolean actionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pill_list_item_menu_favourite:
            case R.id.pill_list_item_menu_unfavourite:
                boolean setFavourite = item.getItemId() == R.id.pill_list_item_menu_favourite;
                if (_selectedPill != null)
                    _selectedPill.setFavourite(setFavourite);

                new UpdatePillTask(_activity, _selectedPill).execute();

                notifyDataSetChanged();
                mode.finish(); // Action picked, so close the CAB
                return true;

            case R.id.pill_list_item_menu_delete:
                int index = _data.indexOf(_selectedPill);
                removeAtPosition(index); //remove() doesn't like newly created pills, so remove manually

                new DeletePillTask(_activity, _selectedPill).execute();
                TrackerHelper.deletePillEvent(_activity, "ActionBar");
                notifyDataSetChanged();
                mode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected boolean onClickListenerSet(View view, Menu menu) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        _selectedPill = viewHolder.pill;
        menu.findItem(R.id.pill_list_item_menu_favourite).setVisible(!_selectedPill.isFavourite());
        menu.findItem(R.id.pill_list_item_menu_unfavourite).setVisible(_selectedPill.isFavourite());

        return true;
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
        if (pill != null) {
            new DeletePillTask(_activity, pill).execute();
            TrackerHelper.deletePillEvent(_activity, "DialogDelete");
            notifyDataSetChanged();
        }
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
}
