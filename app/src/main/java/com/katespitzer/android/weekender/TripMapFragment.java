package com.katespitzer.android.weekender;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.managers.RouteManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.models.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TripMapFragment extends Fragment {

    MapView mMapView;
    private Trip mTrip;
    private Route mRoute;
    private List<Place> mPlaces;
    private GoogleMap googleMap;

    private static final String TAG = "TripMapFragment";
    private static final String TRIP_ID = "trip_id";

    public static Fragment newInstance(UUID tripId) {
        Fragment fragment = new TripMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID tripId = (UUID) getArguments().getSerializable(TRIP_ID);
        mTrip = TripManager.get(getActivity()).getTrip(tripId);
        mRoute = RouteManager.get(getActivity()).getRoute(mTrip.getRouteId());
        mPlaces = PlaceManager.get(getActivity()).getPlacesForTrip(mTrip);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (int i = 0; i < mPlaces.size(); i++) {
                    Place place = mPlaces.get(i);
                    LatLng latlng = place.getLatLng();

                    boundsBuilder.include(latlng);

                    googleMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(place.getName()));
                }

                LatLngBounds bounds = boundsBuilder.build();

                PolylineOptions plo = mRoute.getPolylineOptions();

                if (plo != null) {
                    googleMap.addPolyline(mRoute.getPolylineOptions());
                }

                // For zooming automatically to the location of the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}