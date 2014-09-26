package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import uk.co.pilllogger.events.CreatedNoteEvent;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.repositories.NoteRepository;

public class InsertNoteJob extends Job {

    private final Note _note;

    @Inject
    NoteRepository _noteRepository;

    @Inject
    Bus _bus;

    public InsertNoteJob(Note note){
        super(new Params(Priority.LOW).persist());
        _note = note;
    }

    @Override
    public void onAdded() {
        _bus.post(new CreatedNoteEvent(_note));
    }

    @Override
    public void onRun() throws Throwable {
        _noteRepository.insert(_note);
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
