package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

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
        PillRepository.getSingleton(_activity).update(_pill);
        return null;
    }
}
