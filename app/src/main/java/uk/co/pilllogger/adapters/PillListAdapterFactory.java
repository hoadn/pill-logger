package uk.co.pilllogger.adapters;

import android.content.Context;

import com.path.android.jobqueue.JobManager;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;

public class PillListAdapterFactory{
    private final Context _context;
    private final JobManager _jobManager;
    private final ConsumptionRepository _consumptionRepository;

    @Inject
    public PillListAdapterFactory(Context context, JobManager jobManager, ConsumptionRepository consumptionRepository){
        _context = context;
        _jobManager = jobManager;
        _consumptionRepository = consumptionRepository;
    }

    public PillsListAdapter create(int resourceId, List<Pill> pills){
        return new PillsListAdapter(_context, _jobManager, resourceId, pills, _consumptionRepository);
    }
}
