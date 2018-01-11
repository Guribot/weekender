package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

public class TripRouteFragment extends Fragment {

    private Trip mTrip;
    private Route mRoute;
    private List<Destination> mDestinations;
    private TripManager mTripManager;

    private OnFragmentInteractionListener mListener;
    private DestinationRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mDestinationListener;

    private Button mAddDestinationButton;

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

        return view;
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
                    jsonObject = jsonObject.getJSONArray("results")
                            .getJSONObject(0);
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
                mDestination.setName(jsonObject.getString("name"));
                mDestination.setGooglePlaceId(jsonObject.getString("place_id"));

                DestinationManager.get(getActivity()).addDestinationToRoute(mDestination, mRoute);
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: Exception: ", e);
            }
            super.onPostExecute(jsonObject);
        }
    }

    private class FetchRouteTask extends AsyncTask<Void, Void, JSONObject> {
        Route mRoute;

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
                return jsonObject;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i(TAG, "onPostExecute: " + jsonObject);
            super.onPostExecute(jsonObject);
        }
    }
}
