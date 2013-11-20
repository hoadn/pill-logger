package uk.co.pilllogger.listeners;

import android.app.Activity;
import android.view.View;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.DeletePillTask;

/**
 * Created by nick on 28/10/13.
 */
public class DeletePillClickListener implements View.OnClickListener{

    Pill _pill;
    Activity _activity;

    public DeletePillClickListener(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    @Override
    public void onClick(View view) {
        new DeletePillTask(_activity, _pill).execute();
    }
}
