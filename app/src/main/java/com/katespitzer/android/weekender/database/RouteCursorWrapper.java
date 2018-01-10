package com.katespitzer.android.weekender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katespitzer.android.weekender.Route;
import com.katespitzer.android.weekender.database.DbSchema.RouteTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/10/18.
 */

public class RouteCursorWrapper extends CursorWrapper {
    /**
     * Takes in a Cursor and returns a new instance of
     * a RouteCursorWrapper
     *
     * @param cursor
     */
    public RouteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Route object built from
     * the Cursor used to instantiate the RouteCursorWrapper
     *
     * @return
     */
    public Route getRoute() {
        String uuidString = getString(getColumnIndex(RouteTable.Cols.UUID));
        String overview_polyline = getString(getColumnIndex(RouteTable.Cols.OVERVIEW_POLYLINE));
        int dbId = getInt(getColumnIndex("_id"));

        Route route = new Route();
        route.setId(UUID.fromString(uuidString));
        route.setOverviewPolyline(overview_polyline);
        route.setDbId(dbId);

        return route;
    }
}
