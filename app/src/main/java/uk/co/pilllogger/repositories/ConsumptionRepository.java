package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 14/11/2013.
 */
public class ConsumptionRepository extends BaseRepository<Consumption>{
    private static ConsumptionRepository _instance;

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
        values.put(DatabaseContract.Consumptions.COLUMN_PILL_ID, consumption.get_pill_id());
        values.put(DatabaseContract.Consumptions.COLUMN_DATE_TIME, consumption.get_date().getTime());

        return values;
    }

    @Override
    protected String[] getProjection() {
        String[] projection = {
                DatabaseContract.Consumptions._ID,
                DatabaseContract.Consumptions.COLUMN_PILL_ID,
                DatabaseContract.Consumptions.COLUMN_DATE_TIME,
        };

        return projection;
    }

    @Override
    protected Consumption getFromCursor(Cursor c) {
        Consumption consumption = new Consumption();
        consumption.set_id(c.getInt(c.getColumnIndex(DatabaseContract.Consumptions._ID)));
        consumption.set_date(new Date(c.getLong(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_DATE_TIME))));
        int pillId = c.getInt(c.getColumnIndex(DatabaseContract.Consumptions.COLUMN_PILL_ID));
        Pill pill = PillRepository.getSingleton(_context).get(pillId);
        consumption.set_pill(pill);

        return consumption;
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
        return newRowId;
    }

    @Override
    public void update(Consumption data) {
        //Not needed yet
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Consumption consumption) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String id = String.valueOf(consumption.get_id());

        if (db != null) {
            db.delete(
                    DatabaseContract.Consumptions.TABLE_NAME,
                    "_ID = ?",
                    new String[]{id});
        }
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
        }
        return consumption;
    }

    @Override
    public List<Consumption> getAll() {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = DatabaseContract.Consumptions.COLUMN_DATE_TIME + " DESC";
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
                Consumption consumption = getFromCursor(c);
                consumptions.add(consumption);
                c.moveToNext();
            }
        }

        return consumptions;
    }

    public List<Consumption> groupConsumptions(List<Consumption> consumptions){
        List<Consumption> grouped = new ArrayList<Consumption>();

        Consumption groupedConsumption = null;
        for(Consumption c : consumptions){
            DateTime groupedDate = null;
            if (groupedConsumption != null) {
                groupedDate = new DateTime(groupedConsumption.get_date()).withSecondOfMinute(0);
            }
            DateTime cDate = new DateTime(c.get_date()).withSecondOfMinute(0);
            if(groupedConsumption == null
                    || groupedDate.getMillis() != cDate.getMillis()
                    || groupedConsumption.get_pill_id() != c.get_pill_id())
            {
                if(groupedConsumption != null)
                    grouped.add(groupedConsumption);

                groupedConsumption = c;
            }
            else{
                groupedConsumption.setQuantity(groupedConsumption.getQuantity() + 1);
            }
        }

        return grouped;
    }
}
