package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import uk.co.pilllogger.database.DatabaseCreator;
import uk.co.pilllogger.helpers.ArrayHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by alex on 14/11/2013.
 */
public abstract class BaseRepository<T> implements IRepository<T>{
    protected final Context _context;
    protected DatabaseCreator _dbCreator;
    Bus _bus;

    @DebugLog
    protected BaseRepository(Context context, Bus bus){
        _context = context;
        _bus = bus;
        _dbCreator = new DatabaseCreator(context);
        _bus.register(this);
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

    protected float getFloat(Cursor cursor, String columnName){
        int idx = getIdx(cursor, columnName);
        return cursor.getFloat(idx);
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
