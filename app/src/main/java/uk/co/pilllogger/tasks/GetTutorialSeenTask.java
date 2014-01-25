package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.TooManyListenersException;

import uk.co.pilllogger.repositories.TutorialRepository;

/**
 * Created by nick on 23/01/14.
 */
public class GetTutorialSeenTask extends AsyncTask<Void, Void, Boolean> {

    Context _context;
    String _tag;
    ITaskComplete _listener;

    public GetTutorialSeenTask(Context context, String tag, ITaskComplete listener) {
        _context = context;
        _tag = tag;
        _listener = listener;
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        TutorialRepository tutorialRepository = TutorialRepository.getSingleton(_context);
        Boolean seen = false;
        if (tutorialRepository.hasTutorialBeenSeen(_tag)) {
            seen = true;
        }
        return seen;
    }

    @Override
    protected void onPostExecute(Boolean seen) {
        if (_listener != null)
            _listener.isTutorialSeen(seen, _tag);
        super.onPostExecute(seen);
    }

    public interface ITaskComplete {
        public void isTutorialSeen(Boolean seen, String tag);
    }
}
