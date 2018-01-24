package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.katespitzer.android.weekender.adapters.DestinationRecyclerViewAdapter;
import com.katespitzer.android.weekender.api.DirectionsFetcher;
import com.katespitzer.android.weekender.api.MapFetcher;
import com.katespitzer.android.weekender.api.PlaceFetcher;
import com.katespitzer.android.weekender.managers.DestinationManager;
import com.katespitzer.android.weekender.managers.RouteManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.models.Trip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class TripRouteFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private Trip mTrip;
    private Route mRoute;
    private List<Destination> mDestinations;
    private TripManager mTripManager;
    private DestinationManager mDestinationManager;
    private Bitmap mRouteBitmap;

    private DestinationRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private OnDestinationInteractionListener mDestinationListener;

    private Button mAddDestinationButton;
    private ImageView mRouteImageView;
    private TextView mTripLengths;
    private ConstraintLayout mConstraintLayout;

    private static final String TRIP_ID = "trip_id";

    public TripRouteFragment() {
        // Required empty public constructor
    }


    public static TripRouteFragment newInstance(UUID tripId) {
        TripRouteFragment fragment = new TripRouteFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTripManager = TripManager.get(getActivity());

        if (getArguments() != null) {
            UUID tripId = (UUID) getArguments().getSerializable(TRIP_ID);
            mTrip = mTripManager.getTrip(tripId);
            mRoute = mTrip.getRoute();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_route, container, false);

        // Set the adapter
        final Context context = view.getContext();
        mDestinationManager = DestinationManager.get(getActivity());

        mTripLengths = view.findViewById(R.id.trip_length);
        String tripLengthsString = getString(R.string.route_length_format, mTrip.getTripLength(), mTrip.getDriveTimeString());
        mTripLengths.setText(tripLengthsString);

        mDestinationListener = new OnDestinationInteractionListener() {
            @Override
            public void onDestinationClicked(Destination destination) {
                updateUI();
            }

            @Override
            public void onNavClicked(final Destination destination) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                dialog.setTitle(R.string.open_in_google_maps);

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get the lat & lng of the marker
                        LatLng location = destination.getLatLng();

                        // instantiate an Intent for Google Maps turn-by-turn navigation
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.latitude + "," + location.longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        // send the user to google maps
                        startActivity(mapIntent);
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }

            @Override
            public void onUpArrowClicked(Destination destination) {
                mDestinationManager.moveDestinationUp(destination);

                updateUI();
                getRoute();
            }

            @Override
            public void onDownArrowClicked(Destination destination) {
                mDestinationManager.moveDestinationDown(destination);

                updateUI();
                getRoute();
            }
        };

        mRecyclerView = view.findViewById(R.id.trip_route_recycler_view);
        updateUI();

        // setting swipe callback for destination list
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        mConstraintLayout = view.findViewById(R.id.constraint_layout);

        mRouteImageView = view.findViewById(R.id.trip_route_map_holder);

        mAddDestinationButton = view.findViewById(R.id.trip_add_destination_button);
        mAddDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter Destination Name");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // create Destination object based on search input
                        Destination destination = new Destination();
                        destination.setName(input.getText().toString());
                        // begin AsyncTask to get Destination info
                        new FetchPlaceTask(destination, mRoute).execute();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        launchRoute();

        return view;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_route, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.trip_menu_add_note) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        // set mDestinations to the most up-to-date list of destinations for the current Route
        mDestinations = mDestinationManager
                .getDestinationsForRoute(mRoute);

        // why do I have to rebuild the recyclerview after every load??
        if (mAdapter == null) {
            mAdapter = new DestinationRecyclerViewAdapter(mDestinations, mDestinationListener);
        } else {
            mAdapter.setDestinations(mDestinations);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * If the Route already has a mapImage saved, it will display it.
     * If not, if the route has at least 2 destinations, it will find the route, save the image, and display it.
     */
    private void launchRoute() {
        if (mRoute.getMapImage() != null) {
            mRouteImageView.setImageBitmap(mRoute.getMapImage());
        } else {
            getRoute();
        }
    }

    private void getRoute() {
        if (mDestinations.size() > 1) {
            new FetchRouteTask(mTrip.getRoute())
                    .execute();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DestinationRecyclerViewAdapter.ViewHolder) {
            // get the swiped item name for Snack bar
            String name = mDestinations.get(viewHolder.getAdapterPosition()).getName();

            // backup item for Undo
            final Destination deletedDestination = mDestinations.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from the recycler view
            mAdapter.removeDestination(viewHolder.getAdapterPosition());
            mDestinationManager.deleteDestinationFromRoute(deletedDestination);

            // retrieve the updated map
            getRoute();

            // display a Snack bar with Undo option
            Snackbar snackbar = Snackbar.make(mConstraintLayout, name + " removed!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // undo the removal (restore item)
                    mAdapter.restoreDestination(deletedDestination, deletedIndex);
                    mDestinationManager.restoreDestinationToRoute(deletedDestination, deletedIndex);
                    // retrieve the updated map
                    getRoute();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    // update the route's saved Destination list to match the displayed list
                    mRoute.setDestinations(mDestinations);
                    // re-render the display (??? is this necessary?)
                    updateUI();

                    super.onDismissed(transientBottomBar, event);
                }
            });
            snackbar.show();
        }
    }

    public interface OnDestinationInteractionListener {
        void onDestinationClicked(Destination destination);
        void onUpArrowClicked(Destination destination);
        void onDownArrowClicked(Destination destination);
        void onNavClicked(Destination destination);
    }

    private class FetchPlaceTask extends AsyncTask<Void, Void, JSONObject> {
        Destination mDestination;
        Route mRoute;

        public FetchPlaceTask(Destination destination, Route route) {
            mDestination = destination;
            mRoute = route;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            if (mRoute == null) {
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                String results = new PlaceFetcher().getPlaceData(mDestination.getName());

                try {
                    jsonObject = new JSONObject(results);
                } catch (Exception e) {
                }

                return jsonObject;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                switch (jsonObject.getString("status")) {
                    case "OK":
                        try {
                            // retrieve result data
                            jsonObject = jsonObject.getJSONArray("results")
                                    .getJSONObject(0);

                            // build Destination around result data
                            mDestination.setName(jsonObject.getString("name"));
                            mDestination.setGooglePlaceId(jsonObject.getString("place_id"));

                            // set position; defaults to last
                            int position = mDestinations.size();
                            mDestination.setPosition(position);

                            // set lat & lng
                            JSONObject latlong = jsonObject.getJSONObject("geometry").getJSONObject("location");
                            double latitude = Double.parseDouble(latlong.getString("lat"));
                            double longitude = Double.parseDouble(latlong.getString("lng"));
                            mDestination.setLatLng(latitude, longitude);

                            // add Destination to database, set Route ID
                            mDestinationManager.restoreDestinationToRoute(mDestination, mRoute);

                            // update mDestinations based on DB, re-render list
                            updateUI();

                            // update map
                            getRoute();
                        } catch (Exception e) {
                            // something broke
                        }
                        super.onPostExecute(jsonObject);
                        break;
                    case "ZERO_RESULTS":
                        // display Toast to user
                        Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // something else broke
                }
            } catch (Exception e) {

            }

        }
    }

    private class FetchRouteTask extends AsyncTask<Void, Void, JSONObject> {
        Route mRoute;
        String mPolyline;

        public FetchRouteTask(Route route) {
            mRoute = route;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            if (mRoute == null) {
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                String results = new DirectionsFetcher().getDirections(mRoute);

                try {
                    jsonObject = new JSONObject(results);
                    jsonObject = jsonObject.getJSONArray("routes")
                            .getJSONObject(0);
                } catch (Exception e) {
                }

                try {
                    JSONArray legs = jsonObject.getJSONArray("legs");
                    HashMap info = getDistanceAndTime(legs);


                    // fortunately I have taken the Dual Casting feat
                    double distance = (double) info.get("distance");
                    mTrip.setTripLength((int) distance);

                    double time = (double) info.get("time");
                    mTrip.setDriveTime((int) time);

                    TripManager.get(getActivity()).updateTrip(mTrip);

                    mPolyline = jsonObject.getJSONObject("overview_polyline").getString("points");
                    mRoute.setOverviewPolyline(mPolyline);
                    RouteManager.get(getActivity()).updateRoute(mRoute);
                } catch (Exception e) {
                }

                mRouteBitmap = MapFetcher.getMapImage(mPolyline);

                return jsonObject;
            }
        }

        private HashMap getDistanceAndTime(JSONArray legs) {
            HashMap<String, Double> info = new HashMap<String, Double>();
            info.put("distance", 0.0);
            info.put("time", 0.0);

             try {
                 for (int i = 0; i < legs.length(); i++) {
                     // get current totals
                     double distance = info.get("distance");
                     double time = info.get("time");

                     // get current leg
                     JSONObject leg = legs.getJSONObject(i);

                     // get distance value
                     JSONObject distanceLeg = leg.getJSONObject("distance");
                     double newDistance = distanceLeg.getInt("value");
                     // convert from meters to miles
                     newDistance = newDistance / 1609.34;

                     // get time value
                     JSONObject timeLeg = leg.getJSONObject("duration");
                     double newTime = timeLeg.getInt("value");

                     // update current totals
                     newDistance = distance + newDistance;
                     newTime = time + newTime;

                     // update running totals
                     info.put("distance", newDistance);
                     info.put("time", newTime);
                 }
             } catch (Exception e) {
             }
             return info;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mRoute.setMapImage(mRouteBitmap);
            mRouteImageView.setImageBitmap(mRouteBitmap);

            String tripLengthsString = getString(R.string.route_length_format, mTrip.getTripLength(), mTrip.getDriveTimeString());
            mTripLengths.setText(tripLengthsString);

            super.onPostExecute(jsonObject);
        }
    }
}
