package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.repositories.PillRepository;

/**
 * Created by nick on 22/10/13.
 */
public class InsertPillTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Pill _pill;
    long _pillId;
    ITaskComplete _listener;

    public InsertPillTask(Activity activity, Pill pill) {
        _activity = activity;
        _pill = pill;
    }

    public InsertPillTask(Activity activity, Pill pill, ITaskComplete listener) {
        this(activity, pill);
        _listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        _pillId = PillRepository.getSingleton(_activity).insert(_pill);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (_listener != null) {
            Pill pill = PillRepository.getSingleton(_activity).get((int)_pillId);
            _listener.pillInserted(pill);
        }

    }

    public interface ITaskComplete{
        public void pillInserted(Pill pill);
    }
}
