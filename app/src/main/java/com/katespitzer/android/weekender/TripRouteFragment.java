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
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Toast;

import com.katespitzer.android.weekender.adapters.DestinationRecyclerViewAdapter;
import com.katespitzer.android.weekender.api.DirectionsFetcher;
import com.katespitzer.android.weekender.api.MapFetcher;
import com.katespitzer.android.weekender.api.PlaceFetcher;
import com.katespitzer.android.weekender.managers.DestinationManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Destination;
import com.katespitzer.android.weekender.models.Route;
import com.katespitzer.android.weekender.models.Trip;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripRouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class TripRouteFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private Trip mTrip;
    private Route mRoute;
    private List<Destination> mDestinations;
    private TripManager mTripManager;
    private Bitmap mRouteBitmap;

    private OnFragmentInteractionListener mListener;
    private DestinationRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mDestinationListener;

    private Button mAddDestinationButton;
    private ImageView mRouteImageView;
    private ConstraintLayout mConstraintLayout;

    private static final String TAG = "TripRouteFragment";
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
            mDestinations = DestinationManager.get(getActivity()).getDestinationsForRoute(mRoute);
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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_route_recycler_view);
        mAdapter = new DestinationRecyclerViewAdapter(mDestinations, mDestinationListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        // setting swipe callback for destination list
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
                        Log.i(TAG, "onClick: input: " + input.getText());
                        Destination destination = new Destination();
                        destination.setName(input.getText().toString());
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

        renderRoute();

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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * If the Route already has a mapImage saved, it will display it.
     * If not, if the route has at least 2 destinations, it will find the route, save the image, and display it.
     */
    private void renderRoute() {
        if (mRoute.getMapImage() != null) {
            mRouteImageView.setImageBitmap(mRoute.getMapImage());
        } else if (mRoute.getDestinations().size() > 1) {
            Log.i(TAG, "renderRoute: destinations found, rendering route");
            getRoute();
        }
    }

    private void getRoute() {
        new FetchRouteTask(mTrip.getRoute())
                .execute();
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

            // display a Snack bar with Undo option
            Snackbar snackbar = Snackbar.make(mConstraintLayout, name + " removed!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // undo the removal (restore item)
                    mAdapter.restoreDestination(deletedDestination, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    getRoute();
                    mRoute.setDestinations(mDestinations);
                    mAdapter.notifyDataSetChanged();
                    super.onDismissed(transientBottomBar, event);
                }
            });
            snackbar.show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Destination destination);
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
                Log.e(TAG, "doInBackground: No Destination set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                String results = new PlaceFetcher().getPlaceData(mDestination.getName());
                Log.i(TAG, "doInBackground: results returned: " + results);

                try {
                    jsonObject = new JSONObject(results);
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                }

                Log.i(TAG, "doInBackground: result is: " + results);
                return jsonObject;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: " + jsonObject);
            try {
                switch (jsonObject.getString("status")) {
                    case "OK":
                        try {
                            jsonObject = jsonObject.getJSONArray("results")
                                    .getJSONObject(0);

                            mDestination.setName(jsonObject.getString("name"));
                            mDestination.setGooglePlaceId(jsonObject.getString("place_id"));

                            DestinationManager.get(getActivity()).addDestinationToRoute(mDestination, mRoute);

                            mDestinations = mRoute.getDestinations();
                            mAdapter.notifyDataSetChanged();

                            getRoute();
                        } catch (Exception e) {
                            Log.e(TAG, "onPostExecute: Exception: ", e);
                        }
                        super.onPostExecute(jsonObject);
                        break;
                    case "ZERO_RESULTS":
                        Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.d(TAG, "onPostExecute: something weird happened: " + jsonObject.getString("status"));
                }
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: unexpected JSON response: ", e);
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
                Log.e(TAG, "doInBackground: No Route set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                String results = new DirectionsFetcher().getDirections(mRoute);
                Log.i(TAG, "doInBackground: results returned: " + results);

                try {
                    jsonObject = new JSONObject(results);
                    jsonObject = jsonObject.getJSONArray("routes")
                            .getJSONObject(0);
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                }

                Log.i(TAG, "doInBackground: result is: " + results);

                try {
                    mPolyline = jsonObject.getJSONObject("overview_polyline").getString("points");
                    Log.i(TAG, "onPostExecute: Polyline Found: " + mPolyline);
                    mRoute.setOverviewPolyline(mPolyline);
                } catch (Exception e) {
                    Log.e(TAG, "onPostExecute: exception", e);
                }

                mRouteBitmap = MapFetcher.getMapImage(mPolyline);
                Log.i(TAG, "doInBackground: " + mRouteBitmap);

                return jsonObject;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: " + jsonObject);

            mRoute.setMapImage(mRouteBitmap);
            mRouteImageView.setImageBitmap(mRouteBitmap);

            super.onPostExecute(jsonObject);
        }
    }
}
