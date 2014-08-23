package uk.co.pilllogger.listeners;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.tasks.DeletePillTask;

/**
 * Created by nick on 28/10/13.
 */
public class DeletePillClickListener implements View.OnClickListener{

    Pill _pill;

    public DeletePillClickListener(Pill pill) {
        _pill = pill;
    }

    @Override
    public void onClick(View view) {
        new DeletePillTask(_pill).execute();
    }
}
