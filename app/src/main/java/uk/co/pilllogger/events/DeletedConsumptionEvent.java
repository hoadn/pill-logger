package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class DeletedConsumptionEvent {
    private final Consumption _consumption;

    public DeletedConsumptionEvent(Consumption consumption){

        _consumption = consumption;
    }

    public Consumption getConsumption() {
        return _consumption;
    }
}
