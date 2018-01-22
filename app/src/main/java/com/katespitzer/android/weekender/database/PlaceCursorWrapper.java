package com.katespitzer.android.weekender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.database.DbSchema.PlaceTable;

import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class PlaceCursorWrapper extends CursorWrapper {
    /**
     * Takes in a Cursor and returns a new instance of
     * a PlaceCursorWrapper
     *
     * @param cursor
     */
    public PlaceCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Place object built from
     * the Cursor used to instantiate the PlaceCursorWrapper
     * TODO: this does nothing with lat, long, image
     *
     * @return
     */
    public Place getPlace() {
        String uuidString = getString(getColumnIndex(PlaceTable.Cols.UUID));
        String name = getString(getColumnIndex(PlaceTable.Cols.NAME));
        String address = getString(getColumnIndex(PlaceTable.Cols.ADDRESS));
        String googlePlaceId = getString(getColumnIndex(PlaceTable.Cols.GOOGLE_PLACE_ID));
        byte[] image = getBlob(getColumnIndex(PlaceTable.Cols.IMAGE));
        double latitude = getDouble(getColumnIndex(PlaceTable.Cols.LAT));
        double longitude = getDouble(getColumnIndex(PlaceTable.Cols.LONG));
        int tripId = getInt(getColumnIndex(PlaceTable.Cols.TRIP_ID));
        int dbId = getInt(getColumnIndex("_id"));

        Place place = new Place(UUID.fromString(uuidString));
        place.setName(name);
        place.setAddress(address);
        place.setBitmap(image);
        place.setGooglePlaceId(googlePlaceId);
        place.setLatLng(latitude, longitude);
        place.setTripId(tripId);
        place.setDbId(dbId);

        return place;
    }
}
