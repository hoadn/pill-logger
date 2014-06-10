package uk.co.pilllogger.tasks;

/**
 * Created by nick on 10/06/14.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;


/**
 * Created by alex on 23/10/13.
 */
public class GetMaxDosagesTask extends AsyncTask<Void, Void, List<Consumption>> {

    Context _context;
    ITaskComplete _listener;
    List<Pill> _pills;


    public GetMaxDosagesTask(Context context, ITaskComplete listener, List<Pill> pills) {
        _context = context;
        _listener = listener;
        _pills = pills;
    }
    @Override
    protected List<Consumption> doInBackground(Void... voids) {
        ConsumptionRepository repository = ConsumptionRepository.getSingleton(_context);
        List<Consumption> maxDosageConsumptions = new ArrayList<Consumption>();
        for (Pill pill: _pills) {
            maxDosageConsumptions.add(repository.getMaxDosageForPill(pill));
        }
        return maxDosageConsumptions;
    }

    @Override
    protected void onPostExecute(List<Consumption> consumptions) {
        _listener.maxConsumptionsReceived(consumptions);
    }

    public interface ITaskComplete{
        public void maxConsumptionsReceived(List<Consumption> consumptions);
    }
}
