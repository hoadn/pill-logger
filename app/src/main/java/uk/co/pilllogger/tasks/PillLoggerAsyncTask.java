package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import uk.co.pilllogger.App;

/**
 * Created by Alex on 19/08/2014
 * in uk.co.pilllogger.tasks.
 */
public abstract class PillLoggerAsyncTask<T, U, V> extends AsyncTask<T, U, V> {

    public PillLoggerAsyncTask(Context context){
        App.injectMembers(this);
    }

}
