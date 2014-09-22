package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.events.LoadedNotesEvent;
import uk.co.pilllogger.events.LoadedNotesForPillEvent;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.NoteRepository;

public class LoadNoteFromPillJob extends Job {
    @Inject
    Bus _bus;

    @Inject
    NoteRepository _noteRepository;

    private Pill _pill;

    public LoadNoteFromPillJob(Pill pill)
    {
        super(new Params(Priority.HIGH));
    }
    

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        List<Note> notes = _noteRepository.getForPill(_pill);

        _bus.post(new LoadedNotesForPillEvent(notes));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}

