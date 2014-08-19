package uk.co.pilllogger;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.pilllogger.events.AndroidBus;

@Module(
        injects = {
            App.class
        }
)
public class AppModule {
    private final App _application;
    private final Context _context;

    public AppModule(App app){
        _application = app;
        _context = _application.getApplicationContext();
    }

    @Provides
    @Singleton
    public Context provideApplicationContext(){
        return _context;
    }

    @Provides
    @Singleton
    public Bus provideBus(){
        return new AndroidBus(ThreadEnforcer.ANY);
    }
}
