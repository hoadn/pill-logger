package uk.co.pilllogger.events;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.models.Consumption;

/**
 * Created by Alex on 24/08/2014
 * in uk.co.pilllogger.events.
 */
public class LoadedConsumptionsEvent {
    private String _group = null;
    private List<Consumption> _consumptions;
    private boolean _loadedFromDb = true;

    public LoadedConsumptionsEvent(List<Consumption> consumptions) {
        _consumptions = consumptions;
    }

    public LoadedConsumptionsEvent(List<Consumption> consumptions, String group) {
        this(consumptions);
        _group = group;
    }

    public List<Consumption> getConsumptions() {
        if(_consumptions == null){
            _consumptions = new ArrayList<Consumption>();
        }
        return _consumptions;
    }

    public String getGroup() {
        return _group;
    }

    public boolean isLoadedFromDb() {
        return _loadedFromDb;
    }

    public void setLoadedFromDb(boolean _loadedFromDb) {
        this._loadedFromDb = _loadedFromDb;
    }
}
