package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.interfaces.PillsRecievedListener;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 21/10/13.
 */
public class GetPillsTask extends AsyncTask<Void, Void, List<Pill>>{

    Context _context;
    PillsRecievedListener _listener;

    public GetPillsTask(Context context, PillsRecievedListener listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        List<Pill> pills;
        DatabaseHelper dBHelper = new DatabaseHelper(_context);
        pills = dBHelper.getAllPills();
        return pills;
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        _listener.pillsRecieved(pills);
    }
}
