package com.katespitzer.android.weekender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/9/18.
 */

public class Route {

    private UUID mId;
    private ArrayList<Destination> mDestinations;
    private int mTripId;
    private String mPolyline;

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public List<Destination> getDestinations() {
        return mDestinations;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        mDestinations = destinations;
    }

    public int getTripId() {
        return mTripId;
    }

    public void setTripId(int tripId) {
        mTripId = tripId;
    }

    public String getPolyline() {
        return mPolyline;
    }

    public void setPolyline(String polyline) {
        mPolyline = polyline;
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
}
