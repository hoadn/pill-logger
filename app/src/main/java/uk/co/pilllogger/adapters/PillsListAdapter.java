package uk.co.pilllogger.adapters;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.pilllogger.R;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.DeletePillTask;
import uk.co.pilllogger.tasks.UpdatePillTask;
import uk.co.pilllogger.views.ColourIndicator;

/**
 * Created by nick on 22/10/13.
 */
public class PillsListAdapter extends PillsListBaseAdapter {

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
}