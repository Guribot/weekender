package com.katespitzer.android.weekender;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaceDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceDetailFragment extends Fragment {
    private static final String ARG_PLACE_ID = "google_place_id";
    private static final String ARG_TRIP_ID = "trip_id";

    private OnFragmentInteractionListener mListener;

    private Place mPlace;
    private Trip mTrip;
    private String mGooglePlaceId;

    private GeoDataClient mGeoDataClient;

    private TextView mPlaceName;
    private ImageView mPlacePhoto;
    private TextView mPlaceAddress;
    private Button mAddButton;

    private static final String TAG = "PlaceDetailFragment";

    public PlaceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlaceDetailFragment.
     */
    public static PlaceDetailFragment newInstance(String googlePlaceId, UUID tripId) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, googlePlaceId);
        args.putSerializable(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGooglePlaceId = getArguments().getString(ARG_PLACE_ID);
            UUID tripId = (UUID) getArguments().getSerializable(ARG_TRIP_ID);
            mTrip = TripManager.get(getActivity()).getTrip(tripId);
        }

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);

        mPlaceName = view.findViewById(R.id.place_name);

        mPlacePhoto = view.findViewById(R.id.place_image);

        mPlaceAddress = view.findViewById(R.id.place_address);

        mAddButton = view.findViewById(R.id.place_add_button);

        mGeoDataClient.getPlaceById(mGooglePlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    com.google.android.gms.location.places.Place result = places.get(0);
                    Log.i(TAG, "Place found: " + result.getName());

                    mPlace = new Place();
                    mPlace.setName(result.getName().toString());
                    mPlace.setGooglePlaceId(mGooglePlaceId);
                    mPlace.setAddress(result.getAddress().toString());

                    places.release();

                    mPlaceName.setText(mPlace.getName());
                    mPlaceAddress.setText(mPlace.getAddress());

                    mAddButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlaceManager.get(getActivity()).addPlaceToTrip(mPlace, mTrip);
                            Toast.makeText(getActivity(), mPlace.getName() + " added to trip!", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    });
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });

        return view;
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
        void onFragmentInteraction(Uri uri);
    }


    private class FetchPlaceTask extends AsyncTask<Void, Void, Place> {
        String mGooglePlaceId;
        Place mResult;

        GeoDataClient mGeoDataClient;

        public FetchPlaceTask(String googlePlaceId) {
            mGooglePlaceId = googlePlaceId;
            mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        }

        @Override
        protected Place doInBackground(Void... voids) {
//            mGeoDataClient.getPlaceById(mGooglePlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                    if (task.isSuccessful()) {
//                        PlaceBufferResponse places = task.getResult();
//                        com.google.android.gms.location.Place myPlace = places.get(0);
//                        Log.i(TAG, "Place found: " + myPlace.getName());
//                        places.release();
//                    } else {
//                        Log.e(TAG, "Place not found.");
//                    }
//                }
//            });

//            if (mGooglePlaceId == null) {
//                Log.e(TAG, "doInBackground: No Google Place ID set");
//                return null;
//            } else {
//                JSONObject jsonObject = new JSONObject();
//                JSONArray jsonArray = new JSONArray();
////                String result = new PlaceFetcher().getPlaceDetails(mGooglePlaceId);
//
////                Log.i(TAG, "doInBackground: results returned: " + result);
//
//                try {
////                    jsonObject = new JSONObject(result);
//
//                    return mResult;
//                } catch (Exception e) {
//                    Log.d(TAG, "doInBackground: exception: " + e.toString());
//                    return null;
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Place result) {
            Log.i(TAG, "onPostExecute: " + result);
            super.onPostExecute(result);
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
