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
    private String mName;
    private String mAddress;
    private String mGooglePlaceId;
    private LatLng mLatLng;
    private UUID mTripId;

    private Bitmap mBitmap;

    public Place() {
        this(UUID.randomUUID());
    }

    public Place(UUID id) {
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

    public UUID getTripId() {
        return mTripId;
    }

    public void setTripId(UUID tripId) {
        mTripId = tripId;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latlng) {
        mLatLng = latlng;
    }

    public void setLatLng(double latitude, double longitude) {
        mLatLng = new LatLng(latitude, longitude);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public byte[] getBitmapByteArray() {
        if (mBitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setBitmap(byte[] bytes) {
        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mBitmap = bitmap;
        }
    }
}
