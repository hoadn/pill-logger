package uk.co.pilllogger;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;
import uk.co.pilllogger.helpers.CrashlyticsTree;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.
 */
public class App extends Application {

    private static App _instance;
    private ObjectGraph _objectGraph;

    public App() {
        _instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        else{
            Crashlytics.start(this);
            Timber.plant(new CrashlyticsTree());
        }

        _objectGraph = ObjectGraph.create(getModules().toArray());
        _objectGraph.inject(this);
    }

    protected List<Object> getModules(){
        return Arrays.<Object>asList(new AppModule(this));
    }

    public ObjectGraph getObjectGraph() {
        return _objectGraph;
    }

    public ObjectGraph createScopedGraph(Object... modules){
        return _objectGraph.plus(modules);
    }

    public static App getInstance(){
        return _instance;
    }
}

