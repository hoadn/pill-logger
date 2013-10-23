package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.interfaces.PillsReceivedListener;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by nick on 21/10/13.
 */
public class GetPillsTask extends AsyncTask<Void, Void, List<Pill>>{

    Context _context;
    PillsReceivedListener _listener;

    public GetPillsTask(Context context, PillsReceivedListener listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        List<Pill> pills;
        DatabaseHelper dBHelper = DatabaseHelper.getSingleton(_context);
        pills = dBHelper.getAllPills();
        return pills;
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        _listener.pillsReceived(pills);
    }
}
