package uk.co.pilllogger.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.squareup.otto.Bus;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import uk.co.pilllogger.activities.PillLoggerActivityBase;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 25/01/2014.
 */
public class PillLoggerFragmentBase extends Fragment {
    private Tracker tracker;
    @Inject Bus _bus;
    private Activity _activity;
    private boolean _attached;
    private ObjectGraph _fragmentGraph;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        _activity = activity;

        if(!_attached){
            PillLoggerActivityBase pillLoggerActivityBase = (PillLoggerActivityBase) activity;
            _fragmentGraph = pillLoggerActivityBase.getActivityGraph().plus(getModules().toArray());
            _fragmentGraph.inject(this);

            _attached = true;
        }

        _bus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        _bus.unregister(this);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.tracker = EasyTracker.getInstance(this.getActivity());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        _fragmentGraph = null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.tracker.set(Fields.SCREEN_NAME, getClass().getSimpleName());
            this.tracker.send(MapBuilder.createAppView().build());
        }
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

    protected List<Object> getModules() {
        return Arrays.<Object>asList();
    }
}
