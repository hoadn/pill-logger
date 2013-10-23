package uk.co.cntwo.pilllogger.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.co.cntwo.pilllogger.database.DatabaseContract;
import uk.co.cntwo.pilllogger.database.DatabaseCreator;
import uk.co.cntwo.pilllogger.models.Consumption;
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

    public Pill getPill(int id) {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Pills._ID,
                DatabaseContract.Pills.COLUMN_NAME,
                DatabaseContract.Pills.COLUMN_SIZE,
        };

        String selection = DatabaseContract.Pills._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };
        Pill pill = new Pill();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Pills.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                pill.setId(c.getInt(c.getColumnIndex(DatabaseContract.Pills._ID)));
                pill.setName(c.getString(c.getColumnIndex(DatabaseContract.Pills.COLUMN_NAME)));
                pill.setSize(c.getInt(c.getColumnIndex(DatabaseContract.Pills.COLUMN_SIZE)));
                c.moveToNext();
            }
        }
        return pill;
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

    public long insertConsumption(Consumption consumption) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Consumptions.COLUMN_PILL_ID, consumption.get_pill_id());
        values.put(DatabaseContract.Consumptions.COLUMN_DATE_TIME, consumption.get_date());
        long newRowId = 0L;
        if (db != null) {
            newRowId = db.insert(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    null,
                    values);
        }
        return newRowId;
    }

    public Consumption getConsumption(int id) {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Consumptions._ID,
                DatabaseContract.Consumptions.COLUMN_PILL_ID,
                DatabaseContract.Consumptions.COLUMN_DATE_TIME,
        };

        String selection = DatabaseContract.Consumptions._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };
        Consumption consumption = new Consumption();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                consumption.set_id(c.getInt(c.getColumnIndex(DatabaseContract.Consumptions._ID)));
                consumption.set_date(c.getString(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME)));
                int pillId = c.getInt(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_PILL_ID));
                Pill pill = getPill(pillId);
                consumption.set_pill(pill);
                c.moveToNext();
            }
        }
        return consumption;
    }

    public List<Consumption> getAllConsumptions() {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Consumptions._ID,
                DatabaseContract.Consumptions.COLUMN_PILL_ID,
                DatabaseContract.Consumptions.COLUMN_DATE_TIME,
        };

        String sortOrder = DatabaseContract.Consumptions._ID + " DESC";
        List<Consumption> consumptions = new ArrayList<Consumption>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Consumption consumption = new Consumption();
                consumption.set_id(c.getInt(c.getColumnIndex(DatabaseContract.Consumptions._ID)));
                consumption.set_date(c.getString(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME)));
                int pillId = c.getInt(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_PILL_ID));
                Pill pill = getPill(pillId);
                consumption.set_pill(pill);
                consumptions.add(consumption);
                c.moveToNext();
            }
        }

        return consumptions;
    }
}
