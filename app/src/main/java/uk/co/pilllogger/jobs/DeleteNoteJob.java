package uk.co.pilllogger.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import uk.co.pilllogger.events.DeleteNoteEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.repositories.NoteRepository;
import uk.co.pilllogger.repositories.PillRepository;

public class DeleteNoteJob extends Job {

    private final Note _note;

    @Inject
    NoteRepository _noteRepository;

    @Inject
    Bus _bus;

    public DeleteNoteJob(Note note){
        super(new Params(Priority.HIGH).persist());
        _note = note;
    }

    @Override
    public void onAdded() {
        _bus.post(new DeleteNoteEvent(_note));
    }

    @Override
    public void onRun() throws Throwable {
        _noteRepository.delete(_note);
    }

    @Override
    protected void onCancel() {
        // todo: tell user
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
