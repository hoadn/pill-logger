package uk.co.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by alex on 12/11/13.
 */
public class DeleteConsumptionTask extends AsyncTask<Void, Void, Void> {

    Activity _activity;
    Consumption _consumption;
    private boolean _deleteGroup;
    @Inject
    ConsumptionRepository _consumptionRepository;

    public DeleteConsumptionTask(Activity activity, Consumption consumption, boolean deleteGroup) {
        _activity = activity;
        _consumption = consumption;
        _deleteGroup = deleteGroup;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(_deleteGroup)
            _consumptionRepository.deleteGroupPill(_consumption);
        else
            _consumptionRepository.delete(_consumption);

        return null;
    }
}
