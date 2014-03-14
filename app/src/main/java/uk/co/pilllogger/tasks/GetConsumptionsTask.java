package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by alex on 23/10/13.
 */
public class GetConsumptionsTask extends AsyncTask<Void, Void, List<Consumption>>{

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

        List<Consumption> consumptions;
        if(_group == null){
            consumptions = repository.getAll();
        }
        else{
            consumptions = repository.getForGroup(_group);
        }
        if(_shouldGroup && consumptions.size() > 0)
            return repository.groupConsumptions(consumptions);

        return consumptions;
    }

    @Override
    protected void onPostExecute(List<Consumption> consumptions) {
        _listener.consumptionsReceived(consumptions);
    }

    public interface ITaskComplete{
        public void consumptionsReceived(List<Consumption> consumptions);
    }
}
