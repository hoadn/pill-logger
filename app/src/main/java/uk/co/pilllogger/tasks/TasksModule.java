package uk.co.pilllogger.tasks;

import android.content.Context;

import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;
import hugo.weaving.DebugLog;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.tasks.
 */
@Module(
        injects = {
                GetConsumptionsTask.class,
                GetMaxDosagesTask.class,
                GetTutorialSeenTask.class,
                SetTutorialSeenTask.class
        },
        complete = false,
        library = true
)
public class TasksModule {
}
