package com.katespitzer.android.weekender.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.models.Trip;
import com.katespitzer.android.weekender.database.DatabaseHelper;
import com.katespitzer.android.weekender.database.TripCursorWrapper;
import com.katespitzer.android.weekender.database.DbSchema.TripTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 */

public class TripManager {
    private static TripManager sTripManager;
    // it may be unnecessary to declare the context, this is for potential future features
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private RouteManager mRouteManager;

    private static final String TAG = "TripManager";

    /**
     * Constructor: takes in Context and initializes database
     *
     * @param context
     */
    private TripManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DatabaseHelper(mContext)
                .getWritableDatabase();

        mRouteManager = RouteManager.get(context);
    }

    /**
     * returns singular TripManager
     *
     * @param context
     * @return
     */
    public static TripManager get(Context context) {
        Log.i(TAG, "in get()");

        if (sTripManager == null) {
            sTripManager = new TripManager(context);
        }
        return sTripManager;
    }


    /**
     * Takes in a UUID and returns the corresponding Trip
     *
     * @param id
     * @return
     */
    public Trip getTrip(UUID id) {
        Log.i(TAG, "in getTrip()");
        TripCursorWrapper cursor = queryTrips(
                TripTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                // if there are no results, return null
                return null;
            }
            // if there are results, return the first one
            // (since search param is UUID, There Can Only Be One
            cursor.moveToFirst();

            Trip trip = cursor.getTrip();
            trip.setRoute(mRouteManager.getRoute(trip.getRouteId()));

            return trip;
        } finally {
            // close cursor!
            cursor.close();
        }

        // You Should Not Be Here
    }

    /**
     * Takes in a trip and adds it to the db
     *
     * @param trip
     */
    public void addTrip(Trip trip) {
        Log.i(TAG, "in addTrip()");

        Route route = new Route();
        mRouteManager.addRoute(route);
        trip.setRoute(route);
        trip.setRouteId(route.getId());

        ContentValues values = getContentValues(trip);

        mDatabase.insert(TripTable.NAME, null, values);
    }

    /**
     * Takes in a trip and updates the corresponding entry in the db
     *
     * @param trip
     */
    public void updateTrip(Trip trip) {
        String uuidString = trip.getId().toString();
        ContentValues values = getContentValues(trip);

        mDatabase.update(TripTable.NAME, values,
                TripTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Queries db with null query params (retrieving entire db)
     * iterates through db, parses row into Trip, and adds to
     * returned List<Trip>
     *
     * @return
     */
    public List<Trip> getTrips() {
        List<Trip> trips = new ArrayList<>();

        // querying with null args = returns everything
        TripCursorWrapper cursor = queryTrips(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trips.add(cursor.getTrip());
                cursor.moveToNext();
            }
        } finally {
            // always close your cursors!
            cursor.close();
        }

        return trips;
    }

    public Route getRouteFor(Trip trip) {
        return RouteManager.get(mContext).getRoute(trip.getRouteId());
    }

    /**
     * Returns the size of the database
     * does this work?
     *
     * @return
     */
    public int size() {
        return getTrips().size();
    }

    /**
     * Converts provided Trip into equivalent ContentValues (for use in SQLite operations)
     *
     * @param trip
     * @return
     */
    private static ContentValues getContentValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(TripTable.Cols.UUID, trip.getId().toString());
        values.put(TripTable.Cols.TITLE, trip.getTitle());
        values.put(TripTable.Cols.START_DATE, trip.getStartDate().getTime());
        values.put(TripTable.Cols.END_DATE, trip.getEndDate().getTime());
        values.put(TripTable.Cols.ROUTE_ID, trip.getRouteId().toString());

        return values;
    }

    /**
     * Takes in query parameters and returns a
     * cursor wrapped in a TripCursorWrapper
     * effectively searching the db
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private TripCursorWrapper queryTrips(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TripTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new TripCursorWrapper(cursor);
    }

    /**
     * Seeds database with qty # of fake trips
     * (currently needs to be updated to work w/ db?)
     *
     * @param qty
     */
    private void populateTrip(int qty) {
        Log.i(TAG, "in populateTrip()");

        for (int i = 1; i <= qty; i++) {
            Trip trip = new Trip();
            trip.setTitle("Trip #" + i);
            trip.setStartDate(new Date());
            trip.setEndDate(new Date());

            addTrip(trip);
        }
    }
}
