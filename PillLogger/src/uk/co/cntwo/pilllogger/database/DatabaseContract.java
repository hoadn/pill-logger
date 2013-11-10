package uk.co.cntwo.pilllogger.database;

import android.provider.BaseColumns;

/**
 * Created by root on 21/10/13.
 */
public final class DatabaseContract {

    //To stop accidentally instantiating the contract class I put an empty constructor
    public DatabaseContract() {}

    public static abstract class Pills implements BaseColumns {
        public static final String TABLE_NAME = "pills";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_COLOUR = "colour";
        public static final String COLUMN_FAVOURITE = "favourite";
    }

    public static abstract class Consumptions implements BaseColumns {
        public static final String TABLE_NAME = "consumption";
        public static final String COLUMN_PILL_ID = "pill_id";
        public static final String COLUMN_DATE_TIME = "date_time";
    }

    public static abstract class CreateTables implements  BaseColumns {

        public static final String CREATE_PILL_TABLE =
                "CREATE TABLE " + Pills.TABLE_NAME + " (" +
                Pills._ID + " INTEGER PRIMARY KEY," +
                Pills.COLUMN_NAME + " TEXT, " +
                Pills.COLUMN_SIZE + " INTEGER," +
                Pills.COLUMN_COLOUR + " INTEGER, " +
                Pills.COLUMN_FAVOURITE + " INTEGER" +
                ")";
        public static final String CREATE_CONSUMPTION_TABLE =
                "CREATE TABLE " + Consumptions.TABLE_NAME + " (" +
                Consumptions._ID + " INTEGER PRIMARY KEY," +
                Consumptions.COLUMN_PILL_ID + " INTEGER, " +
                Consumptions.COLUMN_DATE_TIME + " TEXT)";

    }

    public static abstract class DeleteTables implements BaseColumns {
        public static final String DELETE_PILL_TABLE = "DROP TABLE IF EXISTS " + Pills.TABLE_NAME;
        public static final String DELETE_CONSUMPTION_TABLE = "DROP TABLE IF EXISTS " + Consumptions.TABLE_NAME;
    }
}
