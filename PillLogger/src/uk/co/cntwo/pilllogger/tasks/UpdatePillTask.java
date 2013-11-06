package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 22/10/13.
 */
public class UpdatePillTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "UpdatePillTask";
    Activity _activity;
    Pill _pill;

    public UpdatePillTask(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Logger.d(TAG, "Updating pill");
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
        dbHelper.updatePill(_pill);
        return null;
    }
}
