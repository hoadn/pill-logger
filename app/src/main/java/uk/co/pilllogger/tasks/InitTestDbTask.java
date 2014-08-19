package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.helpers.NumberHelper;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by alex on 23/10/13.
 */
public class InitTestDbTask extends AsyncTask<Void, Void, Void>{
    private String TAG = "InitTestDbTask";

    @Inject
    ConsumptionRepository _consumptionRepository;

    private Context _context;
    private ITaskComplete _listener;
    @Inject PillRepository _pillRepository;

    public InitTestDbTask(Context context, ITaskComplete listener){
        _context = context;
        _listener = listener;
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
        List<Pill> pills = _pillRepository.getAll();

        if (pills.size() == 0) { //This will insert 2 pills as test data if your database doesn't have any in
            _pillRepository.insert(new Pill("Paracetamol", 500));
            _pillRepository.insert(new Pill("Ibuprofen", 200));
            _pillRepository.insert(new Pill("Paracetamol Extra", 500));
            _pillRepository.insert(new Pill("Ibuprofen", 400));
            _pillRepository.insert(new Pill("Asprin", 300));
        }
    }

    private void insertConsumptions(){
        List<Consumption> consumptions = _consumptionRepository.getAll();
        List<Pill> pills = _pillRepository.getAll();

        if (consumptions.size() == 0) { //This will insert some consumptions as test data if your data doesn't have any in
            for (int i = 0; i < 60; i ++) { //I am only doing this to see what a list of consumption would look like
                int maxDays = DateHelper.daysOfMonth();
                int day = NumberHelper.randInt(0, maxDays - 1);

                DateTime consumptionDate = new DateTime().minusDays(day);

                int pillIndex = NumberHelper.randInt(0, pills.size() - 1);

                Pill pill = pills.get(pillIndex);

                Consumption consumption = new Consumption(pill, consumptionDate.toDate());

                _consumptionRepository.insert(consumption);
            }
        }
    }

    public interface ITaskComplete{
        public void initComplete();
    }
}
