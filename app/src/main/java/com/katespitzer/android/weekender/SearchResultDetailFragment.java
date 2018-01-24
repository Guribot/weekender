package com.katespitzer.android.weekender;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultDetailFragment extends Fragment {
    private static final String ARG_PLACE_ID = "google_place_id";
    private static final String ARG_TRIP_ID = "trip_id";

    private Place mPlace;
    private Trip mTrip;
    private String mGooglePlaceId;

    private GeoDataClient mGeoDataClient;

    private TextView mPlaceName;
    private ImageView mPlacePhoto;
    private TextView mPlaceAddress;
    private Button mAddButton;

    public SearchResultDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchResultDetailFragment.
     */
    public static SearchResultDetailFragment newInstance(String googlePlaceId, UUID tripId) {
        SearchResultDetailFragment fragment = new SearchResultDetailFragment();
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

        View view = inflater.inflate(R.layout.fragment_searchresult_detail, container, false);

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
                    mPlace = new Place();
                    mPlace.setName(result.getName().toString());
                    mPlace.setGooglePlaceId(mGooglePlaceId);
                    mPlace.setAddress(result.getAddress().toString());
                    mPlace.setLatLng(result.getLatLng());

                    places.release();

                    mPlaceName.setText(mPlace.getName());
                    mPlaceAddress.setText(mPlace.getAddress());

                    setImageView(mPlacePhoto, mPlace);

                    mAddButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlaceManager.get(getActivity()).addPlaceToTrip(mPlace, mTrip);
                            Toast.makeText(getActivity(), mPlace.getName() + " added to trip!", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    });
                }
            }
        });

        return view;
    }

    /**
     * Takes in the target imageView and the google place ID,
     * finds first image and sets it to the image view
     *
     * @param imageView
     * @param place
     */

    private void setImageView(final ImageView imageView, final Place place) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(place.getGooglePlaceId());
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();

                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
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

                            imageView.setImageBitmap(bitmap);

                            place.setBitmap(bitmap);

                            photoMetadataBuffer.release();
                        }
                    });
                }
            }
        });
    }
}
