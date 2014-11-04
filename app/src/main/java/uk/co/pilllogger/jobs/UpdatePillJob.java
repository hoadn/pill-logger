package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

public class UpdatePillJob extends Job {

    private final Pill _pill;

    @Inject
    PillRepository _pillRepository;

    @Inject
    Bus _bus;

    public UpdatePillJob(Pill pill){
        super(new Params(Priority.LOW).persist());
        _pill = pill;
    }

    @Override
    public void onAdded() {
        _bus.post(new UpdatedPillEvent(_pill));
    }

    @Override
    public void onRun() throws Throwable {
        _pillRepository.update(_pill);
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
