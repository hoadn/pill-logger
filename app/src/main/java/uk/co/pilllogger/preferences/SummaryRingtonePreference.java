package uk.co.pilllogger.preferences;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.util.AttributeSet;

/**
 * Created by Alex on 10/05/2014
 * in uk.co.pilllogger.preferences.
 */
public class SummaryRingtonePreference extends RingtonePreference {
    private final static String TAG = SummaryListPreference.class.getName();
    private final Context _context;

    public SummaryRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    public SummaryRingtonePreference(Context context) {
        super(context);
        _context = context;
        init();
    }

    private void init() {
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        _context, Uri.parse((String) arg1));
                arg0.setSummary(ringtone.getTitle(_context));
                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        String ringtonePath= getSharedPreferences().getString(getKey(), "None");
        Uri ringtoneUri = Uri.parse(ringtonePath);
        Ringtone ringtone = RingtoneManager.getRingtone(
                _context, ringtoneUri);

        String scheme = ringtoneUri.getScheme();
        if(scheme == null || !scheme.equals("content"))
            return "None";
        return ringtone.getTitle(_context);
    }
}
