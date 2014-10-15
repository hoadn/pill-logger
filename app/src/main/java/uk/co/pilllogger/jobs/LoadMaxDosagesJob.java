package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.Map;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.events.LoadedMaxDosagesEvent;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by Alex on 24/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class LoadMaxDosagesJob extends Job {

    @Inject
    ConsumptionRepository _consumptionRepository;

    @Inject
    Bus _bus;

    @DebugLog
    public LoadMaxDosagesJob(){
        super(new Params(Priority.HIGH));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Map<Integer, Integer> maxDosages = _consumptionRepository.getMaxDosages();

        _bus.post(new LoadedMaxDosagesEvent(maxDosages));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
