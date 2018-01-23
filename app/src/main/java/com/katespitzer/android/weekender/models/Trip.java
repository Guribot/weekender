package com.katespitzer.android.weekender.models;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 * Trip: Overall container of a single Trip
 */

public class Trip {
    private UUID mId;
    private UUID mRouteId;
    private Route mRoute;
    private String mTitle;
    private Date mStartDate;
    private Date mEndDate;

    private static final String TAG = "Trip";

    public Trip() {
        this(UUID.randomUUID());
    }

    public Trip(UUID id) {
        mId = id;
    }

    @Override
    public String toString() {
        return mTitle + " " + mId;
    }

    public UUID getRouteId() {
        return mRouteId;
    }

    public void setRouteId(UUID routeId) {
        mRouteId = routeId;
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

    public void setId(UUID id) {
        mId = id;
    }

    public Route getRoute() {
        return mRoute;
    }

    public void setRoute(Route route) {
        mRoute = route;
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
