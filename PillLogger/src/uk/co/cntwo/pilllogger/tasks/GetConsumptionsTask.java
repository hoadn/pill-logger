package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.repositories.ConsumptionRepository;

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
        return ConsumptionRepository.getSingleton(_context).getAll();
    }

    @Override
    protected void onPostExecute(List<Consumption> consumptions) {
        _listener.consumptionsReceived(consumptions);
    }

    public interface ITaskComplete{
        public void consumptionsReceived(List<Consumption> consumptions);
    }
}
