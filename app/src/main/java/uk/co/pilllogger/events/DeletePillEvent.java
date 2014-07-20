package uk.co.pilllogger.events;

import uk.co.pilllogger.fragments.PillInfoDialogFragment;
import uk.co.pilllogger.models.Pill;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class DeletePillEvent {
    private final Pill _pill;
    private final PillInfoDialogFragment _pillInfoDialogFragment;

    public DeletePillEvent(Pill pill, PillInfoDialogFragment pillInfoDialogFragment) {
        _pill = pill;
        _pillInfoDialogFragment = pillInfoDialogFragment;
    }

    public Pill getPill() {
        return _pill;
    }

    public PillInfoDialogFragment getPillInfoDialogFragment() {
        return _pillInfoDialogFragment;
    }
}
