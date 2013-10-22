package uk.co.cntwo.pilllogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 21/10/13.
 */
public class DatabaseCreator extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PillLogger.db";

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.CreateTables.CREATE_PILL_TABLE);
        db.execSQL(DatabaseContract.CreateTables.CREATE_CONSUMPTION_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.DeleteTables.DELETE_PILL_TABLE);
        db.execSQL(DatabaseContract.DeleteTables.DELETE_CONSUMPTION_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
