package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.katespitzer.android.weekender.adapters.PlaceRecyclerViewAdapter;
import com.katespitzer.android.weekender.api.PhotoFetcher;
import com.katespitzer.android.weekender.api.PlaceFetcher;
import com.katespitzer.android.weekender.managers.PlaceManager;
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

        updateUI();
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

    private void updateUI() {
        mPlaces = PlaceManager.get(getActivity()).getPlacesForTrip(mTrip);
        mAdapter.setPlaces(mPlaces);
        mAdapter.notifyDataSetChanged();
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

    private class FetchPlacesTask extends AsyncTask<Void, Void, String> {
        String mQuery;
        List<Place> mPlaces;
        List<CharSequence> mPlaceNames;
        Place mSelection;

        GeoDataClient mGeoDataClient;

        public FetchPlacesTask(String query) {
            mQuery = query;
            mPlaces = new ArrayList<>();
            mPlaceNames = new ArrayList<>();
            mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (mQuery == null) {
                Log.e(TAG, "doInBackground: No Query set");
                return null;
            } else {
                JSONObject jsonObject = new JSONObject();
//                JSONArray jsonArray = new JSONArray();
                String results = new PlaceFetcher().getPlaceData(mQuery);
                Log.i(TAG, "doInBackground: results returned: " + results);
//
                try {
                    jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    return status;
//                    jsonArray = jsonObject.getJSONArray("results");
//
//                    int max = (jsonArray.length() > MAX_SEARCH_RESULTS ? MAX_SEARCH_RESULTS : jsonArray.length());
//
//                    for (int i = 0; i < max; i++) {
//                        JSONObject result = jsonArray.getJSONObject(i);
//
//                        Place place = new Place();
//                        place.setName(result.getString("name"));
//                        place.setAddress(result.getString("formatted_address"));
//
//                        mPlaces.add(place);
//                        mPlaceNames.add(place.getName());
//                    }
//
//                    return mPlaces;
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception: " + e.toString());
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(String status) {
            Log.i(TAG, "onPostExecute: " + status);

            if (status.equals("OK")) {
                Log.i(TAG, "onPostExecute: status ok");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = SearchResultFragment.newInstance(mQuery, mTrip.getId());

                fm.beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            } else if (status.equals("ZERO_RESULTS")) {
                Log.i(TAG, "onPostExecute: no results");
                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onPostExecute: unexpected status: " + status);
            }
//            if (mPlaces.size() > 0) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Search Results");
//
//                CharSequence[] placeNames = mPlaceNames.toArray(new CharSequence[mPlaces.size()]);
//
//                builder.setSingleChoiceItems(placeNames, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i(TAG, "onClick: dialog is " + dialog);
//                        Log.i(TAG, "onClick: which is " + which);
//                        mSelection = mPlaces.get(which);
//                    }
//                });
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i(TAG, "onClick: input: " + mSelection);
//                        PlaceManager.get(getActivity()).addPlace(mSelection);
//                        Fragment fragment = SearchResultFragment.newInstance();
////                        PlaceManager.get(getActivity()).addPlaceToTrip(mSelection, mTrip);
////                        Toast.makeText(getActivity(), "Place Added", Toast.LENGTH_SHORT).show();
////                        Intent intent = PlaceCreateActivity.newIntent(getActivity(), mTrip.getId(), mSelection.getId());
////                        startActivity(intent);
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
//            } else {
//
//            }

            super.onPostExecute(status);
        }

        private void getPhotos(String placeId) {
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            Log.i(TAG, "onComplete: result found: \n bitmap: " + bitmap + "\n photo: " + photo);
                        }
                    });
                }
            });
        }
    }
}
