package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 14/11/2013.
 */
public class PillRepository extends BaseRepository<Pill>{
    private static final String TAG = "PillRepository";

    private static PillRepository _instance;

    private PillRepository(Context context){
        super(context);
    }

    public static PillRepository getSingleton(Context context) {
        if (_instance == null) {
            _instance = new PillRepository(context);
        }
        return _instance;
    }

    @Override
    protected ContentValues getContentValues(Pill pill) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Pills.COLUMN_NAME, pill.getName());
        values.put(DatabaseContract.Pills.COLUMN_SIZE, pill.getSize());
        values.put(DatabaseContract.Pills.COLUMN_COLOUR, pill.getColour());
        values.put(DatabaseContract.Pills.COLUMN_FAVOURITE, pill.isFavourite());
        values.put(DatabaseContract.Pills.COLUMN_UNITS, pill.getUnits());

        return values;
    }

    @Override
    protected String[] getProjection() {
        String[] projection = {
                DatabaseContract.Pills._ID,
                DatabaseContract.Pills.COLUMN_NAME,
                DatabaseContract.Pills.COLUMN_SIZE,
                DatabaseContract.Pills.COLUMN_COLOUR,
                DatabaseContract.Pills.COLUMN_FAVOURITE
        };

        return projection;
    }

    @Override
    protected Pill getFromCursor(Cursor c) {
        Pill pill = new Pill();
        pill.setId(getInt(c, DatabaseContract.Pills._ID));
        pill.setName(getString(c, DatabaseContract.Pills.COLUMN_NAME));
        pill.setSize(getInt(c, DatabaseContract.Pills.COLUMN_SIZE));
        pill.setColour(getInt(c, DatabaseContract.Pills.COLUMN_COLOUR));

        int fav = getInt(c, DatabaseContract.Pills.COLUMN_FAVOURITE);
        pill.setFavourite(fav != 0);
        return pill;
    }

    @Override
    public long insert(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getContentValues(pill);
        long newRowId = 0L;
        if (db != null) {
            newRowId = db.insert(
                    DatabaseContract.Pills.TABLE_NAME,
                    null,
                    values);
        }
        return newRowId;
    }

    @Override
    public void update(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getContentValues(pill);

        if(db != null){
            db.update(
                    DatabaseContract.Pills.TABLE_NAME,
                    values,
                    "_ID = ?",
                    new String[]{String.valueOf(pill.getId())});

            Logger.d(TAG, "Pill updated. Favourite: " + pill.isFavourite());
        }
    }

    @Override
    public void delete(Pill pill) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String id = String.valueOf(pill.getId());

        if (db != null) {
            db.delete(
                    DatabaseContract.Pills.TABLE_NAME,
                    "_ID = ?",
                    new String[]{id});
        }
    }

    @Override
    public Pill get(int id) {
        String selection = DatabaseContract.Pills._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };

        return get(selection, selectionArgs);
    }

    private Pill get(String selection, String[] selectionArgs){
        List<Pill> pills = getList(selection, selectionArgs);

        return pills.size() == 0 ? null : pills.get(0);
    }

    private List<Pill> getList(String selection, String[] selectionArgs){
        SQLiteDatabase db = _dbCreator.getReadableDatabase();
        String[] projection = getProjection();
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
                Pill pill = getFromCursor(c);
                pills.add(pill);
                c.moveToNext();
            }
        }

        return pills;
    }

    public List<Pill> getFavouritePills(){
        String selection = DatabaseContract.Pills.COLUMN_FAVOURITE + " =?";
        String[] selectionArgs = {String.valueOf(1) };

        return getList(selection, selectionArgs);
    }

    @Override
    public List<Pill> getAll() {
        return getList(null, null);
    }
}
