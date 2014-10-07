package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.events.CreatedNoteEvent;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.repositories.NoteRepository;

public class InsertNoteJob extends Job {

    private final Note _note;

    @Inject
    NoteRepository _noteRepository;

    @Inject
    Bus _bus;

    @DebugLog
    public InsertNoteJob(Note note){
        super(new Params(Priority.LOW).persist());
        _note = note;
    }

    @Override @DebugLog
    public void onAdded() {

    }

    @Override @DebugLog
    public void onRun() throws Throwable {
        _note.setId((int) _noteRepository.insert(_note));

        _bus.post(new CreatedNoteEvent(_note));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
