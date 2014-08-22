package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.repositories.ConsumptionRepository;

public class DeleteConsumptionJob extends Job {

    private final Consumption _consumption;
    private final boolean _deleteGroup;
    @Inject
    ConsumptionRepository _consumptionRepository;

    public DeleteConsumptionJob(Consumption consumption, boolean deleteGroup){
        super(new Params(Priority.LOW).persist());

        _consumption = consumption;
        _deleteGroup = deleteGroup;
    }

    @Override
    public void onAdded() {
        // todo: send event to add consumption to screen
    }

    @Override
    public void onRun() throws Throwable {
        if(_deleteGroup)
            _consumptionRepository.deleteGroupPill(_consumption);
        else
            _consumptionRepository.delete(_consumption);
    }

    @Override
    protected void onCancel() {
        // todo: send error to user
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
