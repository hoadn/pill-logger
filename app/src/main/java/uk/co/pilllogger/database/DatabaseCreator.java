package uk.co.pilllogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 21/10/13.
 */
public class DatabaseCreator extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "PillLogger.db";

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.CreateTables.CREATE_PILL_TABLE);
        db.execSQL(DatabaseContract.CreateTables.CREATE_CONSUMPTION_TABLE);
        db.execSQL(DatabaseContract.CreateTables.CREATE_TUTORIAL_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 6:
                db.execSQL(DatabaseContract.CreateTables.CREATE_TUTORIAL_TABLE);
            case 7:
                // add alter SQL statement here
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(newVersion){
            case 7:
                db.execSQL(DatabaseContract.DeleteTables.DELETE_TUTORIALS_TABLE);
            case 6:
                // olden days
        }
    }
}
