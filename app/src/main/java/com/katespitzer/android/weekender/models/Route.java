package com.katespitzer.android.weekender.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
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
    private Bitmap mMapImage;
    private Polyline mPolyline;

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

    public Bitmap getMapImage() {
        return mMapImage;
    }

    public byte[] getMapImageByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mMapImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    public void setMapImage(Bitmap mapImage) {
        mMapImage = mapImage;
    }

    public void setMapImage(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        mMapImage = bitmap;
    }

    /**
     * method to decode polylines into LatLng, enabling a string to be converted into a Polyline object
     *
     * adapted from
     * https://reformatcode.com/code/android/find-the-closest-point-on-polygon-to-user-location
     * @return
     */
    public PolylineOptions getPolylineOptions(){
        if (mOverviewPolyline == null) {
            return null;
        }

        int len = mOverviewPolyline.length();

        final ArrayList<LatLng> path = new ArrayList<>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = mOverviewPolyline.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = mOverviewPolyline.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return new PolylineOptions().addAll(path).width(10).color(Color.argb(200, 26, 117, 237));
    }
}
