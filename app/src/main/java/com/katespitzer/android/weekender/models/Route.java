package com.katespitzer.android.weekender.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/9/18.
 */

public class Route {

    private UUID mId;
    private List<Destination> mDestinations;
    private String mOverviewPolyline;
    private int mDbId;

    public Route() {
        this(UUID.randomUUID());
    }

    public Route(UUID uuid) {
        mId = uuid;
    }

    @Override
    public String toString() {
        return "Route " + mId.toString();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public List<Destination> getDestinations() {
        return mDestinations;
    }

    public void setDestinations(List<Destination> destinations) {
        mDestinations = destinations;
    }

    public String getOverviewPolyline() {
        return mOverviewPolyline;
    }

    public void setOverviewPolyline(String overviewPolyline) {
        mOverviewPolyline = overviewPolyline;
    }

    public void addTestData(){
        Destination seattle = new Destination();
        seattle.setName("Seattle");
        seattle.setGooglePlaceId("place_id:ChIJVTPokywQkFQRmtVEaUZlJRA");

        Destination portland = new Destination();
        portland.setName("Portland");
        portland.setGooglePlaceId("place_id:ChIJXc7PQCellFQRITpZ6kxP_hs");

        Destination cannonBeach = new Destination();
        cannonBeach.setName("Cannon Beach");
        cannonBeach.setGooglePlaceId("place_id:ChIJJ3SpfQsLlVQRkYXR9ua5Nhw");

        ArrayList<Destination> destinations = new ArrayList<>();
        destinations.add(seattle);
        destinations.add(portland);
        destinations.add(cannonBeach);

        setDestinations(destinations);
    }

    public int getDbId() {
        return mDbId;
    }

    public void setDbId(int dbId) {
        mDbId = dbId;
    }
}