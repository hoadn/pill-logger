package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.interfaces.PillsReceivedListener;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by alex on 23/10/13.
 */
public class GetConsumptionsTask extends AsyncTask<Void, Void, List<Consumption>>{

    Context _context;
    ITaskComplete _listener;

    public GetConsumptionsTask(Context context, ITaskComplete listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected List<Consumption> doInBackground(Void... voids) {
        DatabaseHelper dBHelper = DatabaseHelper.getSingleton(_context);
        return dBHelper.getAllConsumptions();
    }

    @Override
    protected void onPostExecute(List<Consumption> consumptions) {
        _listener.consumptionsReceived(consumptions);
    }

    public interface ITaskComplete{
        public void consumptionsReceived(List<Consumption> consumptions);
    }
}
