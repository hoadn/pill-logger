package uk.co.pilllogger.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import uk.co.pilllogger.activities.AddConsumptionActivity;

/**
 * Created by nick on 24/10/13.
 */
public class AddConsumptionClickListener implements View.OnClickListener {

    Activity _activity;

    public AddConsumptionClickListener(Activity activity) {
        _activity = activity;
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(_activity, AddConsumptionActivity.class);
        _activity.startActivity(intent);
    }
}
