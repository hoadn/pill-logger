package uk.co.pilllogger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import uk.co.pilllogger.R;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 30/10/2013.
 */
public class SettingsActivity extends PillLoggerActivityBase {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(State.getSingleton().getTheme().getStyleResourceId());

        setContentView(R.layout.activity_settings);
    }
}