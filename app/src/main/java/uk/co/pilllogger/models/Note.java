package uk.co.pilllogger.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nicholas.Allen on 08/09/2014.
 */
public class Note implements Serializable {
    private Date _date;
    private String _title;
    private String _text;
    private Pill _pill;
    private int _id;

    public Note() {
        _date = new Date();
    }

    public Note(Pill pill) {
        this();
        _pill = pill;
        _date = new Date();
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

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void updateFromNote(Note note) {
        setTitle(note.getTitle());
        setId(note.getId());
        setDate(note.getDate());
        setPill(note.getPill());
        setText(note.getText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (_id != note._id) return false;
        if (_date != null ? !_date.equals(note._date) : note._date != null) return false;
        if (_text != null ? !_text.equals(note._text) : note._text != null) return false;
        if (_title != null ? !_title.equals(note._title) : note._title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _date != null ? _date.hashCode() : 0;
        result = 31 * result + (_title != null ? _title.hashCode() : 0);
        result = 31 * result + (_text != null ? _text.hashCode() : 0);
        result = 31 * result + _id;
        return result;
    }
}
