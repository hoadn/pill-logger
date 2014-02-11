package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.pilllogger.listeners.AddConsumptionListener;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 31/10/13.
 */
public class InsertConsumptionTask  extends AsyncTask<Void, Void, Void> {

    Context _context;
    Consumption _consumption;
    ITaskComplete _listener;

    public InsertConsumptionTask(Context context, Consumption consumption) {
        _context = context;
        _consumption = consumption;
    }

    public  InsertConsumptionTask(Context context, Consumption consumption, ITaskComplete listener) {
        this(context, consumption);
        _listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ConsumptionRepository.getSingleton(_context).insert(_consumption);
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
