package com.katespitzer.android.weekender.database;

/**
 * Created by kate on 1/3/18.
 */

public class DbSchema {
    public static final class TripTable {
        public static final String NAME = "trips";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String START_DATE = "start_date";
            public static final String END_DATE = "end_date";
        }

        public static final String CREATE = "create table " + TripTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.TITLE + ", " +
                Cols.START_DATE + ", " +
                Cols.END_DATE +
                ")";

    }

    public static final class PlaceTable {
        public static final String NAME = "places";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TRIP_ID = "trip_id";
            public static final String NAME = "name";
            public static final String ADDRESS = "address";
            public static final String LONG = "longitude";
            public static final String LAT = "latitude";
            public static final String IMG = "imgUrl";
        }

        public static final String CREATE = "create table " + PlaceTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.TRIP_ID + " integer, " +
                Cols.NAME + ", " +
                Cols.ADDRESS + ", " +
                Cols.LONG + ", " +
                Cols.LAT + ", " +
                Cols.IMG + ", " +
                "FOREIGN KEY (" + Cols.TRIP_ID + ") REFERENCES " + TripTable.NAME + "(_id) )";

    }
}
