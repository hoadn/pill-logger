package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;

/**
 * Created by alex on 14/11/2013.
 */
public class PillRepository extends BaseRepository<Pill>{
    private static final String TAG = "PillRepository";
    private static PillRepository _instance;
    private boolean _invalidateCache;
    private List<Pill> _cache = new ArrayList<Pill>();

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
                DatabaseContract.Pills.COLUMN_FAVOURITE,
                DatabaseContract.Pills.COLUMN_UNITS
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
        pill.setUnits(getString(c, DatabaseContract.Pills.COLUMN_UNITS));

        int fav = getInt(c, DatabaseContract.Pills.COLUMN_FAVOURITE);
        pill.setFavourite(fav != 0);

        List<Consumption> consumptions = ConsumptionRepository.getSingleton(_context).getForPill(pill);
        pill.getConsumptions().addAll(consumptions);

        return pill;
    }

    @Override
    protected String getTableName() {
        return DatabaseContract.Pills.TABLE_NAME;
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
        notifyUpdated();
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
        notifyUpdated();
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

        List<Consumption> pillConsumptions = ConsumptionRepository.getSingleton(_context).getForPill(pill);
        for (Consumption consumption : pillConsumptions) {
            ConsumptionRepository.getSingleton(_context).delete(consumption);
        }
        notifyUpdated();
    }


    @Override
    public Pill get(int id) {
        String selection = getTableName() + "." + DatabaseContract.Pills._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };

        return get(selection, selectionArgs);
    }

    private Pill get(String selection, String[] selectionArgs){
        List<Pill> pills = getList(selection, selectionArgs);

        return pills.size() == 0 ? null : pills.get(0);
    }

    private List<Pill> getList(String selection, String[] selectionArgs){
        SQLiteDatabase db = _dbCreator.getReadableDatabase();
        List<Pill> pills = new ArrayList<Pill>();
        if (db != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select distinct ");
            sql.append(getSelectFromProjection());
            sql.append(" from ");
            sql.append(DatabaseContract.Pills.TABLE_NAME);
            //sql.append(" left join " );
            //sql.append(DatabaseContract.Consumptions.TABLE_NAME);
            //sql.append(" on ");
            //sql.append(getTableName()).append(".").append(DatabaseContract.Pills._ID);
            //sql.append(" = ");
            //sql.append(DatabaseContract.Consumptions.COLUMN_PILL_ID);
            if(selection != null){
                sql.append(" where ");
                sql.append(" ").append(selection).append(" ");
            }
            //sql.append(" group by ");
            //sql.append(getTableName()).append(".").append(DatabaseContract.Pills._ID);
            //sql.append(" order by ");
            //sql.append(DatabaseContract.Consumptions.TABLE_NAME + "." + DatabaseContract.Consumptions.COLUMN_DATE_TIME);
            //sql.append(" desc ");
            Cursor c = db.rawQuery(sql.toString(), selectionArgs);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Pill pill = getFromCursor(c);
                pills.add(pill);
                c.moveToNext();
            }
            c.close();
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
        if(!_invalidateCache && _cache != null && _cache.size() > 0)
            return _cache;

        _cache = getList(null, null);
        return _cache;
    }

    private void notifyUpdated(){
        _invalidateCache = true;
        ConsumptionRepository.getSingleton(_context).notifyUpdated();
        Observer.getSingleton().notifyPillsUpdated();
    }
}
