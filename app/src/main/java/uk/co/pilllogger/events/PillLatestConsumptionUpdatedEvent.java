package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 02/11/2014
 * in uk.co.pilllogger.events.
 */
public class PillLatestConsumptionUpdatedEvent {
    private final Pill _pill;

    public PillLatestConsumptionUpdatedEvent(Pill pill) {
        _pill = pill;
    }

    public Pill getPill() {
        return _pill;
    }
}
