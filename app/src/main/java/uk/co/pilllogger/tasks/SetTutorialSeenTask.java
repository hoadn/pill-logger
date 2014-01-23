package uk.co.pilllogger.tasks;

import android.content.Context;
import android.os.AsyncTask;

import uk.co.pilllogger.repositories.TutorialRepository;

/**
 * Created by nick on 23/01/14.
 */
public class SetTutorialSeenTask extends AsyncTask<Void, Void, Void> {

    Context _context;
    String _tag;

    public SetTutorialSeenTask(Context context, String tag) {
        _context = context;
        _tag = tag;
    }
    @Override
    protected Void doInBackground(Void... params) {
        TutorialRepository tutorialRepository = TutorialRepository.getSingleton(_context);
        tutorialRepository.tutorialSeen(_tag);
        return null;
    }
}
