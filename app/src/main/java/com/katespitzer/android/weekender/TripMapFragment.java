package com.katespitzer.android.weekender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.katespitzer.android.weekender.managers.DestinationManager;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.managers.RouteManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.models.Trip;

import java.util.List;
import java.util.UUID;

public class TripMapFragment extends Fragment {

    MapView mMapView;
    private Trip mTrip;
    private Route mRoute;
    private List<Place> mPlaces;
    private List<Destination> mDestinations;
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
        mDestinations = DestinationManager.get(getActivity()).getDestinationsForRoute(mRoute);
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

        updateMap();

        return rootView;
    }

    private void updateMap() {
        mRoute = RouteManager.get(getActivity()).getRoute(mTrip.getRouteId());
        mDestinations = DestinationManager.get(getActivity()).getDestinationsForRoute(mRoute);
        mPlaces = PlaceManager.get(getActivity()).getPlacesForTrip(mTrip);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    private int mSelection;

                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        Log.i(TAG, "onInfoWindowClick: " + marker);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(marker.getTitle());

                        CharSequence[] options = new CharSequence[]{
                                getString(R.string.view_place_details),
                                getString(R.string.open_in_google_maps),
                        };

                        dialog.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // View place details

                                        // retrieve the place ID from the marker tag
                                        UUID placeId = (UUID) marker.getTag();

                                        // instantiate an Intent to view the place details
                                        Intent intent = PlaceTabbedActivity.newIntent(getActivity(), placeId);

                                        // send the user to the place detail page
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        // Open in google maps

                                        // get the lat & lng of the marker
                                        LatLng location = marker.getPosition();

                                        // instantiate an Intent for Google Maps turn-by-turn navigation
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");

                                        // send the user to google maps
                                        startActivity(mapIntent);
                                        break;
                                    default:
                                        // if nothing is selected, close dialog with no action
                                        dialog.cancel();
                                        break;
                                }
                            }
                        });
                        dialog.show();
                    }
                });

                if (mPlaces != null && mPlaces.size() > 0 || mDestinations != null && mDestinations.size() > 0) {
                    // instantiate map bounds builder
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                    // add a marker for every place to the map, as well as including them in the map bounds
                    for (int i = 0; i < mPlaces.size(); i++) {
                        Place place = mPlaces.get(i);
                        LatLng latlng = place.getLatLng();

                        boundsBuilder.include(latlng);

                        googleMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(place.getName()))
                                .setTag(place.getId());
                    }

                    // include every destination in the map bounds
                    //    (this can potentially NOT display some of the route, but is
                    //     much faster than adding every polyline point)
                    for (int i = 0; i < mDestinations.size(); i++) {
                        Destination destination = mDestinations.get(i);
                        LatLng latlng = destination.getLatLng();

                        boundsBuilder.include(latlng);
                    }

                    // build the map bounds
                    LatLngBounds bounds = boundsBuilder.build();

                    // get the polyline from the route and if it exists, add to map
                    PolylineOptions plo = mRoute.getPolylineOptions();
                    if (plo != null) {
                        googleMap.addPolyline(mRoute.getPolylineOptions());
                    }

                    // zoom the map in to the bounds, with 400px padding
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                }
            }
        });
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