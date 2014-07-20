package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import timber.log.Timber;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by nick on 22/10/13.
 */
public class UpdatePillTask extends AsyncTask<Void, Void, Void> {
    Activity _activity;
    Pill _pill;

    public UpdatePillTask(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Timber.d("Updating pill");
        PillRepository.getSingleton(_activity).update(_pill);
        return null;
    }
}
