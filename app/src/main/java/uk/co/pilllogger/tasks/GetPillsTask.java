package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.state.State;

/**
 * Created by nick on 21/10/13.
 */
public class GetPillsTask extends AsyncTask<Void, Void, List<Pill>>{

    Context _context;

    PillRepository _pillRepository;
    private final Bus _bus;

    public GetPillsTask(Context context, PillRepository pillRepository, Bus bus) {
        _context = context;
        _pillRepository = pillRepository;
        _bus = bus;
    }
    @Override
    protected List<Pill> doInBackground(Void... voids) {
        return _pillRepository.getAll();
    }

    @Override
    protected void onPostExecute(List<Pill> pills) {
        _bus.post(new LoadedPillsEvent(pills));
    }
}
