package uk.co.pilllogger.state;

import android.graphics.Typeface;

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
    private List<Integer> _graphExcludePills;

    public Typeface getTypeface() {
        return _typeface;
    }

    public void setTypeface(Typeface typeface) {
        _typeface = typeface;
    }

    private Typeface _typeface;

    private State() {
    }

    public static State getSingleton() {
        if (_instance == null)
            _instance = new State();
        return _instance;
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
}
