package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by alex on 23/10/13.
 */
public class GetConsumptionsTask extends AsyncTask<Void, Void, List<Consumption>>{

    private static final String TAG = "GetConsumptionsTask";
    Context _context;
    ITaskComplete _listener;
    private boolean _shouldGroup;
    private String _group;

    public GetConsumptionsTask(Context context, ITaskComplete listener, boolean shouldGroup){
        this(context, listener, shouldGroup, null);
    }

    public GetConsumptionsTask(Context context, ITaskComplete listener, boolean shouldGroup, String group) {
        _context = context;
        _listener = listener;
        _shouldGroup = shouldGroup;
        _group = group;
    }
    @Override
    protected List<Consumption> doInBackground(Void... voids) {
        ConsumptionRepository repository = ConsumptionRepository.getSingleton(_context);

        Logger.d(TAG, "Timing: Going to get Consumptions");
        List<Consumption> consumptions;
        if(_group == null){
            consumptions = repository.getAll();
        }
        else{
            consumptions = repository.getForGroup(_group);
        }

        Logger.d(TAG, "Timing: Going to group Consumptions");
        if(_shouldGroup && consumptions.size() > 0) {
            return repository.groupConsumptions(consumptions);
        }


        return consumptions;
    }

    @Override
    protected void onPostExecute(List<Consumption> consumptions) {
        Logger.d(TAG, "Timing: Going to call consumptionsReceived");
        _listener.consumptionsReceived(consumptions);
    }

    public interface ITaskComplete{
        public void consumptionsReceived(List<Consumption> consumptions);
    }
}
