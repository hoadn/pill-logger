package uk.co.pilllogger.state;

public class Feature{

    private final InAppId _id;
    private final String _price;

    public Feature(InAppId id, String price){
        _id = id;
        _price = price;
    }

    public InAppId getId() {
        return _id;
    }

    public String getPrice() {
        return _price;
    }

    public enum InAppId{
        CHARTS,
        WIDGET
    }
}
