package uk.co.pilllogger.state;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;

/**
 * Created by nick on 28/10/13.
 */
public class State {

    private static State _instance;
    private Map<Pill, Integer> _openPills = new HashMap<Pill, Integer>();
    private List<Integer> _graphExcludePills;
    private Typeface _typeface;
    private Typeface _scriptTypeface;
    private List<Pill> _consumptionPills = new ArrayList<Pill>();
    private boolean _appVisible = false;


    private State() {
    }

    public static State getSingleton() {
        if (_instance == null)
            _instance = new State();
        return _instance;
    }

    public Typeface getTypeface() {
        return _typeface;
    }

    public void setTypeface(Typeface typeface) {
        _typeface = typeface;
    }

    public List<Integer> getGraphExcludePills() {
        return _graphExcludePills;
    }

    public void setGraphExcludePills(List<Integer> graphExcludePills) {
        this._graphExcludePills = graphExcludePills;
    }

    public boolean isPillExcluded(Pill pill){
        return pill == null || _graphExcludePills.contains(pill.getId());
    }

    public void clearConsumpedPills() {
        _consumptionPills.clear();
    }
    public void addConsumedPill(Pill pill) {
        _consumptionPills.add(pill);
    }

    public void removeConsumedPill(Pill pill) {
        if (_consumptionPills.contains(pill))
            _consumptionPills.remove(pill);
    }

    /*
    This is only used when pill selected first time and the pill is auto
    added to the conumption
     */
    public void addConsumedPillAtStart(Pill pill) {
        if (!_consumptionPills.contains(pill))
            _consumptionPills.add(pill);
    }

    /*
    This is only used when the pill is deselected to remove all instances
     */
    public void removeAllInstancesOfPill(Pill removePill) {
        List<Pill> pillsToRemove = new ArrayList<Pill>();
        for(Pill pill : _consumptionPills) {
            if (pill.getId() == removePill.getId())
                pillsToRemove.add(pill);
        }
        for (Pill pill : pillsToRemove) {
            _consumptionPills.remove(pill);
        }
    }

    public List<Pill> getConsumptionPills() {
        return _consumptionPills;
    }

    public void addOpenPill(Pill pill) {
        if (!(_openPills.containsKey(pill)))
            _openPills.put(pill, 1);
        Logger.v("TEST", "pill: open in activity, size = " + _openPills.size());
    }

    public void removeOpenPill(Pill pill) {
        if (_openPills.containsKey(pill))
            _openPills.remove(pill);
        Logger.v("TEST", "removeOpenPill called");
    }

    public void clearOpenPillsList() {
        _openPills.clear();
        Logger.v("TEST", "clearOpenPillsList called");
    }

    public Map<Pill, Integer> getOpenPills() {
        return _openPills;
    }

    public boolean isAppVisible() {
        return _appVisible;
    }

    public void setAppVisible(boolean appVisible) {
        _appVisible = appVisible;
    }
}
