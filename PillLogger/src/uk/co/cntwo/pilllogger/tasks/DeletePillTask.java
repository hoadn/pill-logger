package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 28/10/13.
 */
public class DeletePillTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Pill _pill;

    public DeletePillTask(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
        dbHelper.deletePill(_pill);
        return null;
    }
}