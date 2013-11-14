package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.repositories.PillRepository;

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
        PillRepository.getSingleton(_activity).delete(_pill);
        return null;
    }
}
