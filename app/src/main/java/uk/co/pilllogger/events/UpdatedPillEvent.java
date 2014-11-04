package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class UpdatedPillEvent {
    private final Pill _pill;
    private boolean _deleted = false;

    public UpdatedPillEvent(Pill pill){
        _pill = pill;
    }

    public UpdatedPillEvent(Pill pill, boolean deleted) {
        this(pill);
        _deleted = deleted;
    }

    public Pill getPill() {
        return _pill;
    }

    public boolean wasDeleted(){
        return _deleted;
    }
}
