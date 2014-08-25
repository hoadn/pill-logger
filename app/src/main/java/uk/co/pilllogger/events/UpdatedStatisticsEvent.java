package uk.co.pilllogger.events;

import java.util.List;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 25/08/2014
 * in uk.co.pilllogger.events.
 */
public class UpdatedStatisticsEvent {
    private final List<Consumption> _consumptions;

    public UpdatedStatisticsEvent(List<Consumption> consumptions){

        _consumptions = consumptions;
    }

    public List<Consumption> getConsumptions() {
        return _consumptions;
    }
}
