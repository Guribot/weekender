package com.katespitzer.android.weekender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katespitzer.android.weekender.database.TripBaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 */

public class TripList {
    private static TripList sTripList;
    private List<Trip> mTrips;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static final String TAG = "TripList";

    private TripList(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TripBaseHelper(mContext)
                .getWritableDatabase();
        
        mTrips = new ArrayList<>();
//
        populateTrip(10);
    }

    public static TripList get(Context context) {
        Log.i(TAG, "in get()");

        if (sTripList == null) {
            sTripList = new TripList(context);
        }
        return sTripList;
    }

    private void populateTrip(int qty) {
        Log.i(TAG, "in populateTrip()");

        for (int i = 1; i <= qty; i++) {
            Trip trip = new Trip();
            trip.setTitle("Trip #" + i);
            trip.setStartDate(new Date());
            trip.setEndDate(new Date());

            mTrips.add(trip);
        }
    }

    public Trip getTrip(UUID id) {
        Log.i(TAG, "in getTrip()");
        for (Trip trip: mTrips) {
            if (trip.getId() == id) {
                return trip;
            }
        }
        return null;
    }

    public List<Trip> getTrips() {
        return mTrips;
    }

//    public int size(){
//        return mTrips.size();
//    }

//    public void addTrip(Trip trip) {
//        Log.i(TAG, "in addTrip()");
//        mTrips.add(trip);
//    }
}
