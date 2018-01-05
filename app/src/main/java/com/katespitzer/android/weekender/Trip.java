package com.katespitzer.android.weekender;

import android.util.Log;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 * Trip: Overall container of a single Trip
 */

public class Trip {
    private UUID mId;
    private String mTitle;
    private Date mStartDate;
    private Date mEndDate;

    private static final String TAG = "Trip";
//    private Route mRoute;
//    private PlaceList mPlaces;

    public Trip() {
        this(UUID.randomUUID());
    }

    public Trip(UUID id) {
        mId = id;
    }
    
    public void addPlace(Place place) {
        Log.i(TAG, "addPlace() - " + place);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }
}
