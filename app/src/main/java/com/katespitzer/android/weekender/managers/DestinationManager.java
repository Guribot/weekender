package com.katespitzer.android.weekender.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.database.DatabaseHelper;
import com.katespitzer.android.weekender.database.DestinationCursorWrapper;
import com.katespitzer.android.weekender.database.DbSchema.DestinationTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/10/18.
 */

public class DestinationManager {
    private static DestinationManager sDestinationManager;
    // it may be unnecessary to declare the context, this is for potential future features
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static final String TAG = "DestinationManager";

    /**
     * Constructor: takes in Context and initializes database
     *
     * @param context
     */
    private DestinationManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DatabaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * returns singular DestinationManager
     *
     * @param context
     * @return
     */
    public static DestinationManager get(Context context) {
        Log.i(TAG, "in get()");

        if (sDestinationManager == null) {
            sDestinationManager = new DestinationManager(context);
        }
        return sDestinationManager;
    }


    /**
     * Takes in a UUID and returns the corresponding Destination
     *
     * @param id
     * @return
     */
    public Destination getDestination(UUID id) {
        Log.i(TAG, "in getDestination()");
        DestinationCursorWrapper cursor = queryDestinations(
                DestinationTable.Cols.UUID + " = ?",
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
            return cursor.getDestination();
        } finally {
            // close cursor!
            cursor.close();
        }

        // You Should Not Be Here
    }

    /**
     * Takes in a destination and adds it to the db
     *
     * @param destination
     */
    public void addDestination(Destination destination) {
        Log.i(TAG, "in addDestination()");
        ContentValues values = getContentValues(destination);

        long r = mDatabase.insert(DestinationTable.NAME, null, values);
    }

    /**
     * Takes in a destination and updates the corresponding entry in the db
     *
     * @param destination
     */
    public void updateDestination(Destination destination) {
        Log.i(TAG, "updateDestination()");
        String uuidString = destination.getId().toString();
        ContentValues values = getContentValues(destination);

        mDatabase.update(DestinationTable.NAME, values,
                DestinationTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }


    /**
     * Takes in a route and deletes it from the database (if it's present)
     *
     * @param destination
     */
    public void deleteDestination(Destination destination) {
        Log.i(TAG, "deleteDestination: " + destination);
        String uuidString = destination.getId().toString();

        mDatabase.delete(DestinationTable.NAME,
                DestinationTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    /**
     * Queries db with null query params (retrieving entire db)
     * iterates through db, parses row into Destination, and adds to
     * returned List<Destination>
     *
     * @return
     */
    public List<Destination> getDestinations() {
        Log.i(TAG, "getDestinations()");
        List<Destination> destinations = new ArrayList<>();

        // querying with null args = returns everything
        DestinationCursorWrapper cursor = queryDestinations(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                destinations.add(cursor.getDestination());
                cursor.moveToNext();
            }
        } finally {
            // always close your cursors!
            cursor.close();
        }

        return destinations;
    }

    /**
     * Takes a trip as a parameters and returns List of all of its Destinations
     * DESTINATION: This includes destinations belonging to child Places
     * @param route
     * @return
     */
    public List<Destination> getDestinationsForRoute(Route route){
        List<Destination> destinations = new ArrayList<>();
        String whereClause = DestinationTable.Cols.ROUTE_ID + " = " + route.getDbId();
//        String[] whereArgs = new String[] {String.valueOf(trip.getDbId())};
        DestinationCursorWrapper cursor = queryDestinations(whereClause, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                destinations.add(cursor.getDestination());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        route.setDestinations(destinations);
        return destinations;
    }

    /**
     * Returns the size of the database
     * does this work?
     *
     * @return
     */
    public int size() {
        Log.i(TAG, "size()");
        return getDestinations().size();
    }

    public void addDestinationToRoute(Destination destination, Route route) {
        Log.i(TAG, "addDestinationToRoute()");
        destination.setRouteId(route.getDbId());
        addDestination(destination);
    }

    /**
     * Converts provided Destination into equivalent ContentValues (for use in SQLite operations)
     *
     * @param destination
     * @return
     */
    private static ContentValues getContentValues(Destination destination) {
        ContentValues values = new ContentValues();
        values.put(DestinationTable.Cols.UUID, destination.getId().toString());
        values.put(DestinationTable.Cols.NAME, destination.getName());
        values.put(DestinationTable.Cols.GOOGLE_PLACE_ID, destination.getGooglePlaceId());
        values.put(DestinationTable.Cols.ROUTE_ID, destination.getRouteId());

        return values;
    }

    /**
     * Takes in query parameters and returns a
     * cursor wrapped in a DestinationCursorWrapper
     * effectively searching the db
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private DestinationCursorWrapper queryDestinations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DestinationTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DestinationCursorWrapper(cursor);
    }
}
