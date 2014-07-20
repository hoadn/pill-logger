package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class CreatedConsumptionEvent {
    private final Consumption _consumption;

    public CreatedConsumptionEvent(Consumption consumption) {
        _consumption = consumption;
    }

    public Consumption getConsumption() {
        return _consumption;
    }
}
