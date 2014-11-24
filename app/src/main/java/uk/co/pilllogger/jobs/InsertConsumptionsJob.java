package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.events.CreatedConsumptionEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

/**
 * Created by Alex on 22/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class InsertConsumptionsJob extends Job {

    private final List<Consumption> _consumptions;
    @Inject
    ConsumptionRepository _consumptionRepository;

    @Inject
    Bus _bus;

    public InsertConsumptionsJob(Consumption consumption){
        this(Arrays.asList(consumption));
    }

    @DebugLog
    public InsertConsumptionsJob(List<Consumption> consumptions){
        super(new Params(Priority.MID).persist());

        _consumptions = consumptions;
    }

    @DebugLog
    public InsertConsumptionsJob(List<Consumption> consumptions, boolean shouldGroup) {
        super(new Params(Priority.MID).persist());
        String consumptionGroup = UUID.randomUUID().toString();
        for (Consumption c : consumptions) {
            c.setGroup(consumptionGroup);
        }
        _consumptions = consumptions;
    }

    @Override @DebugLog
    public void onAdded() {
        List<Consumption> groupedConsumptions = _consumptionRepository.groupConsumptions(_consumptions);

        _bus.post(new CreatedConsumptionEvent(groupedConsumptions));
    }

    @Override @DebugLog
    public void onRun() throws Throwable {
        for(Consumption c : _consumptions) {
            _consumptionRepository.insert(c);
        }
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

