package uk.co.pilllogger.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nick on 22/10/13.
 */
public class Consumption implements Comparable, Serializable {

    private Pill _pill;
    private Date _date;
    private int _id;
    private int _quantity;
    private String _group;

    public Consumption(){
        _quantity = 1;
    }

    public Consumption(Pill pill, Date date){
        this(pill, date, UUID.randomUUID().toString());
    }

    public Consumption(Pill pill, Date date, String group) {
        this();
        this._pill = pill;
        this._date = date;
        this._group = group;
    }

    /// copy ctor
    public Consumption(Consumption consumption){
        setDate(consumption.getDate());
        setQuantity(consumption.getQuantity());
        setPill(consumption.getPill());
        setId(consumption.getId());
        setGroup(consumption.getGroup());
    }

    public Pill getPill() {
        return _pill;
    }

    public void setPill(Pill pill) {
        this._pill = pill;
    }

    public Date getDate() {
        return _date != null ? _date : new Date();
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
        return _pill != null ? _pill.getId() : 0;
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
        else {
            if (!this.getGroup().equals("")) {
                if (this.getGroup().equals(consumption.getGroup())) {
                    return ((Integer) this.getPillId()).compareTo(consumption.getPillId());
                }
            }
            else{
                if(this.getDate().getTime() == consumption.getDate().getTime()){
                    return ((Integer) this.getPillId()).compareTo(consumption.getPillId());
                }
            }
        }

        return -1;
    }

    public String getGroup() {
        return _group;
    }

    public void setGroup(String group) {
        _group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Consumption that = (Consumption) o;

        if (_id != that._id) return false;
        if (_date != null ? !_date.equals(that._date) : that._date != null) return false;
        if (_group != null ? !_group.equals(that._group) : that._group != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _date != null ? _date.hashCode() : 0;
        result = 31 * result + _id;
        result = 31 * result + (_group != null ? _group.hashCode() : 0);
        return result;
    }
}
