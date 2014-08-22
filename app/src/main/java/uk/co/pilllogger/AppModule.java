package uk.co.pilllogger;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.pilllogger.events.AndroidBus;
import uk.co.pilllogger.jobs.JobModule;
import uk.co.pilllogger.models.ModelModule;
import uk.co.pilllogger.tasks.TasksModule;
import uk.co.pilllogger.widget.MyAppWidgetProvider;

@Module(
        injects = {
            App.class,
            MyAppWidgetProvider.class
        },
        includes = {
                JobModule.class,
                UiModule.class,
                ModelModule.class,
                TasksModule.class
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
