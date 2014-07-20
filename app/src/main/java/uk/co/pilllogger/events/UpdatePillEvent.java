package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class UpdatePillEvent {
    private final Pill _pill;

    public UpdatePillEvent(Pill pill) {
        _pill = pill;
    }

    public Pill getPill() {
        return _pill;
    }
}
