package com.katespitzer.android.weekender;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchResultFragment extends Fragment {

    private static final String ARG_QUERY = "query";
    private static final String ARG_TRIP_ID = "trip_id";

    private String mQuery;
    private Trip mTrip;
    private List<Place> mResults;
    private OnListFragmentInteractionListener mListener;
    private SearchResultRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final int MAX_SEARCH_RESULTS = 20;
    private static final String TAG = "SearchResultFragment";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchResultFragment newInstance(String query, UUID tripId) {
        Log.i(TAG, "newInstance: ");
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        args.putSerializable(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Log.i(TAG, "onCreate: has arguments");
            mQuery = getArguments().getString(ARG_QUERY);
            UUID tripId = (UUID) getArguments().getSerializable(ARG_TRIP_ID);
            mTrip = TripManager.get(getActivity()).getTrip(tripId);

            new FetchPlacesTask(mQuery, mTrip).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_searchresult_list, container, false);
        mResults = new ArrayList<>();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.i(TAG, "onCreateView: be here please");

//            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_route_recycler_view);
//            mAdapter = new DestinationRecyclerViewAdapter(mDestinations, mDestinationListener);
//            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            recyclerView.setAdapter(mAdapter);

            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            updateUI();
        }
        return view;
    }

    // TODO: make this work
    private void updateUI() {
        Log.i(TAG, "updateUI(), mResults is currently " + mResults);
        if (mAdapter == null) {
            Log.i(TAG, "updateUI: setting new adapter");
            mAdapter = new SearchResultRecyclerViewAdapter(mResults, mListener);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.i(TAG, "updateUI: updating adapter");
            // breakpoint
            mAdapter.setValues(mResults);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach: ");
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
        Log.i(TAG, "onDetach: ");
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
        void onListFragmentInteraction(Place result);
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
                        place.setGooglePlaceId(result.getString("place_id"));
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
            Log.i(TAG, "onPostExecute() " + places);
            mResults = mPlaces;
            updateUI();
            super.onPostExecute(places);
        }

//        private void getPhotos(String placeId) {
//            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
//            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                    // Get the list of photos.
//                    PlacePhotoMetadataResponse photos = task.getResult();
//                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
//                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                    // Get the first photo in the list.
//                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
//                    // Get the attribution text.
//                    CharSequence attribution = photoMetadata.getAttributions();
//                    // Get a full-size bitmap for the photo.
//                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
//                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
//                            PlacePhotoResponse photo = task.getResult();
//                            Bitmap bitmap = photo.getBitmap();
//                            Log.i(TAG, "onComplete: result found: \n bitmap: " + bitmap + "\n photo: " + photo);
//                        }
//                    });
//                }
//            });
//        }
    }
}
