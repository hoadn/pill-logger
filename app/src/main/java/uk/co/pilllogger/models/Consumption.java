package uk.co.pilllogger.models;

import java.util.Date;

/**
 * Created by nick on 22/10/13.
 */
public class Consumption implements Comparable {

    private Pill _pill;
    private Date _date;
    private int _id;
    private int _quantity;

    public Consumption() {
        _quantity = 1;
    }

    public Consumption(Pill pill, Date date) {
        this();
        this._pill = pill;
        this._date = date;
    }

    /// copy ctor
    public Consumption(Consumption consumption){
        setDate(consumption.getDate());
        setQuantity(consumption.getQuantity());
        setPill(consumption.getPill());
        setId(consumption.getId());
    }

    public Pill getPill() {
        return _pill;
    }

    public void setPill(Pill pill) {
        this._pill = pill;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public int getPillId() {
        return _pill.getId();
    }

    public int getQuantity(){return _quantity;}
    public void setQuantity(int quantity){_quantity = quantity;}

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Consumption))
            return 0;

        Consumption consumption = (Consumption) o;
        if (this == consumption)
            return 0;

        if (this.getDate().before(consumption.getDate()))
            return 1;
        else
            return -1;
    }
}
