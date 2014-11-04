package uk.co.pilllogger.events;

import uk.co.pilllogger.models.Note;


public class UpdatedNoteEvent {
    private final Note _note;

    public UpdatedNoteEvent(Note note){
        _note = note;
    }


    public Note getNote() {
        return _note;
    }

}
