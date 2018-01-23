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
            public static final String ROUTE_ID = "route_id";
            public static final String TRIP_LENGTH = "trip_length";
        }

        public static final String CREATE = "create table " + TripTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.TITLE + ", " +
                Cols.START_DATE + ", " +
                Cols.END_DATE + ", " +
                Cols.ROUTE_ID + ", " +
                Cols.TRIP_LENGTH + ", " +
                "FOREIGN KEY (" + Cols.ROUTE_ID + ") REFERENCES " + RouteTable.NAME + "(_id) )";

    }

    public static final class PlaceTable {
        public static final String NAME = "places";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TRIP_ID = "trip_id";
            public static final String NAME = "name";
            public static final String ADDRESS = "address";
            public static final String IMAGE = "image";
            public static final String LAT = "latitude";
            public static final String LONG = "longitude";
            public static final String GOOGLE_PLACE_ID = "google_place_id";
        }

        public static final String CREATE = "create table " + PlaceTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.TRIP_ID + " integer, " +
                Cols.NAME + ", " +
                Cols.IMAGE + ", " +
                Cols.ADDRESS + ", " +
                Cols.LAT + ", " +
                Cols.LONG + ", " +
                Cols.GOOGLE_PLACE_ID + ", " +
                "FOREIGN KEY (" + Cols.TRIP_ID + ") REFERENCES " + TripTable.NAME + "(_id) )";

    }

    public static final class NoteTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String TRIP_ID = "trip_id";
            public static final String PLACE_ID = "place_id";
            public static final String CREATED_DATE = "created_date";
        }

        public static final String CREATE = "create table " + NoteTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.TITLE + ", " +
                Cols.CONTENT + ", " +
                Cols.CREATED_DATE + ", " +
                Cols.TRIP_ID + ", " +
                Cols.PLACE_ID + ", " +
                "FOREIGN KEY (" + Cols.TRIP_ID + ") REFERENCES " + TripTable.NAME + "(_id), " +
                "FOREIGN KEY (" + Cols.PLACE_ID + ") REFERENCES " + PlaceTable.NAME + "(_id) )";
    }

    public static final class RouteTable {
        public static final String NAME= "routes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String MAP_IMG = "map_image";
            public static final String OVERVIEW_POLYLINE = "overview_polyline";
        }

        public static final String CREATE = "create table " + RouteTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.MAP_IMG + ", " +
                Cols.OVERVIEW_POLYLINE +
                ")";
    }

    public static final class DestinationTable {
        public static final String NAME = "destinations";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String POSITION = "position";
            public static final String GOOGLE_PLACE_ID = "google_place_id";
            public static final String LAT = "latitude";
            public static final String LONG = "longitude";
            public static final String ROUTE_ID = "route_id";
        }

        public static final String CREATE = "create table " + DestinationTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                Cols.UUID + ", " +
                Cols.NAME + ", " +
                Cols.POSITION + ", " +
                Cols.GOOGLE_PLACE_ID + ", " +
                Cols.LAT + ", " +
                Cols.LONG + ", " +
                Cols.ROUTE_ID + ", " +
                "FOREIGN KEY (" + Cols.ROUTE_ID + ") REFERENCES " + RouteTable.NAME + "(_id) )";
    }
}
