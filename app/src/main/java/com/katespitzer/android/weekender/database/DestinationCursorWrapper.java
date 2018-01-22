package com.katespitzer.android.weekender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.database.DbSchema.DestinationTable;

import java.util.UUID;

/**
 * Created by kate on 1/10/18.
 */

public class DestinationCursorWrapper extends CursorWrapper {
    /**
     * Takes in a Cursor and returns a new instance of
     * a DestinationCursorWrapper
     *
     * @param cursor
     */
    public DestinationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Destination object built from
     * the Cursor used to instantiate the DestinationCursorWrapper
     *
     * @return
     */
    public Destination getDestination() {
        String uuidString = getString(getColumnIndex(DestinationTable.Cols.UUID));
        String name = getString(getColumnIndex(DestinationTable.Cols.NAME));
        String googlePlaceId = getString(getColumnIndex(DestinationTable.Cols.GOOGLE_PLACE_ID));
        double latitude = getDouble(getColumnIndex(DestinationTable.Cols.LAT));
        double longitude = getDouble(getColumnIndex(DestinationTable.Cols.LONG));
        int position = getInt(getColumnIndex(DestinationTable.Cols.POSITION));
        int routeId = getInt(getColumnIndex(DestinationTable.Cols.ROUTE_ID));

        Destination destination = new Destination(java.util.UUID.fromString(uuidString));
        destination.setName(name);
        destination.setGooglePlaceId(googlePlaceId);
        destination.setPosition(position);
        destination.setLatLng(latitude, longitude);
        destination.setRouteId(routeId);

        return destination;
    }
}