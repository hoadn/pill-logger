package uk.co.pilllogger.state;

import android.graphics.Typeface;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.pilllogger.billing.SkuDetails;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.themes.ITheme;
import uk.co.pilllogger.themes.ProfessionalTheme;

/**
 * Created by nick on 28/10/13.
 */
public class State {

    private static State _instance;
    private Map<Pill, Integer> _openPills = new HashMap<Pill, Integer>();
    private List<Integer> _graphExcludePills = new ArrayList<Integer>();
    private Typeface _typeface;
    private Typeface _scriptTypeface;
    private Typeface _roboto;
    private List<Pill> _consumptionPills = new ArrayList<Pill>();
    private boolean _appVisible = false;
    private HashMap<FeatureType, SkuDetails> _availableFeatures = new HashMap<FeatureType, SkuDetails>();
    private Set<FeatureType> _enabledFeatures = new HashSet<FeatureType>();
    private ITheme _theme = new ProfessionalTheme();
    private MixpanelAPI _mixpanelAPI = null;

    private State() {
    }

    public static State getSingleton() {
        if (_instance == null)
            _instance = new State();
        return _instance;
    }

    public void setRobotoTypeface(Typeface typeface) {
        _roboto = typeface;
    }

    public Typeface getRobotoTypeface() {
        return _roboto;
    }

    public MixpanelAPI getMixpanelAPI() {
        return _mixpanelAPI;
    }

    public void setMixpanelAPI(MixpanelAPI mixpanelAPI) {
        _mixpanelAPI = mixpanelAPI;
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
    }

    public void removeOpenPill(Pill pill) {
        if (_openPills.containsKey(pill))
            _openPills.remove(pill);
    }

    public void clearOpenPillsList() {
        _openPills.clear();
    }

    public Map<Pill, Integer> getOpenPills() {
        return _openPills;
    }

    public boolean isAppVisible() {return _appVisible;}

    public void setAppVisible(boolean appVisible) {
        _appVisible = appVisible;
    }

        public ITheme getTheme() {
        return _theme;
    }

    public void setTheme(ITheme theme) {
        _theme = theme;
    }

    public HashMap<FeatureType, SkuDetails> getAvailableFeatures() { return _availableFeatures; }

    public Set<FeatureType> getEnabledFeatures(){
        return _enabledFeatures;
    }

    public boolean hasFeature(FeatureType feature){
        return _enabledFeatures.contains(feature);
    }
}

