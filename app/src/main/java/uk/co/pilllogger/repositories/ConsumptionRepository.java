package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.helpers.Logger;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;
import uk.co.pilllogger.state.Observer;

/**
 * Created by alex on 14/11/2013.
 */
public class ConsumptionRepository extends BaseRepository<Consumption>{
    private static ConsumptionRepository _instance;
    private Map<Integer, Map<Integer, Consumption>> _cache = new HashMap<Integer, Map<Integer, Consumption>>();
    private Map<Integer, Consumption> _consumptionsCache = new HashMap<Integer, Consumption>();
    private Map<String, Map<Integer, Consumption>> _groupConsumptionCache = new HashMap<String, Map<Integer, Consumption>>();

    private ConsumptionRepository(Context context) {
        super(context);
    }

    public static ConsumptionRepository getSingleton(Context context) {
        if (_instance == null) {
            _instance = new ConsumptionRepository(context);
        }
        return _instance;
    }

    @Override
    protected ContentValues getContentValues(Consumption consumption) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Consumptions.COLUMN_PILL_ID, consumption.getPillId());
        values.put(DatabaseContract.Consumptions.COLUMN_DATE_TIME, consumption.getDate().getTime());
        values.put(DatabaseContract.Consumptions.COLUMN_GROUP, consumption.getGroup());

        return values;
    }

    @Override
    protected String[] getProjection() {

        return new String[]{
                DatabaseContract.Consumptions._ID,
                DatabaseContract.Consumptions.COLUMN_PILL_ID,
                DatabaseContract.Consumptions.COLUMN_DATE_TIME,
                DatabaseContract.Consumptions.COLUMN_GROUP
        };
    }

    @Override
    protected Consumption getFromCursor(Cursor c) {
        return getFromCursor(c, null);
    }

    @Override
    protected String getTableName() {
        return DatabaseContract.Consumptions.TABLE_NAME;
    }

    private Consumption getFromCursor(Cursor c, Pill pill) {
        Consumption consumption = new Consumption();
        consumption.setId(c.getInt(c.getColumnIndex(DatabaseContract.Consumptions._ID)));
        consumption.setDate(new Date(c.getLong(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME))));
        consumption.setGroup(c.getString(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_GROUP)));
        int pillId = c.getInt(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_PILL_ID));

        if(pill == null){
            pill = PillRepository.getSingleton(_context).get(pillId);
        }

        consumption.setPill(pill);

        updateCaches(consumption);

        return consumption;
    }

    private void updateCaches(Consumption consumption){

        Map<Integer, Consumption> consumptionCache = new HashMap<Integer, Consumption>();
        if(_cache.containsKey(consumption.getPillId())) {
            consumptionCache = _cache.get(consumption.getPillId());
        }
        else{
            _cache.put(consumption.getPillId(), consumptionCache);
        }
        consumptionCache.put(consumption.getId(), consumption);

        Map<Integer, Consumption> groupCache = new HashMap<Integer, Consumption>();
        if(_groupConsumptionCache.containsKey(consumption.getGroup())) {
            groupCache = _groupConsumptionCache.get(consumption.getGroup());
        }
        else{
            _groupConsumptionCache.put(consumption.getGroup(), groupCache);
        }
        groupCache.put(consumption.getId(), consumption);

        _consumptionsCache.put(consumption.getId(), consumption);
    }

    @Override
    public long insert(Consumption consumption) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getContentValues(consumption);
        long newRowId = 0L;
        if (db != null) {
            newRowId = db.insert(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    null,
                    values);
        }
        consumption.setId((int) newRowId);
        notifyUpdated(consumption);
        updateCaches(consumption);
        return newRowId;
    }

    @Override
    public void update(Consumption data) {
        //Not needed yet
        throw new UnsupportedOperationException();
//        notifyUpdated();
    }

    @Override
    public void delete(Consumption consumption) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String id = String.valueOf(consumption.getId());

        if (db != null) {
            db.delete(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    "_ID = ?",
                    new String[]{id});
        }
        notifyDeleted(consumption);
    }

    public void deleteGroupPill(Consumption consumption){
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String pillId = String.valueOf(consumption.getPillId());

        if (db != null) {
            if(consumption.getGroup() != null){
                db.delete(
                        DatabaseContract.Consumptions.TABLE_NAME,
                        DatabaseContract.Consumptions.COLUMN_GROUP + " = ? AND " +
                        DatabaseContract.Consumptions.COLUMN_PILL_ID + " = ? ",
                        new String[]{consumption.getGroup(), pillId});
            }
            else{
                String dateTime = String.valueOf(consumption.getDate().getTime());
                db.delete(
                        DatabaseContract.Consumptions.TABLE_NAME,
                        DatabaseContract.Consumptions.COLUMN_DATE_TIME + " = ? AND " +
                                DatabaseContract.Consumptions.COLUMN_PILL_ID + " = ? ",
                        new String[]{dateTime, pillId});
            }
        }
        notifyDeletedGroupPill(consumption);
    }

    @Override
    public Consumption get(int id) {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

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

                c.moveToNext();
            }
            c.close();
        }
        return consumption;
    }

    public List<Consumption> getForPill(Pill pill) {
        if(_cache != null
            && _cache.size() > 0
            && _cache.containsKey(pill.getId())){
            return new ArrayList<Consumption>(_cache.get(pill.getId()).values());
        }

        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = getSortOrder();
        String selection = pill == null ? null : DatabaseContract.Consumptions.COLUMN_PILL_ID + " =?";
        String[] selectionArgs = pill == null ? null : new String[] { String.valueOf(pill.getId()) };
        List<Consumption> consumptions = new ArrayList<Consumption>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Consumption consumption = getFromCursor(c, pill); // we don't want to recursively cause ourselves trouble, we already have the pill
                consumption.setPill(pill);
                consumptions.add(consumption);
                c.moveToNext();
            }
            c.close();
        }

        return consumptions;
    }

    public List<Consumption> getForGroup(String group) {
        if(_groupConsumptionCache != null
                && _groupConsumptionCache.size() > 0
                && _groupConsumptionCache.containsKey(group)) {
            return new ArrayList<Consumption>(_groupConsumptionCache.get(group).values());
        }


        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = getSortOrder();
        String selection = group == null ? null : DatabaseContract.Consumptions.COLUMN_GROUP + " =?";
        String[] selectionArgs = group == null ? null : new String[] { group };
        List<Consumption> consumptions = new ArrayList<Consumption>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Consumption consumption = getFromCursor(c, null); // we don't want to recursively cause ourselves trouble, we already have the pill
                consumptions.add(consumption);
                c.moveToNext();
            }
            c.close();
        }

        return consumptions;
    }

    private String getSortOrder(){
        return DatabaseContract.Consumptions.COLUMN_DATE_TIME + " DESC" + ", " + DatabaseContract.Consumptions.COLUMN_PILL_ID + " ASC";
    }

    @Override
    public List<Consumption> getAll() {
        if(_consumptionsCache != null && _consumptionsCache.size() > 0) {
            ArrayList<Consumption> consumptions = new ArrayList<Consumption>(_consumptionsCache.values());
            Collections.sort(consumptions);
            return consumptions;
        }

        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = getSortOrder();
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

            if (c.moveToFirst() != false) {
                while (!c.isAfterLast()) {
                    Consumption consumption = getFromCursor(c);
                    consumptions.add(consumption);
                    c.moveToNext();
                }
            }
            c.close();
        }

        return consumptions;
    }

    public List<Consumption> groupConsumptions(List<Consumption> consumptions){
        if (consumptions.size() == 0)
            return consumptions;
        List<Consumption> grouped = new ArrayList<Consumption>();

        Consumption groupedConsumption = null;
        for(Consumption c : consumptions){
            DateTime groupedDate = null;
            if (groupedConsumption != null) {
                groupedDate = new DateTime(groupedConsumption.getDate()).withSecondOfMinute(0);
            }
            DateTime cDate = new DateTime(c.getDate()).withSecondOfMinute(0);
            if(groupedConsumption == null
                    || groupedDate.getMillis() != cDate.getMillis()
                    || groupedConsumption.getPillId() != c.getPillId())
            {
                if(groupedConsumption != null) {
                    grouped.add(groupedConsumption);
                }

                groupedConsumption = new Consumption(c);
            }
            else{
                groupedConsumption.setQuantity(groupedConsumption.getQuantity() + 1);
            }
        }

        grouped.add(groupedConsumption);

        return grouped;
    }

    public void notifyUpdated(Consumption consumption) {
        _consumptionsCache.put(consumption.getId(), consumption);
        if (_cache.size() > 0) {
            Map map = _cache.get(consumption.getPillId());
            if (map != null)
              map.put(consumption.getId(), consumption);
        }
        Observer.getSingleton().notifyConsumptionAdded(consumption);
    }

    private void notifyDeleted(Consumption consumption) {
        _consumptionsCache.remove(consumption.getId());
        _cache.get(consumption.getPillId()).remove(consumption.getId());
        Observer.getSingleton().notifyConsumptionDeleted(consumption);
    }

    private void notifyDeletedGroupPill(Consumption consumption){
        Map<Integer, Consumption> remove = _groupConsumptionCache.remove(consumption.getGroup());

        if(remove != null) {
            for (int key : remove.keySet()) {
                Consumption c = remove.get(key);
                _consumptionsCache.remove(key);

                _cache.get(c.getPillId()).remove(key);
            }
        }
        _consumptionsCache.remove(consumption.getId());
        Observer.getSingleton().notifyConsumptionPillGroupDeleted(consumption.getGroup(), consumption.getPillId());
    }
}
