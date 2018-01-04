package com.katespitzer.android.weekender;

import android.util.Log;

import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class Place {
    private UUID mId;
    private String mName;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private int mTripId;
    private String mImageUrl;

    private static final String TAG = "Place.java";

    public Place() {
        Log.i(TAG, "Place: in constructor");
        this(UUID.randomUUID());
    }

    public Place(UUID id) {
        Log.i(TAG, "Place: in UUID constructor");
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public int getTripId() {
        return mTripId;
    }

    public void setTripId(int tripId) {
        mTripId = tripId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
