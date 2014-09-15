package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Note;

public class CreatedNoteEvent {

    private final Note _note;

    public CreatedNoteEvent(Note note) {
        _note = note;
    }

    public Note getNote() {
        return _note;
    }
}
