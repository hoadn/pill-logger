package uk.co.pilllogger.listeners;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.adapters.AddConsumptionPillListAdapter;
import uk.co.pilllogger.animations.AddPillToConsumptionAnimation;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 25/10/13.
 */
public class AddConsumptionPillItemClickListener implements ListView.OnItemClickListener {

    private boolean _allowMultiple = true;
    Context _context;
    AddConsumptionPillListAdapter _adapter;

    public AddConsumptionPillItemClickListener(Context context, AddConsumptionPillListAdapter adapter) {
        _context = context;
        _adapter = adapter;
    }

    public AddConsumptionPillItemClickListener(Context context, AddConsumptionPillListAdapter adapter, boolean allowMultiple){
        this(context, adapter);

        _allowMultiple = allowMultiple;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Timber.v("testingWidth, this is being called");
        LinearLayout addButtonLayout = (LinearLayout)view.findViewById(R.id.add_consumption_after_click_layout);
        AddPillToConsumptionAnimation animation;
        int width = addButtonLayout.getLayoutParams().width;
        int color;
        Pill pill = _adapter.getItem(i);

        if(!_allowMultiple){
            _adapter.clearOpenPillsList();
            _adapter.notifyDataSetChanged();
        }

        if (width > 0) {
            animation = new AddPillToConsumptionAnimation(addButtonLayout, width, false, _context);
            color = android.R.color.transparent;
            _adapter.removeAllInstancesOfPill(i);
            _adapter.removeOpenPill(pill);
        }
        else {
            animation = new AddPillToConsumptionAnimation(addButtonLayout, (int) LayoutHelper.dpToPx(_context, 125), true, _context);
            color = State.getSingleton().getTheme().getSelectedBackgroundColourResourceId();
            _adapter.addConsumedPillAtStart(i);
            TextView amount = (TextView) view.findViewById(R.id.add_consumption_amount);
            amount.setText("1");
            _adapter.addOpenPill(pill);
        }

        view.setBackgroundColor(_context.getResources().getColor(color));
        addButtonLayout.setBackgroundColor(_context.getResources().getColor(State.getSingleton().getTheme().getSelectedBackgroundColourResourceId()));
        View rightLayout = view.findViewById(R.id.add_consumption_right_info);
        rightLayout.setBackgroundColor(_context.getResources().getColor(color));
        animation.setDuration(150);
        addButtonLayout.startAnimation(animation);
    }
}
