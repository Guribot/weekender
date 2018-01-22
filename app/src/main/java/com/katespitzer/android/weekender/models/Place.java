package com.katespitzer.android.weekender.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class Place {
    private UUID mId;
    private int dbId;
    private String mName;
    private String mAddress;
    private String mGooglePlaceId;
    private double mLat;
    private double mLong;
    private LatLng mLatLng;
    private int mTripId;

    private Bitmap mBitmap;

    private static final String TAG = "Place.java";

    public Place() {
        this(UUID.randomUUID());
        Log.i(TAG, "Place: in constructor");
    }

    public Place(UUID id) {
        Log.i(TAG, "Place: in UUID constructor");
        mId = id;
    }

    @Override
    public String toString() {
        return mName + " " + mId;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
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

    public String getGooglePlaceId() {
        return mGooglePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        mGooglePlaceId = googlePlaceId;
    }

    public int getTripId() {
        return mTripId;
    }

    public void setTripId(int tripId) {
        mTripId = tripId;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(double latitude, double longitude) {
        mLat = latitude;
        mLong = longitude;
        mLatLng = new LatLng(latitude, longitude);
    }

    public double getLat() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public byte[] getBitmapByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        mBitmap = bitmap;
    }
}
