package uk.co.pilllogger.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by Alex on 10/05/2014
 * in uk.co.pilllogger.preferences.
 */
public class SummaryListPreference extends ListPreference {
    private final static String TAG = SummaryListPreference.class.getName();

    public SummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SummaryListPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                arg0.setSummary(getEntry());
                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}
