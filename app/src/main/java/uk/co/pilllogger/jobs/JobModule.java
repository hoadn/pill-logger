package uk.co.pilllogger.jobs;

import android.content.Context;

import com.path.android.jobqueue.JobManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                LoadPillsJob.class,
                LoadConsumptionsJob.class,
                InsertConsumptionsJob.class,
                DeleteConsumptionJob.class,
                InsertPillJob.class,
                UpdatePillJob.class,
                DeletePillJob.class,
                LoadNotesJob.class,
                InsertNoteJob.class
        },
        library = true,
        complete = false
)
public class JobModule{
    @Provides
    @Singleton
    public JobManager provideJobManager(Context context){
        return new PillLoggerJobManager(context);
    }
}
