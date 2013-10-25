package uk.co.cntwo.pilllogger.listeners;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import uk.co.cntwo.pilllogger.R;
import uk.co.cntwo.pilllogger.animations.AddPillToConsumptionAnimation;
import uk.co.cntwo.pilllogger.helpers.LayoutHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;

/**
 * Created by nick on 25/10/13.
 */
public class AddConsumptionPillItemClickListener implements ListView.OnItemClickListener {

    Activity _activity;

    public AddConsumptionPillItemClickListener(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Logger.v("AddConsumptionPillItemClickListener", "testingWidth, this is being called");
        LinearLayout addButtonLayout = (LinearLayout)view.findViewById(R.id.add_consumption_after_click_layout);
        AddPillToConsumptionAnimation animation;
        int width = addButtonLayout.getLayoutParams().width;
        int color;
        if (width > 0) {
            animation = new AddPillToConsumptionAnimation(addButtonLayout, width, false, _activity);
            color = android.R.color.transparent;
        }
        else {
            animation = new AddPillToConsumptionAnimation(addButtonLayout, (int)LayoutHelper.dpToPx(_activity, 125), true, _activity);
            color = R.color.done_cancel_grey;
            TextView amount = (TextView) view.findViewById(R.id.add_consumption_amount);
            if (amount.getText().equals("0"))
                amount.setText("1");
        }

        view.setBackgroundColor(_activity.getResources().getColor(color));
        animation.setDuration(200);
        addButtonLayout.startAnimation(animation);
    }
}
