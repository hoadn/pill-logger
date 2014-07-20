package uk.co.pilllogger.events;

import uk.co.pilllogger.state.FeatureType;

/**
 * Created by Alex on 20/07/2014
 * in uk.co.pilllogger.events.
 */
public class PurchasedFeatureEvent {

    private final FeatureType _featureType;

    public PurchasedFeatureEvent(FeatureType featureType){
        _featureType = featureType;
    }

    public FeatureType getFeatureType() {
        return _featureType;
    }
}
