package uk.co.pilllogger.state;

public class Feature{

    private final FeatureType _id;
    private final String _price;

    public Feature(FeatureType id, String price){
        _id = id;
        _price = price;
    }

    public FeatureType getId() {
        return _id;
    }

    public String getPrice() {
        return _price;
    }
}
