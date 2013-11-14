package uk.co.cntwo.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import uk.co.cntwo.pilllogger.helpers.DateHelper;
import uk.co.cntwo.pilllogger.helpers.Logger;
import uk.co.cntwo.pilllogger.helpers.NumberHelper;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;
import uk.co.cntwo.pilllogger.repositories.ConsumptionRepository;
import uk.co.cntwo.pilllogger.repositories.PillRepository;

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
        PillRepository repository = PillRepository.getSingleton(_context);
        List<Pill> pills = repository.getAll();

        if (pills.size() == 0) { //This will insert 2 pills as test data if your database doesn't have any in
            repository.insert(new Pill("Paracetamol", 500));
            repository.insert(new Pill("Ibuprofen", 200));
            repository.insert(new Pill("Paracetamol Extra", 500));
            repository.insert(new Pill("Ibuprofen", 400));
            repository.insert(new Pill("Asprin", 300));
        }
    }

    private void insertConsumptions(){
        ConsumptionRepository consumptionRepository = ConsumptionRepository.getSingleton(_context);
        PillRepository pillRepository = PillRepository.getSingleton(_context);
        List<Consumption> consumptions = consumptionRepository.getAll();
        List<Pill> pills = pillRepository.getAll();

        if (consumptions.size() == 0) { //This will insert some consumptions as test data if your data doesn't have any in
            for (int i = 0; i < 60; i ++) { //I am only doing this to see what a list of consumption would look like
                int maxDays = DateHelper.daysOfMonth();
                int day = NumberHelper.randInt(0, maxDays -1);

                DateTime consumptionDate = new DateTime().minusDays(day);

                int pillIndex = NumberHelper.randInt(0, pills.size() - 1);

                Pill pill = pills.get(pillIndex);

                Consumption consumption = new Consumption(pill, consumptionDate.toDate());

                consumptionRepository.insert(consumption);
            }
        }
    }

    public interface ITaskComplete{
        public void initComplete();
    }
}
