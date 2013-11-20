package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by alex on 12/11/13.
 */
public class DeleteConsumptionTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Consumption _consumption;

    public DeleteConsumptionTask(Activity activity, Consumption consumption) {
        _activity = activity;
        _consumption = consumption;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ConsumptionRepository.getSingleton(_activity).delete(_consumption);
        return null;
    }
}
