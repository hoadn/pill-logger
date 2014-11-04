package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Pill;


public class PillNotesChangeEvent {
    private final Pill _pill;

    public PillNotesChangeEvent(Pill pill){
        _pill = pill;
    }


    public Pill getPill() {
        return _pill;
    }

}
