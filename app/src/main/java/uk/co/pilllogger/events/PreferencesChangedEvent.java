package uk.co.pilllogger.events;

import android.content.SharedPreferences;

/**
 * Created by Alex on 06/10/2014
 * in uk.co.pilllogger.events.
 */
public class PreferencesChangedEvent {

    private final SharedPreferences _sharedPreferences;
    private final String _key;

    public PreferencesChangedEvent(SharedPreferences sharedPreferences, String key) {
        _sharedPreferences = sharedPreferences;

        _key = key;
    }

    public String getKey() {
        return _key;
    }

    public SharedPreferences getSharedPreferences() {
        return _sharedPreferences;
    }
}
