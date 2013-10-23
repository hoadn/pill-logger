package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by alex on 23/10/13.
 */
public class InitTestDbTask extends AsyncTask<Void, Void, Void>{
    private String TAG = "InitTestDbTask";

    private Context _context;
    private ITaskComplete _listener;

    public InitTestDbTask(Context context, ITaskComplete listener){
        _context = context;
        _listener = listener;

        Logger.d(TAG, "Ctor");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        insertPills();
        insertConsumptions();

        return null;
    }

    @Override
    protected void onPostExecute(Void nothing){
        if(_listener != null)
            _listener.initComplete();
    }

    private void insertPills(){
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_context);
        List<Pill> pills = dbHelper.getAllPills();

        if (pills.size() == 0) { //This will insert 2 pills as test data if your database doesn't have any in
            Pill pill1 = new Pill("Paracetamol", 400);
            Pill pill2 = new Pill("Ibuprofen", 200);
            dbHelper.insertPill(pill1);
            dbHelper.insertPill(pill2);
        }
    }

    private void insertConsumptions(){
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_context);
        List<Consumption> consumptions = dbHelper.getAllConsumptions();

        if (consumptions.size() == 0) { //This will insert some consumptions as test data if your data doesn't have any in
            for (int i = 0; i < 10; i ++) { //I am only doing this to see what a list of consumption would look like
                Pill pill = dbHelper.getPill(1);
                Consumption consumption = new Consumption(pill, new Date());
                dbHelper.insertConsumption(consumption);
            }
        }
    }

    public interface ITaskComplete{
        public void initComplete();
    }
}
