package uk.co.cntwo.pilllogger.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

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
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_context);
        dbHelper.insertConsumption(_consumption);
        return null;
    }
}
