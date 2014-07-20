package uk.co.pilllogger.events;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class LoadedPillsEvent {
    private final List<Pill> _pills;

    public LoadedPillsEvent(List<Pill> pills) {
        if(pills == null){ // ensure we don't cause null reference exceptions
            _pills = new ArrayList<Pill>();
        }
        else {
            _pills = pills;
        }
    }

    public List<Pill> getPills() {
        return _pills;
    }
}
