package uk.co.pilllogger.adapters;

import android.content.Context;

import com.path.android.jobqueue.JobManager;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;

public class ConsumptionListAdapterFactory{
    private final Context _context;
    private final JobManager _jobManager;

    @Inject
    public ConsumptionListAdapterFactory(Context context, JobManager jobManager){
        _context = context;
        _jobManager = jobManager;
    }

    public ConsumptionListAdapter create(int resourceId, List<Consumption> consumptions){
        return new ConsumptionListAdapter(_context, _jobManager, resourceId, consumptions);
    }

    public ConsumptionListAdapter create(int resourceId, List<Consumption> consumptions, List<Pill> pills){
        return new ConsumptionListAdapter(_context, _jobManager, resourceId, consumptions, pills);
    }
}

