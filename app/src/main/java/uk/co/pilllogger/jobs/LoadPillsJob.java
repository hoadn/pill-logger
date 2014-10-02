package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.NoteRepository;
import uk.co.pilllogger.repositories.PillRepository;

public class LoadPillsJob extends Job {
    @Inject
    Bus _bus;

    @Inject
    PillRepository _pillRepository;


    public LoadPillsJob() {
        super(new Params(Priority.HIGH));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        List<Pill> pills = _pillRepository.getAll();

        _bus.post(new LoadedPillsEvent(pills));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}

