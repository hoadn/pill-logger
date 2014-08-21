package uk.co.pilllogger;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.pilllogger.events.AndroidBus;
import uk.co.pilllogger.factories.PillFactory;
import uk.co.pilllogger.jobs.JobModule;
import uk.co.pilllogger.models.ModelModule;
import uk.co.pilllogger.repositories.ConsumptionRepository;
import uk.co.pilllogger.repositories.PillRepository;
import uk.co.pilllogger.stats.Statistics;
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

    @Provides
    @Singleton
    public PillRepository providePillRepository(Context context, Bus bus, ConsumptionRepository consumptionRepository, PillFactory pillFactory){
        return new PillRepository(context, bus, consumptionRepository, pillFactory);
    }

    @Provides
    @Singleton
    public ConsumptionRepository provideConsumptionRepository(Context context, Bus bus, PillFactory pillFactory){
        return new ConsumptionRepository(context, bus, pillFactory);
    }

    @Provides
    @Singleton
    public Statistics provideStatistics(Context context, Bus bus, ConsumptionRepository consumptionRepository){
        return new Statistics(context, bus, consumptionRepository);
    }

    @Provides
    @Singleton
    public PillFactory providePillFactory(Bus bus){
        return new PillFactory(bus);
    }
}
