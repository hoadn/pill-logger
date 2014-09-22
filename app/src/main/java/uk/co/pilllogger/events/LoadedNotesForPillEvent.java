package uk.co.pilllogger.events;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Note;

public class LoadedNotesForPillEvent {
    private final List<Note> _notes;

    public LoadedNotesForPillEvent(List<Note> notes) {
        if(notes == null){ // ensure we don't cause null reference exceptions
            _notes = new ArrayList<Note>();
        }
        else {
            _notes = notes;
        }
    }

    @NotNull
    public List<Note> getNotes() {
        return _notes;
    }
}
