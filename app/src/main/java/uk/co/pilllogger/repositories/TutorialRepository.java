package uk.co.pilllogger.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import uk.co.pilllogger.database.DatabaseContract;
import uk.co.pilllogger.database.DatabaseCreator;

/**
 * Created by nick on 23/01/14.
 */
public class TutorialRepository {

    Context _context;
    private static TutorialRepository _instance;
    private DatabaseCreator _dbCreator;

    private TutorialRepository(Context context) {
        _context = context;
        _dbCreator = new DatabaseCreator(context);
    }

    public static TutorialRepository getSingleton(Context context) {
        if (_instance == null)
            _instance = new TutorialRepository(context);
        return _instance;
    }

    protected String[] getProjection() {
        String[] projection = {
                DatabaseContract.Tutorials._ID,
                DatabaseContract.Tutorials.COLUMN_TAG
        };

        return projection;
    }

    public boolean hasTutorialBeenSeen(String tag) {
        SQLiteDatabase db = _dbCreator.getReadableDatabase();

        String[] projection = getProjection();
        String selection = DatabaseContract.Tutorials.COLUMN_TAG + " =?";
        String[] selectionArgs = { tag };

        if (db != null) {
            Cursor c = db.query(
                    DatabaseContract.Tutorials.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                return true;
            }
            c.close();
        }
        return false;
    }

    public long tutorialSeen(String tag) {
        SQLiteDatabase db = _dbCreator.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Tutorials.COLUMN_TAG, tag);
        long newRowId = 0L;
        if (db != null) {
            newRowId = db.insert(
                    DatabaseContract.Tutorials.TABLE_NAME,
                    null,
                    values);
        }
        return newRowId;
    }
}
