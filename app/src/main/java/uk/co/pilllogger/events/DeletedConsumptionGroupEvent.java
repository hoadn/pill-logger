package uk.co.pilllogger.events;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class DeletedConsumptionGroupEvent {
    private final String _group;
    private final int _pillId;

    public DeletedConsumptionGroupEvent(String group, int pillId){

        _group = group;
        _pillId = pillId;
    }

    public String getGroup() {
        return _group;
    }

    public int getPillId() {
        return _pillId;
    }
}
