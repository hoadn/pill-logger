package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Consumption;

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
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_activity);
        dbHelper.deleteConsumption(_consumption);
        return null;
    }
}
