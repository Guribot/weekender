package com.katespitzer.android.weekender;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.katespitzer.android.weekender.adapters.SearchResultRecyclerViewAdapter;
import com.katespitzer.android.weekender.api.PlaceFetcher;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSearchResultInteractionListener}
 * interface.
 */
public class SearchResultFragment extends Fragment {

    private static final String ARG_QUERY = "query";
    private static final String ARG_TRIP_ID = "trip_id";

    private String mQuery;
    private Trip mTrip;
    private List<Place> mResults;
    private OnSearchResultInteractionListener mListener;
    private SearchResultRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final int MAX_SEARCH_RESULTS = 20;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchResultFragment newInstance(String query, UUID tripId) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        args.putSerializable(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuery = getArguments().getString(ARG_QUERY);
            UUID tripId = (UUID) getArguments().getSerializable(ARG_TRIP_ID);
            mTrip = TripManager.get(getActivity()).getTrip(tripId);

            new FetchPlacesTask(mQuery, mTrip).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresult_list, container, false);
        mResults = new ArrayList<>();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            updateUI();
        }
        return view;
    }

    // TODO: make this work
    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new SearchResultRecyclerViewAdapter(mResults, mListener);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setValues(mResults);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchResultInteractionListener) {
            mListener = (OnSearchResultInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDestinationInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchResultInteractionListener {
        void onSearchResultClicked(Place result);
    }


    private class FetchPlacesTask extends AsyncTask<Void, Void, List<Place>> {
        String mQuery;
        List<Place> mPlaces;
        Place mSelection;

        GeoDataClient mGeoDataClient;

        public FetchPlacesTask(String query, Trip trip) {
            mQuery = query;
            mPlaces = new ArrayList<>();
            mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        }

        @Override
        protected List<Place> doInBackground(Void... voids) {
            if (mQuery == null) {
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                String results = new PlaceFetcher().getPlaceData(mQuery);

                try {
                    jsonObject = new JSONObject(results);
                    jsonArray = jsonObject.getJSONArray("results");

                    int max = (jsonArray.length() > MAX_SEARCH_RESULTS ? MAX_SEARCH_RESULTS : jsonArray.length());

                    for (int i = 0; i < max; i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                        Place place = new Place();
                        place.setName(result.getString("name"));
                        place.setGooglePlaceId(result.getString("place_id"));
                        place.setAddress(result.getString("formatted_address"));

                        mPlaces.add(place);
                    }

                    return mPlaces;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            mResults = mPlaces;
            updateUI();
            super.onPostExecute(places);
        }
    }
}
