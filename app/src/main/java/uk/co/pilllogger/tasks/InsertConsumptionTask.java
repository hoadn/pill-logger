package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by nick on 31/10/13.
 */
public class InsertConsumptionTask  extends AsyncTask<Void, Void, Void> {

    Context _context;
    Consumption _consumption;

    public InsertConsumptionTask(Context context, Consumption consumption) {
        _context = context;
        _consumption = consumption;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ConsumptionRepository.getSingleton(_context).insert(_consumption);
        return null;
    }
}
