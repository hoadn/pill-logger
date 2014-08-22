package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by nick on 31/10/13.
 */
public class InsertConsumptionTask  extends AsyncTask<Void, Void, Void> {

    Context _context;
    Consumption _consumption;
    ITaskComplete _listener;
    @Inject
    ConsumptionRepository _consumptionRepository;

    public InsertConsumptionTask(Context context, Consumption consumption, ConsumptionRepository consumptionRepository){
        _context = context;
        _consumption = consumption;
        _consumptionRepository = consumptionRepository;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        _consumptionRepository.insert(_consumption);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (_listener != null)
            _listener.consumptionInserted(_consumption);
    }

    public interface ITaskComplete{
        public void consumptionInserted(Consumption consumption);
    }
}
