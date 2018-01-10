package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                Intent intent = PlaceCreateActivity.newIntent(context, mTrip.getId());
                startActivity(intent);
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Place place);
    }
}
