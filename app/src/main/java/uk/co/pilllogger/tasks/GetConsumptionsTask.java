package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by alex on 23/10/13.
 */
public class GetConsumptionsTask extends PillLoggerAsyncTask<Void, Void, List<Consumption>>{

    @Inject
    ConsumptionRepository _consumptionRepository;

    @Inject
    PillRepository _pillRepository;

    Context _context;
    ITaskComplete _listener;
    private boolean _shouldGroup;
    private String _group;

    public GetConsumptionsTask(Context context, ITaskComplete listener, boolean shouldGroup){
        this(context, listener, shouldGroup, null);
    }

    public GetConsumptionsTask(Context context, ITaskComplete listener, boolean shouldGroup, String group) {
        super(context);
        _context = context;
        _listener = listener;
        _shouldGroup = shouldGroup;
        _group = group;
    }

    @Override
    protected List<Consumption> doInBackground(Void... voids) {
        List<Consumption> consumptions;
        if(_group == null){
            consumptions = _consumptionRepository.getAll();
        }
        else{
            consumptions = _consumptionRepository.getForGroup(_group);
        }

        for(Consumption c : consumptions){
            c.setPill(_pillRepository.get(c.getPillId()));
        }

        if(_shouldGroup && consumptions.size() > 0) {
            return _consumptionRepository.groupConsumptions(consumptions);
        }

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
