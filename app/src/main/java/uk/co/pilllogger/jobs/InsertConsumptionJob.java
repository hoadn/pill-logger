package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by Alex on 22/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class InsertConsumptionJob extends Job {

    private final Consumption _consumption;
    @Inject
    ConsumptionRepository _consumptionRepository;

    @DebugLog
    public InsertConsumptionJob(Consumption consumption){
        super(new Params(Priority.LOW).persist());

        _consumption = consumption;
    }

    @Override @DebugLog
    public void onAdded() {
        // todo: send event to add consumption to screen
    }

    @Override @DebugLog
    public void onRun() throws Throwable {
        _consumptionRepository.insert(_consumption);
    }

    @Override @DebugLog
    protected void onCancel() {
        // todo: send error to user
    }

    @Override @DebugLog
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}

