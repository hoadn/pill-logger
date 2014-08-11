package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 11/08/2014
 * in uk.co.pilllogger.events.
 */
public class CreatedPillEvent {

    private final Pill _pill;

    public CreatedPillEvent(Pill pill) {
        _pill = pill;
    }

    public Pill getPill() {
        return _pill;
    }
}
