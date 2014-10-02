package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.R;
import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.events.LoadedPillsEvent;
import uk.co.pilllogger.events.UpdatedPillEvent;
import uk.co.pilllogger.models.Consumption;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;

/**
 * Created by alex on 14/11/2013.
 */
@Singleton
public class PillRepository extends BaseRepository<Pill>{

    ConsumptionRepository _consumptionRepository;

    private static final String TAG = "PillRepository";
    private static PillRepository _instance;
    private Map<Integer, Pill> _cache = new ConcurrentHashMap<Integer, Pill>();
    private boolean _getAllCalled = false;
    private Provider<Pill> _pillProvider;

    public boolean isCached(){
        return _cache != null && _cache.size() > 0 && _getAllCalled;
    }

    @Inject
    NoteRepository _noteRepository;

    @Inject
    public PillRepository(Context context, Bus bus, ConsumptionRepository consumptionRepository, Provider<Pill> pillProvider){
        super(context, bus);
        _consumptionRepository = consumptionRepository;
        _pillProvider = pillProvider;
    }

    @Produce @DebugLog
    public LoadedPillsEvent produceLoadedPills(){
        List<Pill> pills = new ArrayList<Pill>();

        if(isCached()){
            pills = getAll();
        }

        return new LoadedPillsEvent(pills);
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
    protected Pill getFromCursor(Cursor c){
        return getFromCursor(c, true);
    }

    protected Pill getFromCursor(Cursor c, boolean getConsumptions) {
        Pill pill = _pillProvider.get();
        pill.setId(getInt(c, DatabaseContract.Pills._ID));
        pill.setName(getString(c, DatabaseContract.Pills.COLUMN_NAME));
        pill.setSize(getFloat(c, DatabaseContract.Pills.COLUMN_SIZE));
        pill.setColour(getInt(c, DatabaseContract.Pills.COLUMN_COLOUR));
        pill.setUnits(getString(c, DatabaseContract.Pills.COLUMN_UNITS));

        int fav = getInt(c, DatabaseContract.Pills.COLUMN_FAVOURITE);
        pill.setFavourite(fav != 0);

        if(getConsumptions) {
            List<Consumption> consumptions = _consumptionRepository.getForPill(pill);
            pill.getConsumptions().addAll(consumptions);
        }

        _cache.put(pill.getId(), pill);

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
        pill.setId((int)newRowId);
        notifyUpdated(pill);
        Timber.d("inserted pill");
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

            Timber.d("Pill updated");
        }
        notifyUpdated(pill);
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

        List<Consumption> pillConsumptions = _consumptionRepository.getForPill(pill);
        for (Consumption consumption : pillConsumptions) {
            _consumptionRepository.delete(consumption);
        }
        notifyUpdated(pill, true);
    }


    @Override
    public Pill get(int id) {
        if(_cache != null
                && _cache.size() > 0
                && _cache.containsKey(id))
            return _cache.get(id);

        String selection = getTableName() + "." + DatabaseContract.Pills._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };

        return get(selection, selectionArgs);
    }

    private Pill get(String selection, String[] selectionArgs){
        List<Pill> pills = getList(selection, selectionArgs, true);

        return pills.size() == 0 ? null : pills.get(0);
    }

    private List<Pill> getList(String selection, String[] selectionArgs, boolean getConsumptions){
        SQLiteDatabase db = _dbCreator.getReadableDatabase();
        List<Pill> pills = new ArrayList<Pill>();
        if (db != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select ");
            sql.append(getSelectFromProjection());
            sql.append(" from ");
            sql.append(DatabaseContract.Pills.TABLE_NAME);
            if(selection != null){
                sql.append(" where ");
                sql.append(" ").append(selection).append(" ");
            }

            Cursor c = db.rawQuery(sql.toString(), selectionArgs);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Pill pill = getFromCursor(c, getConsumptions);
                pills.add(pill);
                c.moveToNext();
            }
            c.close();
        }

        List<Note> notes = _noteRepository.getAll();

        for (Note note : notes) {
            for (Pill pill : pills) {
                if(pill.getId() == note.getPillId()) {
                    pill.addNote(note);
                    break;
                }
            }
        }

        return pills;
    }

    public List<Pill> getFavouritePills(){
        String selection = DatabaseContract.Pills.COLUMN_FAVOURITE + " =?";
        String[] selectionArgs = {String.valueOf(1) };

        return getList(selection, selectionArgs, true);
    }

    @Override @DebugLog
    public List<Pill> getAll() {
        List<Pill> pills = getList(null, null, false);
        /*
        if(isCached())
            pills = new ArrayList<Pill>(_cache.values());
        else {*/
            _getAllCalled = true;
        /*    pills = getList(null, null, false);
        }*/

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        String sortOrder = preferences.getString(_context.getResources().getString(R.string.pref_key_medication_list_order), "");
        Boolean reverseOrder = preferences.getBoolean(_context.getResources().getString(R.string.pref_key_reverse_order), false);
        Comparator<Pill> comparator = null;
        if (sortOrder.equals(_context.getResources().getString(R.string.alphabetical))) {
            comparator = new Comparator<Pill>() {
                @Override
                public int compare(Pill pill1, Pill pill2) {
                    return pill1.getName().compareTo(pill2.getName());
                }
            };
        }
        else if (sortOrder.equals(_context.getResources().getString(R.string.order_created))) {
            comparator = new Comparator<Pill>() {
                @Override
                public int compare(Pill pill1, Pill pill2) {
                    return ((Integer)pill2.getId()).compareTo(pill1.getId());
                }
            };
        }
        else if (sortOrder.equals(_context.getResources().getString(R.string.last_taken))) {
            comparator = new Comparator<Pill>() {
                @Override
                public int compare(Pill pill1, Pill pill2) {
                    if(pill1.getLatestConsumption(_consumptionRepository) == null)
                        return 1;
                    if(pill2.getLatestConsumption(_consumptionRepository) == null)
                        return -1;
                    return pill2.getLatestConsumption(_consumptionRepository).getDate().compareTo(pill1.getLatestConsumption(_consumptionRepository).getDate());
                }
            };
        }
        else if (sortOrder.equals(_context.getResources().getString(R.string.most_taken))) {
            comparator = new Comparator<Pill>() {
                @Override
                public int compare(Pill pill1, Pill pill2) {
                    if(pill1.getLatestConsumption(_consumptionRepository) == null)
                        return 1;
                    if(pill2.getLatestConsumption(_consumptionRepository) == null)
                        return -1;
                    return (((Integer)pill2.getConsumptions().size()).compareTo(pill1.getConsumptions().size()));
                }
            };
        }

        if(comparator != null) {
            Collections.sort(pills, comparator);

            if(reverseOrder){
                Collections.reverse(pills);
            }
        }

        return pills;
    }

    private void notifyUpdated(Pill pill){
        notifyUpdated(pill, false);
    }

    @DebugLog
    private void notifyUpdated(Pill pill, boolean remove){
        if(remove)
            _cache.remove(pill.getId());
        else
            _cache.put(pill.getId(), pill);
    }
}
