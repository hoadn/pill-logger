package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.cntwo.pilllogger.helpers.DatabaseHelper;
import uk.co.cntwo.pilllogger.helpers.DateHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.helpers.NumberHelper;
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
            dbHelper.insertPill(new Pill("Paracetamol", 500));
            dbHelper.insertPill(new Pill("Ibuprofen", 200));
            dbHelper.insertPill(new Pill("Paracetamol Extra", 500));
            dbHelper.insertPill(new Pill("Ibuprofen", 400));
            dbHelper.insertPill(new Pill("Asprin", 300));
        }
    }

    private void insertConsumptions(){
        DatabaseHelper dbHelper = DatabaseHelper.getSingleton(_context);
        List<Consumption> consumptions = dbHelper.getAllConsumptions();
        List<Pill> pills = dbHelper.getAllPills();

        if (consumptions.size() == 0) { //This will insert some consumptions as test data if your data doesn't have any in
            for (int i = 0; i < 60; i ++) { //I am only doing this to see what a list of consumption would look like
                int maxDays = DateHelper.daysOfMonth();
                int day = NumberHelper.randInt(0, maxDays -1);

                DateTime consumptionDate = new DateTime().minusDays(day);

                int pillIndex = NumberHelper.randInt(0, pills.size() - 1);

                Pill pill = pills.get(pillIndex);

                Consumption consumption = new Consumption(pill, consumptionDate.toDate());

                dbHelper.insertConsumption(consumption);
            }
        }
    }

    public interface ITaskComplete{
        public void initComplete();
    }
}
