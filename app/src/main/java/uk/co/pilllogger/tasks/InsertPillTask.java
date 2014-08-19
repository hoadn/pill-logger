package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import javax.inject.Inject;

import uk.co.pilllogger.events.CreatedPillEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 22/10/13.
 */
public class InsertPillTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Pill _pill;
    long _pillId;
    ITaskComplete _listener;
    @Inject PillRepository _pillRepository;

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
        _pillId = _pillRepository.insert(_pill);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Pill pill = _pillRepository.get((int)_pillId);
        if (_listener != null) {
            _listener.pillInserted(pill);
        }

        State.getSingleton().getBus().post(new CreatedPillEvent(pill));
    }

    public interface ITaskComplete{
        public void pillInserted(Pill pill);
    }
}
