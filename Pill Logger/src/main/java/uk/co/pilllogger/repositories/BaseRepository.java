package uk.co.cntwo.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Date;

import uk.co.cntwo.pilllogger.database.DatabaseCreator;

/**
 * Created by alex on 14/11/2013.
 */
public abstract class BaseRepository<T> implements IRepository<T>{
    protected DatabaseCreator _dbCreator;
    protected Context _context;

    protected BaseRepository(Context context){
        _dbCreator = new DatabaseCreator(context);
        _context = context;
    }

    protected abstract ContentValues getContentValues(T data);
    protected abstract String[] getProjection();
    protected abstract T getFromCursor(Cursor cursor);

    protected int getIdx(Cursor cursor, String columnName){
        return cursor.getColumnIndex(columnName);
    }

    protected int getInt(Cursor cursor, String columnName){
        int idx = getIdx(cursor, columnName);
        return cursor.getInt(idx);
    }

    protected boolean getBoolean(Cursor cursor, String columnName){
        int idx = getIdx(cursor, columnName);
        return cursor.getInt(idx) > 0;
    }

    protected long getLong(Cursor cursor, String columnName){
        int idx = getIdx(cursor, columnName);
        return cursor.getLong(idx);
    }

    protected String getString(Cursor cursor, String columnName){
        int idx = getIdx(cursor, columnName);
        return cursor.getString(idx);
    }
}
