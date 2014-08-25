package uk.co.pilllogger.jobs;

import android.content.Context;

import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;

import uk.co.pilllogger.App;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.jobs.
 */
public class PillLoggerJobManager extends JobManager {
    public PillLoggerJobManager(Context context){
        super(context, new Configuration.Builder(context)
                .injector(new DependencyInjector() {
                    @Override
                    public void inject(BaseJob job) {
                        App.injectMembers(job);
                    }
                })
                .build());
    }
}

