package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import uk.co.pilllogger.events.LoadedNotesEvent;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.NoteRepository;
import uk.co.pilllogger.repositories.PillRepository;

public class LoadNotesJob extends Job {
    @Inject
    Bus _bus;

    @Inject
    NoteRepository _noteRepository;

    public LoadNotesJob() {
        super(new Params(Priority.HIGH));
    }


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        List<Note> notes = _noteRepository.getAll();

        _bus.post(new LoadedNotesEvent(notes));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}

