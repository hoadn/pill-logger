package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 21/10/13.
 */
public class GetPillsTask extends AsyncTask<Void, Void, List<Pill>>{

    private static final String TAG = "GetPillsTask";
    Context _context;
    ITaskComplete _listener;

    public GetPillsTask(Context context, ITaskComplete listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        List<Pill> pills = PillRepository.getSingleton(_context).getAll();
        Logger.d(TAG, "Timing: Returning pills");
        return pills;
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        Logger.d(TAG, "Timing: Going to call pillsReceived");
        _listener.pillsReceived(pills);

        State.getSingleton().getBus().post(new LoadedPillsEvent(pills));
    }

    public interface ITaskComplete{
        public void pillsReceived(List<Pill> pills);
    }
}
