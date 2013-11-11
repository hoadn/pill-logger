package uk.co.cntwo.pilllogger.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nick on 22/10/13.
 */
public class Consumption {

    private Pill _pill;
    private Date _date;
    private int _id;

    public Consumption() {

    }

    public Consumption(Pill pill, Date date) {
        this._pill = pill;
        this._date = date;
    }

    public Pill get_pill() {
        return _pill;
    }

    public void set_pill(Pill pill) {
        this._pill = pill;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date date) {
        this._date = date;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_pill_id() {
        return _pill.getId();
    }
}
