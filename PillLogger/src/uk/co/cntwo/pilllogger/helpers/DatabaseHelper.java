package uk.co.cntwo.pilllogger.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.co.cntwo.pilllogger.database.DatabaseContract;
import uk.co.cntwo.pilllogger.database.DatabaseCreator;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by root on 21/10/13.
 */
public class DatabaseHelper {

    DatabaseCreator _dbCreator;

    public DatabaseHelper(Context context) {
        _dbCreator = new DatabaseCreator(context);
    }

    public long insertPill(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String name = pill.getName();
        int size = pill.getSize();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Pills.COLUMN_NAME, name);
        values.put(DatabaseContract.Pills.COLUMN_SIZE, size);
        long newRowId = 0L;
        if (db != null) {
        newRowId = db.insert(
                DatabaseContract.Pills.TABLE_NAME,
                null,
                values);
        }
        return newRowId;
    }

    public List<Pill> getAllPills() {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Pills._ID,
                DatabaseContract.Pills.COLUMN_NAME,
                DatabaseContract.Pills.COLUMN_SIZE,
        };

        String sortOrder = DatabaseContract.Pills._ID + " DESC";
        List<Pill> pills = new ArrayList<Pill>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Pills.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Pill pill = new Pill();
                pill.setId(c.getInt(c.getColumnIndex(DatabaseContract.Pills._ID)));
                pill.setName(c.getString(c.getColumnIndex(DatabaseContract.Pills.COLUMN_NAME)));
                pill.setSize(c.getInt(c.getColumnIndex(DatabaseContract.Pills.COLUMN_SIZE)));
                pills.add(pill);
                c.moveToNext();
            }
        }

        return pills;
    }
}
