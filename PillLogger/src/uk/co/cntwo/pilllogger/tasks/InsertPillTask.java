package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Pill;

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
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
        _pillId = dbHelper.insertPill(_pill);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (_listener != null) {
            DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
            Pill pill =  dbHelper.getPill((int)_pillId);
            _listener.pillInserted(pill);
        }

    }

    public interface ITaskComplete{
        public void pillInserted(Pill pill);
    }
}
