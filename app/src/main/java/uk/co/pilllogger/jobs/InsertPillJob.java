package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import uk.co.pilllogger.events.CreatedPillEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by Alex on 23/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class InsertPillJob extends Job {

    private final Pill _pill;

    @Inject
    PillRepository _pillRepository;

    @Inject
    Bus _bus;

    public InsertPillJob(Pill pill){
        super(new Params(Priority.LOW).persist());
        _pill = pill;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        _pillRepository.insert(_pill);

        _bus.post(new CreatedPillEvent(_pill));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
