package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import hugo.weaving.DebugLog;
import timber.log.Timber;
import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.models.Note;
import uk.co.pilllogger.models.Pill;

/**
 * Created by Nicholas.Allen on 08/09/2014.
 */
public class NoteRepository extends BaseRepository<Note> {
    private static NoteRepository _instance;
    private final Provider<Pill> _pillProvider;

    @Inject
    public NoteRepository(Context context, Bus bus, Provider<Pill> pillProvider) {
        super(context, bus);
        _pillProvider = pillProvider;
    }

    @Override
    protected ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Notes.COLUMN_PILL_ID, note.getPillId());
        values.put(DatabaseContract.Notes.COLUMN_DATE_TIME, note.getDate().getTime());
        values.put(DatabaseContract.Notes.COLUMN_NOTE_TITLE, note.getTitle());
        values.put(DatabaseContract.Notes.COLUMN_CONTENT, note.getText());

        return values;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                DatabaseContract.Notes._ID,
                DatabaseContract.Notes.COLUMN_PILL_ID,
                DatabaseContract.Notes.COLUMN_DATE_TIME,
                DatabaseContract.Notes.COLUMN_NOTE_TITLE,
                DatabaseContract.Notes.COLUMN_CONTENT
        };
    }

    @Override
    protected Note getFromCursor(Cursor cursor) {
        return getFromCursor(cursor, null);
    }

    protected Note getFromCursor(Cursor cursor, Pill pill) {
        Note note = new Note();
        note.setId(getInt(cursor, DatabaseContract.Notes._ID));
        note.setDate(new Date(getLong(cursor, DatabaseContract.Notes.COLUMN_DATE_TIME)));
        note.setText(getString(cursor, DatabaseContract.Notes.COLUMN_CONTENT));
        note.setTitle(getString(cursor, DatabaseContract.Notes.COLUMN_NOTE_TITLE));
        int pillId = getInt(cursor ,DatabaseContract.Notes.COLUMN_PILL_ID);

        if(pill == null){
            pill = _pillProvider.get();
            pill.setId(pillId);
        }

        note.setPill(pill);

        return note;
    }

    @Override
    protected String getTableName() {
        return DatabaseContract.Notes.TABLE_NAME;
    }

    @Override @DebugLog
    public long insert(Note note) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        Timber.d("Going to insert note");

        ContentValues values = getContentValues(note);
        long newRowId = 0L;
        if (db != null) {
            newRowId = db.insert(
                    DatabaseContract.Notes.TABLE_NAME,
                    null,
                    values);
        }
        note.setId((int) newRowId);

        return newRowId;
    }

    @Override
    public void update(Note note) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = getContentValues(note);

        if(db != null){
            db.update(
                    DatabaseContract.Notes.TABLE_NAME,
                    values,
                    "_ID = ?",
                    new String[]{String.valueOf(note.getId())});

            Timber.d("Note updated");
        }
    }

    @Override
    public void delete(Note note) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        String id = String.valueOf(note.getId());

        if (db != null) {
            db.delete(
                    DatabaseContract.Notes.TABLE_NAME,
                    "_ID = ?",
                    new String[]{id});
        }
    }

    @Override
    public Note get(int id) {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String selection = DatabaseContract.Consumptions._ID + " =?";
        String[] selectionArgs = { String.valueOf(id) };
        Note note = new Note();
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
                note = getFromCursor(c, null);
                c.moveToNext();
            }
            c.close();
        }
        return note;
    }

    public List<Note> getForPill(Pill pill) {

        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = getSortOrder();
        String selection = pill == null ? null : DatabaseContract.Consumptions.COLUMN_PILL_ID + " =?";
        String[] selectionArgs = pill == null ? null : new String[] { String.valueOf(pill.getId()) };
        List<Note> notes = new ArrayList<Note>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Notes.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Note note = getFromCursor(c, pill); // we don't want to recursively cause ourselves trouble, we already have the pill
                note.setPill(pill);
                notes.add(note);
                c.moveToNext();
            }
            c.close();
        }

        return notes;
    }

    private String getSortOrder(){
        return DatabaseContract.Notes.COLUMN_DATE_TIME + " DESC" + ", " + DatabaseContract.Notes.COLUMN_PILL_ID + " ASC";
    }

    @Override
    public List<Note> getAll() {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();

        String sortOrder = getSortOrder();
        List<Note> notes = new ArrayList<Note>();
        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Notes.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    Note note = getFromCursor(c);
                    notes.add(note);
                    c.moveToNext();
                }
            }
            c.close();
        }

        return notes;
    }
}
