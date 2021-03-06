package com.katespitzer.android.weekender.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.katespitzer.android.weekender.database.DbSchema;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;
import com.katespitzer.android.weekender.database.DatabaseHelper;
import com.katespitzer.android.weekender.database.PlaceCursorWrapper;
import com.katespitzer.android.weekender.database.DbSchema.PlaceTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 */

public class PlaceManager {
    private static PlaceManager sPlaceManager;
    // it may be unnecessary to declare the context, this is for potential future features
    private static GeoDataClient sGeoDataClient;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Constructor: takes in Context and initializes database
     *
     * @param context
     */
    private PlaceManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DatabaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * returns singular PlaceManager
     *
     * @param context
     * @return
     */
    public static PlaceManager get(Context context) {
        if (sPlaceManager == null) {
            sPlaceManager = new PlaceManager(context);
        }

        if (sGeoDataClient == null) {
            sGeoDataClient = Places.getGeoDataClient(context, null);
        }

        return sPlaceManager;
    }


    /**
     * Takes in a UUID and returns the corresponding Place
     *
     * @param id
     * @return
     */
    public Place getPlace(UUID id) {
        PlaceCursorWrapper cursor = queryPlaces(
                PlaceTable.Cols.UUID + " = ?",
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
            Place place = cursor.getPlace();

            return place;
        } finally {
            // close cursor!
            cursor.close();
        }

        // You Should Not Be Here
    }

    /**
     * Takes in a place and adds it to the db
     *
     * @param place
     */
    public void addPlace(Place place) {
        ContentValues values = getContentValues(place);

        long r = mDatabase.insert(PlaceTable.NAME, null, values);
    }

    /**
     * Takes in a place and updates the corresponding entry in the db
     *
     * @param place
     */
    public void updatePlace(Place place) {
        String uuidString = place.getId().toString();
        ContentValues values = getContentValues(place);

        mDatabase.update(PlaceTable.NAME, values,
                PlaceTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Takes in a place and deletes it from the database (if it's present)
     *
     * @param place
     */
    public void deletePlace(Place place) {
        String uuidString = place.getId().toString();

        mDatabase.delete(DbSchema.PlaceTable.NAME,
                DbSchema.PlaceTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );

        NoteManager.get(mContext).deleteNotesForPlace(place);
    }

    /**
     * Queries db with null query params (retrieving entire db)
     * iterates through db, parses row into Place, and adds to
     * returned List<Place>
     *
     * @return
     */
    public List<Place> getPlaces() {
        List<Place> places = new ArrayList<>();

        // querying with null args = returns everything
        PlaceCursorWrapper cursor = queryPlaces(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                places.add(cursor.getPlace());
                cursor.moveToNext();
            }
        } finally {
            // always close your cursors!
            cursor.close();
        }

        return places;
    }

    /**
     * Takes a trip as a parameters and returns List of all of its Places it has
     * ALSO, WORKS!!!!
     * @param trip
     * @return
     */
    public List<Place> getPlacesForTrip(Trip trip){
        String whereString = "\"" + trip.getId() + "\"";

        List<Place> places = new ArrayList<>();
        String whereClause = PlaceTable.Cols.TRIP_ID + " = ?";
        String[] whereArgs = new String[] { trip.getId().toString() };
        PlaceCursorWrapper cursor = queryPlaces(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Place place = cursor.getPlace();

                places.add(place);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return places;
    }

    /**
     * Returns the size of the database
     * does this work?
     *
     * @return
     */
    public int size() {
        return getPlaces().size();
    }

    public void addPlaceToTrip(Place place, Trip trip) {
        place.setTripId(trip.getId());
        addPlace(place);
    }

    /**
     * Converts provided Place into equivalent ContentValues (for use in SQLite operations)
     *
     * @param place
     * @return
     */
    private static ContentValues getContentValues(Place place) {
        ContentValues values = new ContentValues();
        values.put(PlaceTable.Cols.UUID, place.getId().toString());
        values.put(PlaceTable.Cols.NAME, place.getName());
        values.put(PlaceTable.Cols.IMAGE, place.getBitmapByteArray());
        values.put(PlaceTable.Cols.ADDRESS, place.getAddress());
        values.put(PlaceTable.Cols.GOOGLE_PLACE_ID, place.getGooglePlaceId());
        values.put(PlaceTable.Cols.LAT, place.getLatLng().latitude);
        values.put(PlaceTable.Cols.LONG, place.getLatLng().longitude);
        values.put(PlaceTable.Cols.TRIP_ID, place.getTripId().toString());

        return values;
    }

    /**
     * Takes in query parameters and returns a
     * cursor wrapped in a PlaceCursorWrapper
     * effectively searching the db
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private PlaceCursorWrapper queryPlaces(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PlaceTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new PlaceCursorWrapper(cursor);
    }


}
