package com.katespitzer.android.weekender.models;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by kate on 1/9/18.
 */

public class Destination implements Comparable {

    private UUID mId;
    private String mName;
    private String mGooglePlaceId;
    private int mPosition;
    private int mRouteId;

    public Destination() {
        this(UUID.randomUUID());
    }

    public Destination(UUID uuid) {
        mId = uuid;
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

    public String getGooglePlaceId() {
        return mGooglePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        mGooglePlaceId = googlePlaceId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        mRouteId = routeId;
    }

    @Override
    public int compareTo(@NonNull Object other) throws ClassCastException {
        if (other instanceof Destination) {
            return this.getPosition() - ((Destination) other).getPosition();
        } else {
            throw new ClassCastException("Destination expected");
        }
    }
}
