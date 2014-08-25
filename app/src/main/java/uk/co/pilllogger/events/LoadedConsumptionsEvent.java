package uk.co.pilllogger.events;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 24/08/2014
 * in uk.co.pilllogger.events.
 */
public class LoadedConsumptionsEvent {
    private List<Consumption> _consumptions;

    public LoadedConsumptionsEvent(List<Consumption> consumptions) {
        _consumptions = consumptions;
    }

    public List<Consumption> getConsumptions() {
        if(_consumptions == null){
            _consumptions = new ArrayList<Consumption>();
        }
        return _consumptions;
    }
}
