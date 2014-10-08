package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.events.LoadedConsumptionsEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by Alex on 24/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class LoadConsumptionsJob extends Job {

    @Inject
    ConsumptionRepository _consumptionRepository;

    @Inject
    PillRepository _pillRepository;

    @Inject
    Bus _bus;

    private final boolean _shouldGroup;
    private String _group;

    @DebugLog
    public LoadConsumptionsJob(boolean shouldGroup){
        super(new Params(Priority.HIGH));

        _shouldGroup = shouldGroup;
    }

    @DebugLog
    public LoadConsumptionsJob(boolean shouldGroup, String group){
        this(shouldGroup);
        _group = group;
    }

    @Override
    public void onAdded() {

    }

    @Override @DebugLog
    public void onRun() throws Throwable {
        List<Consumption> consumptions;
        if(_group == null){
            consumptions = _consumptionRepository.getAll();
        }
        else{
            consumptions = _consumptionRepository.getForGroup(_group);
        }

        for(Consumption c : consumptions){
            Pill currentPill = c.getPill();

            currentPill.destroy();

            c.setPill(_pillRepository.get(c.getPillId()));
        }

        if(_shouldGroup && consumptions.size() > 0) {
            consumptions = _consumptionRepository.groupConsumptions(consumptions);
        }

        _bus.post(new LoadedConsumptionsEvent(consumptions));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
