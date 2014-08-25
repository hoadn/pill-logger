package uk.co.pilllogger.adapters;

import android.content.Context;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.ConsumptionRepository;

public class ConsumptionListAdapterFactory{
    private final Context _context;
    private final JobManager _jobManager;
    private final ConsumptionRepository _consumptionRepository;
    private final Bus _bus;

    @Inject
    public ConsumptionListAdapterFactory(Context context, JobManager jobManager, ConsumptionRepository consumptionRepository, Bus bus){
        _context = context;
        _jobManager = jobManager;
        _consumptionRepository = consumptionRepository;
        _bus = bus;
    }

    public ConsumptionListAdapter create(int resourceId, List<Consumption> consumptions){
        return new ConsumptionListAdapter(_context, _jobManager, _consumptionRepository, _bus, resourceId, consumptions);
    }

    public ConsumptionListAdapter create(int resourceId, List<Consumption> consumptions, List<Pill> pills){
        return new ConsumptionListAdapter(_context, _jobManager, _consumptionRepository, _bus, resourceId, consumptions, pills);
    }
}

