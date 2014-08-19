package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import javax.inject.Inject;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by nick on 28/10/13.
 */
public class DeletePillTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Pill _pill;

    @Inject PillRepository _pillRepository;

    public DeletePillTask(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        _pillRepository.delete(_pill);
        return null;
    }
}
