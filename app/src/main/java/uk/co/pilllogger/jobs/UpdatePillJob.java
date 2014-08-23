package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import javax.inject.Inject;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.PillRepository;

public class UpdatePillJob extends Job {

    private final Pill _pill;

    @Inject
    PillRepository _pillRepository;

    public UpdatePillJob(Pill pill){
        super(new Params(Priority.LOW).persist());
        _pill = pill;
    }

    @Override
    public void onAdded() {
        // todo: event saying Pill updated
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
