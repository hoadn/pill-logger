package uk.co.pilllogger.events;

import java.util.Map;

/**
 * Created by Alex on 13/10/2014
 * in uk.co.pilllogger.events.
 */
public class LoadedMaxDosagesEvent {
    private final Map<Integer, Integer> _maxDosages;

    public LoadedMaxDosagesEvent(Map<Integer, Integer> maxDosages) {
        _maxDosages = maxDosages;
    }

    public Map<Integer, Integer> getMaxDosages() {
        return _maxDosages;
    }
}
