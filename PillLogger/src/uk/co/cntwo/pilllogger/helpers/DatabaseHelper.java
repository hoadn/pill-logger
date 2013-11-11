package uk.co.cntwo.pilllogger.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.cntwo.pilllogger.database.DatabaseContract;
import uk.co.cntwo.pilllogger.database.DatabaseCreator;
import uk.co.cntwo.pilllogger.models.Consumption;
import uk.co.cntwo.pilllogger.models.Pill;

/**
 * Created by root on 21/10/13.
 */
public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    DatabaseCreator _dbCreator;
    private static DatabaseHelper _instance;

    private DatabaseHelper(Context context) {
        _dbCreator = new DatabaseCreator(context);
    }

    public static DatabaseHelper getSingleton(Context context) {
        if (_instance == null) {
            _instance = new DatabaseHelper(context);
        }
        return _instance;
    }

    public long insertPill(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getPillContentValues(pill);
        long newRowId = 0L;
        if (db != null) {
        newRowId = db.insert(
                DatabaseContract.Pills.TABLE_NAME,
                null,
                values);
        }
        return newRowId;
    }

    public void updatePill(Pill pill){
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getPillContentValues(pill);

        if(db != null){
            int update = db.update(
                    DatabaseContract.Pills.TABLE_NAME,
                    values,
                    "_ID = ?",
                    new String[]{String.valueOf(pill.getId())});

            Logger.d(TAG, "Pill updated. Favourite: " + pill.isFavourite());
        }
    }

    private ContentValues getPillContentValues(Pill pill){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Pills.COLUMN_NAME, pill.getName());
        values.put(DatabaseContract.Pills.COLUMN_SIZE, pill.getSize());
        values.put(DatabaseContract.Pills.COLUMN_COLOUR, pill.getColour());
        values.put(DatabaseContract.Pills.COLUMN_FAVOURITE, pill.isFavourite());

        return values;
    }

    public void deletePill(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String id = String.valueOf(pill.getId());

        db.delete(
                DatabaseContract.Pills.TABLE_NAME,
                "_ID = ?",
                new String[]{id});
    }

    public Pill getPill(int id) {
        String selection = DatabaseContract.Pills._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };

        return getPill(selection, selectionArgs);
    }

    public List<Pill> getAllPills() {
        return getPills(null, null);
    }

    public List<Pill> getFavouritePills(){
        String selection = DatabaseContract.Pills.COLUMN_FAVOURITE + " =?";
        String[] selectionArgs = {String.valueOf(1) };

        return getPills(selection, selectionArgs);
    }

    private Pill getPill(String selection, String[] selectionArgs){
        List<Pill> pills = getPills(selection, selectionArgs);

        return pills.size() == 0 ? null : pills.get(0);
    }

    private List<Pill> getPills(String selection, String[] selectionArgs){
        SQLiteDatabase db = _dbCreator.getReadableDatabase();
        String[] projection = getPillProjection();
        String sortOrder = DatabaseContract.Pills._ID + " ASC";
        List<Pill> pills = new ArrayList<Pill>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Pills.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Pill pill = getPillfromCursor(c);
                pills.add(pill);
                c.moveToNext();
            }
        }

        return pills;
    }

    private String[] getPillProjection(){
        String[] projection = {
                DatabaseContract.Pills._ID,
                DatabaseContract.Pills.COLUMN_NAME,
                DatabaseContract.Pills.COLUMN_SIZE,
                DatabaseContract.Pills.COLUMN_COLOUR,
                DatabaseContract.Pills.COLUMN_FAVOURITE
        };

        return projection;
    }

    private Pill getPillfromCursor(Cursor c){
        Pill pill = new Pill();
        pill.setId(c.getInt(c.getColumnIndex(DatabaseContract.Pills._ID)));
        pill.setName(c.getString(c.getColumnIndex(DatabaseContract.Pills.COLUMN_NAME)));
        pill.setSize(c.getInt(c.getColumnIndex(DatabaseContract.Pills.COLUMN_SIZE)));
        pill.setColour(c.getInt(c.getColumnIndex(DatabaseContract.Pills.COLUMN_COLOUR)));

        int fav = c.getInt(c.getColumnIndex(DatabaseContract.Pills.COLUMN_FAVOURITE));
        pill.setFavourite(fav != 0);
        return pill;
    }

    public long insertConsumption(Consumption consumption) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Consumptions.COLUMN_PILL_ID, consumption.get_pill_id());
        values.put(DatabaseContract.Consumptions.COLUMN_DATE_TIME, consumption.get_date().getTime());
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
                consumption.set_date(new Date(c.getLong(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME))));
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
                consumption.set_date(new Date(c.getLong(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME))));
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
