package uk.co.pilllogger.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.models.Pill;

/**
 * Created by nick on 28/10/13.
 */
public class State {

    private static State _instance;
    private List<Pill> _pillsOpen = new ArrayList<Pill>();
    private Map<Pill, Integer> _pillsOpenList = new HashMap<Pill, Integer>();
    private List<Integer> _graphPills;

    private State() {
    }

    public static State getSingleton() {
        if (_instance == null)
            _instance = new State();
        return _instance;
    }

    public void addOpenPill(Pill pill) {
        if (!(_pillsOpenList.containsKey(pill)))
            _pillsOpenList.put(pill, 1);
    }

    public void removeOpenPill(Pill pill) {
        if (_pillsOpenList.containsKey(pill))
            _pillsOpenList.remove(pill);
    }

    public void clearOpenPillsList() {
        _pillsOpenList.clear();
    }

    public Map<Pill, Integer> getOpenPillsList() {
        return _pillsOpenList;
    }

    public void setOpenPillsList(Map<Pill, Integer> openPills) {
        _pillsOpenList = openPills;
    }

    public List<Integer> getGraphPills() {
        return _graphPills;
    }

    public void setGraphPills(List<Integer> graphPills) {
        this._graphPills = graphPills;
    }
}
