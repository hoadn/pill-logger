package uk.co.cntwo.pilllogger.database;

import android.provider.BaseColumns;

/**
 * Created by root on 21/10/13.
 */
public final class DatabaseContract {

    public static final String CREATE_STATEMENTS = CreateTables.CREATE_PILL_TABLE;
    public static final String DELETE_STATEMENTS = DeleteTables.DELETE_PILL_TABLE;
    //To stop accidentally instantiating the contract class I put an empty constructor
    public DatabaseContract() {}

    public static abstract class Pills implements BaseColumns {
        public static final String TABLE_NAME = "pills";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
    }

    public static abstract class CreateTables implements  BaseColumns {

        public static final String CREATE_PILL_TABLE =
                "CREATE TABLE " + Pills.TABLE_NAME + " (" +
                Pills._ID + " INTEGER PRIMARY KEY," +
                Pills.COLUMN_NAME + " TEXT, " +
                Pills.COLUMN_SIZE + " INTEGER)";

    }

    public static abstract class DeleteTables implements BaseColumns {
        public static final String DELETE_PILL_TABLE = "DROP TABLE IF EXISTS " + Pills.TABLE_NAME;
    }
}
