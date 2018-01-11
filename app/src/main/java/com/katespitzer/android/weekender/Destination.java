package com.katespitzer.android.weekender;

import java.util.UUID;

/**
 * Created by kate on 1/9/18.
 */

public class Destination {

    private UUID mId;
    private String mName;
    private String mGooglePlaceId;
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

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        mRouteId = routeId;
    }
}
