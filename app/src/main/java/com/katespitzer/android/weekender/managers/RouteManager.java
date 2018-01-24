package com.katespitzer.android.weekender.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.database.DatabaseHelper;
import com.katespitzer.android.weekender.database.RouteCursorWrapper;
import com.katespitzer.android.weekender.database.DbSchema.RouteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/10/18.
 */

public class RouteManager {
    private static RouteManager sRouteManager;
    // it may be unnecessary to declare the context, this is for potential future features
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Constructor: takes in Context and initializes database
     *
     * @param context
     */
    private RouteManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DatabaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * returns singular RouteManager
     *
     * @param context
     * @return
     */
    public static RouteManager get(Context context) {
        if (sRouteManager == null) {
            sRouteManager = new RouteManager(context);
        }
        return sRouteManager;
    }


    /**
     * Takes in a UUID and returns the corresponding Route
     *
     * @param id
     * @return
     */
    public Route getRoute(UUID id) {
        RouteCursorWrapper cursor = queryRoutes(
                RouteTable.Cols.UUID + " = ?",
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
            return cursor.getRoute();
        } finally {
            // close cursor!
            cursor.close();
        }

        // You Should Not Be Here
    }

    /**
     * Takes in a route and adds it to the db
     *
     * @param route
     */
    public void addRoute(Route route) {
        ContentValues values = getContentValues(route);

        long r = mDatabase.insert(RouteTable.NAME, null, values);
    }

    /**
     * Takes in a route and updates the corresponding entry in the db
     *
     * @param route
     */
    public void updateRoute(Route route) {
        String uuidString = route.getId().toString();
        ContentValues values = getContentValues(route);

        mDatabase.update(RouteTable.NAME, values,
                RouteTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Takes in a route and deletes it from the database (if it's present)
     *
     * @param route
     */
    public void deleteRoute(Route route) {
        String uuidString = route.getId().toString();

        mDatabase.delete(RouteTable.NAME,
                RouteTable.Cols.UUID + " = ?",
                new String[] {uuidString}
                );
    }

    /**
     * Queries db with null query params (retrieving entire db)
     * iterates through db, parses row into Route, and adds to
     * returned List<Route>
     *
     * @return
     */
    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();

        // querying with null args = returns everything
        RouteCursorWrapper cursor = queryRoutes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                routes.add(cursor.getRoute());
                cursor.moveToNext();
            }
        } finally {
            // always close your cursors!
            cursor.close();
        }

        return routes;
    }

    /**
     * Returns the size of the database
     * does this work?
     *
     * @return
     */
    public int size() {
        return getRoutes().size();
    }

    /**
     * Converts provided Route into equivalent ContentValues (for use in SQLite operations)
     *
     * @param route
     * @return
     */
    private static ContentValues getContentValues(Route route) {
        ContentValues values = new ContentValues();
        values.put(RouteTable.Cols.UUID, route.getId().toString());
        if (route.getMapImage() != null) {
            values.put(RouteTable.Cols.MAP_IMG, route.getMapImageByteArray());
        }
        values.put(RouteTable.Cols.OVERVIEW_POLYLINE, route.getOverviewPolyline());

        return values;
    }

    /**
     * Takes in query parameters and returns a
     * cursor wrapped in a RouteCursorWrapper
     * effectively searching the db
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private RouteCursorWrapper queryRoutes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RouteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new RouteCursorWrapper(cursor);
    }
}
