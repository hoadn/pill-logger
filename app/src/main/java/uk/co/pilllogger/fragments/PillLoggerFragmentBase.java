package uk.co.pilllogger.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerFragmentBase extends Fragment {
    private Tracker tracker;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.tracker = EasyTracker.getInstance(this.getActivity());
    }

    @Override
    public void onResume() {

        super.onResume();

        this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
        this.tracker.send( MapBuilder.createAppView().build() );
    }

    protected void executeRunnable(Runnable runnable){
        Activity activity = getActivity();
        if(activity != null)
            activity.runOnUiThread(runnable);
        else{
            try{
                runnable.run();
            }
            catch(Exception ignored){}
        }
    }
}
