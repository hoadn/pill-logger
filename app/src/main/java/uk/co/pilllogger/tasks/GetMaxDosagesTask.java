package uk.co.pilllogger.tasks;

/**
 * Created by nick on 10/06/14.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.util.Map;

import javax.inject.Inject;

import uk.co.pilllogger.repositories.ConsumptionRepository;


/**
 * Created by alex on 23/10/13.
 */
public class GetMaxDosagesTask extends AsyncTask<Void, Void, Map<Integer, Integer>> {

    @Inject
    ConsumptionRepository _consumptionRepository;

    Context _context;
    ITaskComplete _listener;

    public GetMaxDosagesTask(Context context, ITaskComplete listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected Map<Integer, Integer> doInBackground(Void... voids) {
        return _consumptionRepository.getMaxDosages();
    }

    @Override
    protected void onPostExecute(Map<Integer, Integer> pillConsumptionMaxQuantityMap) {
        _listener.maxConsumptionsReceived(pillConsumptionMaxQuantityMap);
    }

    public interface ITaskComplete{
        public void maxConsumptionsReceived(Map<Integer, Integer> pillConsumptionMaxQuantityMap);
    }
}
