package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Note;


public class DeleteNoteEvent {
    private final Note _note;

    public DeleteNoteEvent(Note note) {
        _note = note;
    }

    public Note getNote() {
        return _note;
    }
}
