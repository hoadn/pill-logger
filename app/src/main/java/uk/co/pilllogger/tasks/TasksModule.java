package uk.co.pilllogger.tasks;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import uk.co.pilllogger.repositories.PillRepository;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.tasks.
 */
@Module(
        injects = {
                DeleteConsumptionTask.class,
                DeletePillTask.class,
                GetConsumptionsTask.class,
                GetFavouritePillsTask.class,
                GetMaxDosagesTask.class,
                GetPillsTask.class,
                GetTutorialSeenTask.class,
                InitTestDbTask.class,
                InsertConsumptionTask.class,
                InsertPillTask.class,
                SetTutorialSeenTask.class,
                UpdatePillTask.class
        },
        complete = false,
        library = true
)
public class TasksModule {
    @Provides
    public GetPillsTask provideGetPillsTask(Context context, PillRepository pillRepository){
        return new GetPillsTask(context, pillRepository);
    }
}
