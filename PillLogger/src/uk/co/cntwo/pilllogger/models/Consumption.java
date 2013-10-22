package uk.co.cntwo.pilllogger.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nick on 22/10/13.
 */
public class Consumption {

    private Pill _pill;
    private String _date;

    public Consumption(Pill pill, Date date) {
        this._pill = pill;
        this._date = new SimpleDateFormat("H:m  dd/MM").format(date);
    }

    public Pill get_pill() {
        return _pill;
    }

    public void set_pillName(Pill pill) {
        this._pill = pill;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(Date date) {
        this._date = new SimpleDateFormat("H:m  dd/MM").format(date);
    }

    public void set_date(String date) {
        this._date = date;
    }

    public int get_pill_id() {
        return _pill.getId();
    }
}
