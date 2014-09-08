package uk.co.pilllogger.models;

import java.util.Date;

/**
 * Created by Nicholas.Allen on 08/09/2014.
 */
public class Note {
    private Date _date;
    private String _text;
    private Pill _pill;
    private int _id;

    public Note() {
        _date = new Date();
    }

    public Note(Pill pill) {
        this();
        _pill = pill;
    }


    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    public Pill getPill() {
        return _pill;
    }

    public int getPillId() {
        return _pill.getId();
    }

    public void setPill(Pill pill) {
        _pill = pill;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
}
