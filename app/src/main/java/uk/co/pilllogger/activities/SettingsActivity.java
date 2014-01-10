package uk.co.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import uk.co.pilllogger.R;

/**
 * Created by alex on 30/10/2013.
 */
public class SettingsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

    }
}