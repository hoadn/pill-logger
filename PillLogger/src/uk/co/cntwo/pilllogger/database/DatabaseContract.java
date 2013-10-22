package uk.co.cntwo.pilllogger.database;

import android.provider.BaseColumns;

/**
 * Created by root on 21/10/13.
 */
public final class DatabaseContract {

    public static final String CREATE_STATEMENTS = CreateTables.CREATE_PILL_TABLE + ";" + CreateTables.CREATE_CONSUMPTION_TABLE;
    public static final String DELETE_STATEMENTS = DeleteTables.DELETE_PILL_TABLE + ";" + DeleteTables.DELETE_CONSUMPTION_TABLE;
    //To stop accidentally instantiating the contract class I put an empty constructor
    public DatabaseContract() {}

    public static abstract class Pills implements BaseColumns {
        public static final String TABLE_NAME = "pills";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
    }

    public static abstract class Consumption implements BaseColumns {
        public static final String TABLE_NAME = "consumption";
        public static final String COLUMN_PILL_ID = "pill_id";
        public static final String COLUMN_DATE_TIME = "date_time";
    }

    public static abstract class CreateTables implements  BaseColumns {

        public static final String CREATE_PILL_TABLE =
                "CREATE TABLE " + Pills.TABLE_NAME + " (" +
                Pills._ID + " INTEGER PRIMARY KEY," +
                Pills.COLUMN_NAME + " TEXT, " +
                Pills.COLUMN_SIZE + " INTEGER)";
        public static final String CREATE_CONSUMPTION_TABLE =
                "CREATE TABLE " + Consumption.TABLE_NAME + " (" +
                Consumption._ID + " INTEGER PRIMARY KEY," +
                Consumption.COLUMN_PILL_ID + " INTEGER, " +
                Consumption.COLUMN_DATE_TIME + " TEXT)";

    }

    public static abstract class DeleteTables implements BaseColumns {
        public static final String DELETE_PILL_TABLE = "DROP TABLE IF EXISTS " + Pills.TABLE_NAME;
        public static final String DELETE_CONSUMPTION_TABLE = "DROP TABLE IF EXISTS " + Consumption.TABLE_NAME;
    }
}
