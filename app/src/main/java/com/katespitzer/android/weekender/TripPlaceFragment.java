package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TripPlaceFragment extends Fragment {

    private static final String TAG = "TripPlaceFragment";
//    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TRIP_ID = "trip-id";
    private static final int MAX_SEARCH_RESULTS = 5;

    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private PlaceRecyclerViewAdapter mAdapter;

    private Trip mTrip;
    private List<Place> mPlaces;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TripPlaceFragment() {
    }

    public static TripPlaceFragment newInstance(UUID tripId) {
        TripPlaceFragment fragment = new TripPlaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UUID tripId = (UUID) getArguments().getSerializable(ARG_TRIP_ID);
            mTrip = TripManager.get(getActivity()).getTrip(tripId);
            Log.i(TAG, "onCreate: Trip Found: " + mTrip);
            mPlaces = PlaceManager.get(getActivity()).getPlacesForTrip(mTrip);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);

        // Set the adapter

        final Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.trip_place_list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        mAdapter = new PlaceRecyclerViewAdapter(mPlaces, mListener);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton addButton = view.findViewById(R.id.trip_place_add_button);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = PlaceCreateActivity.newIntent(context, mTrip.getId());
//                startActivity(intent);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Place Search");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: input: " + input.getText());
                        String query = input.getText().toString();

                        new FetchPlacesTask(query).execute();
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

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();

        mPlaces = PlaceManager.get(getActivity()).getPlacesForTrip(mTrip);
        mAdapter.setPlaces(mPlaces);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach()");
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach()");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Place place);
    }

    private class FetchPlacesTask extends AsyncTask<Void, Void, List<Place>> {
        String mQuery;
        List<Place> mPlaces;
        Place mSelection;

        public FetchPlacesTask(String query) {
            mQuery = query;
            mPlaces = new ArrayList<>();
        }

        @Override
        protected List<Place> doInBackground(Void... voids) {
            if (mQuery == null) {
                Log.e(TAG, "doInBackground: No Query set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                String results = new PlaceFetcher().getPlaceData(mQuery);
                Log.i(TAG, "doInBackground: results returned: " + results);

                try {
                    jsonObject = new JSONObject(results);
                    jsonArray = jsonObject.getJSONArray("results");

                    int max = (jsonArray.length() > MAX_SEARCH_RESULTS ? MAX_SEARCH_RESULTS : jsonArray.length());

                    for (int i = 0; i < max; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                        Place place = new Place();
                        place.setName(result.getString("name"));
                        place.setAddress(result.getString("formatted_address"));

                        mPlaces.add(place);
                    }

                    return mPlaces;
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            Log.i(TAG, "onPostExecute: " + places);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Search Results");

            CharSequence[] placeNames = new CharSequence[]{"one", "two", "three"};

            builder.setSingleChoiceItems(placeNames, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "onClick: dialog is " + dialog);
                    Log.i(TAG, "onClick: which is " + which);
                    mSelection = mPlaces.get(which);
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "onClick: input: " + mSelection);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            super.onPostExecute(places);
        }
    }
}
