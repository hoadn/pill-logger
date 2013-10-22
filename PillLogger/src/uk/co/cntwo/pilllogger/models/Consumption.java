package uk.co.cntwo.pilllogger.models;

import java.util.Date;

/**
 * Created by nick on 22/10/13.
 */
public class Consumption {

    private String _pillName;
    private Date _date;

    public Consumption(String _pillName, Date _date) {
        this._pillName = _pillName;
        this._date = _date;
    }

    public String get_pillName() {
        return _pillName;
    }

    public void set_pillName(String _pillName) {
        this._pillName = _pillName;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }
}
