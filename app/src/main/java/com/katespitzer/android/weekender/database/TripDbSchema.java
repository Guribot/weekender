package com.katespitzer.android.weekender.database;

/**
 * Created by kate on 1/3/18.
 */

public class TripDbSchema {
    public static final class TripTable {
        public static final String NAME = "trips";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String START_DATE = "start_date";
            public static final String END_DATE = "end_date";
        }
    }
}
