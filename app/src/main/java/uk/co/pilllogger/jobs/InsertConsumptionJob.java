package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by Alex on 22/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class InsertConsumptionJob extends Job {

    private final Consumption _consumption;
    @Inject
    ConsumptionRepository _consumptionRepository;

    @Inject
    Bus _bus;

    @DebugLog
    public InsertConsumptionJob(Consumption consumption){
        super(new Params(Priority.MID).persist());

        _consumption = consumption;
    }

    @Override @DebugLog
    public void onAdded() {
        _bus.post(new CreatedConsumptionEvent(_consumption));
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

