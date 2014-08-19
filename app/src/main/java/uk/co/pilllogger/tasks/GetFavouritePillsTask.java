package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by nick on 21/10/13.
 */
public class GetFavouritePillsTask extends AsyncTask<Void, Void, List<Pill>>{

    Context _context;
    ITaskComplete _listener;

    @Inject PillRepository _pillRepository;

    public GetFavouritePillsTask(Context context, ITaskComplete listener) {
        _context = context;
        _listener = listener;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        return _pillRepository.getFavouritePills();
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        _listener.favouritePillsReceived(pills);
    }

    public interface ITaskComplete{
        public void favouritePillsReceived(List<Pill> pills);
    }
}
