package com.katespitzer.android.weekender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katespitzer.android.weekender.models.Trip;
import com.katespitzer.android.weekender.database.DbSchema.TripTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 *
 * This CursorWrapper allows custom functionality to be written
 * around cursors, such as parsing into Trip object (getTrip())
 */

public class TripCursorWrapper extends CursorWrapper {
    /**
     * Takes in a Cursor and returns a new instance of
     * a TripCursorWrapper
     *
     * @param cursor
     */
    public TripCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Trip object built from
     * the Cursor used to instantiate the TripCursorWrapper
     *
     * @return
     */
    public Trip getTrip() {
        String uuidString = getString(getColumnIndex(TripTable.Cols.UUID));
        String title = getString(getColumnIndex(TripTable.Cols.TITLE));
        long startDate = getLong(getColumnIndex(TripTable.Cols.START_DATE));
        long endDate = getLong(getColumnIndex(TripTable.Cols.END_DATE));
        int dbId = getInt(getColumnIndex("_id"));
        String routeId = getString(getColumnIndex(TripTable.Cols.ROUTE_ID));

        Trip trip = new Trip(UUID.fromString(uuidString));
        trip.setTitle(title);
        trip.setStartDate(new Date(startDate));
        trip.setEndDate(new Date(endDate));
        trip.setDbId(dbId);
        trip.setRouteId(UUID.fromString(routeId));

        return trip;
    }
}
