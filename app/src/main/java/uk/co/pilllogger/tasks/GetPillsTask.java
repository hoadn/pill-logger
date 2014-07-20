package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 21/10/13.
 */
public class GetPillsTask extends AsyncTask<Void, Void, List<Pill>>{

    Context _context;

    public GetPillsTask(Context context) {
        _context = context;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        List<Pill> pills = PillRepository.getSingleton(_context).getAll();
        return pills;
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        State.getSingleton().getBus().post(new LoadedPillsEvent(pills));
    }
}
