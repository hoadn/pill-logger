package uk.co.pilllogger.database;

import android.provider.BaseColumns;

/**
 * Created by root on 21/10/13.
 */
public final class DatabaseContract {

    //To stop accidentally instantiating the contract class I put an empty constructor
    private DatabaseContract() {}

    public static abstract class Pills implements BaseColumns {
        public static final String TABLE_NAME = "pills";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_COLOUR = "colour";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_UNITS = "units";
    }

    public static abstract class Consumptions implements BaseColumns {
        public static final String TABLE_NAME = "consumption";
        public static final String COLUMN_PILL_ID = "pill_id";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_GROUP = "group_id";
        public static final String INDEX_DATE_TIME = "idx_date_time";
    }

    public static abstract class Tutorials implements BaseColumns {
        public static final String TABLE_NAME = "tutorials";
        public static final String COLUMN_TAG = "pill_id";
    }

    public static abstract class CreateTables implements  BaseColumns {
        static String createTableSql = "create table if not exists ";

        public static final String CREATE_PILL_TABLE =
                String.format("%s%s (%s INTEGER PRIMARY KEY,%s TEXT, %s INTEGER,%s INTEGER, %s INTEGER, %s TEXT)",
                        createTableSql,
                        Pills.TABLE_NAME,
                        Pills._ID,
                        Pills.COLUMN_NAME,
                        Pills.COLUMN_SIZE,
                        Pills.COLUMN_COLOUR,
                        Pills.COLUMN_FAVOURITE,
                        Pills.COLUMN_UNITS);

        public static final String CREATE_CONSUMPTION_TABLE =
                String.format("%s%s (%s INTEGER PRIMARY KEY,%s INTEGER, %s LONG, %s TEXT)",
                        createTableSql,
                        Consumptions.TABLE_NAME,
                        Consumptions._ID,
                        Consumptions.COLUMN_PILL_ID,
                        Consumptions.COLUMN_DATE_TIME,
                        Consumptions.COLUMN_GROUP);

        public static final String CREATE_TUTORIAL_TABLE =
                String.format("%s%s (%s INTEGER PRIMARY KEY,%s TEXT)",
                        createTableSql,
                        Tutorials.TABLE_NAME,
                        Tutorials._ID,
                        Tutorials.COLUMN_TAG);
    }

    public static abstract class CreateIndicies implements BaseColumns{
        public static final String CREATE_CONSUMPTION_DATE_INDEX =
                String.format("create index %s on %s(%s);", Consumptions.INDEX_DATE_TIME, Consumptions.TABLE_NAME, Consumptions.COLUMN_DATE_TIME);
    }

    public static abstract class DeleteTables implements BaseColumns {
        public static final String DELETE_PILL_TABLE = "DROP TABLE IF EXISTS " + Pills.TABLE_NAME;
        public static final String DELETE_CONSUMPTION_TABLE = "DROP TABLE IF EXISTS " + Consumptions.TABLE_NAME;
        public static final String DELETE_TUTORIALS_TABLE = "DROP TABLE IF EXISTS " + Tutorials.TABLE_NAME;
    }

    public static abstract class DeleteIndicies implements BaseColumns{
        public static final String DELETE_CONSUMPTION_DATE_INDEX = "drop index if exists " + Consumptions.INDEX_DATE_TIME;
    }

    public static abstract class AlterTables implements BaseColumns{
        public static final String ADD_CONSUMPTIONS_GROUP_COLUMN = "ALTER TABLE "+Consumptions.TABLE_NAME+" ADD COLUMN " + Consumptions.COLUMN_GROUP + " TEXT DEFAULT ''";
    }
}