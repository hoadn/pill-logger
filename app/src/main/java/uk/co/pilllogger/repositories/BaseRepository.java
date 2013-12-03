package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.database.DatabaseCreator;
import uk.co.pilllogger.helpers.ArrayHelper;

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
    protected abstract String getTableName();

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

    protected String getSelectFromProjection(){
        String[] projection = getProjection();
        List<String> projStrings = new ArrayList<String>();
        for(String s : projection){
            String col = getTableName() + "." + s;
            projStrings.add(col);
        }

        return ArrayHelper.StringArrayToString(projStrings);
    }
}
