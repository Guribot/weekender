package com.katespitzer.android.weekender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.katespitzer.android.weekender.adapters.PlaceNoteRecyclerViewAdapter;
import com.katespitzer.android.weekender.dummy.DummyContent;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.models.Place;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private GeoDataClient mGeoDataClient;
    private Place mPlace;

    private TextView mPlaceName;
    private ImageView mPlaceImage;
    private TextView mPlaceAddress;
    private Button mAddNoteButton;
    private RecyclerView mRecyclerView;
    private PlaceManager mPlaceManager;
    


    private static final String TAG = "PlaceFragment";
    private static final String ARG_PLACE_ID = "place_id";

    public PlaceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param placeId
     * @return A new instance of fragment PlaceFragment.
     */
    public static PlaceFragment newInstance(UUID placeId) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPlaceManager = PlaceManager.get(getActivity());
        
        if (getArguments() != null) {
            // set mPlace
            UUID placeId = (UUID) getArguments().getSerializable(ARG_PLACE_ID);
            mPlace = mPlaceManager.getPlace(placeId);
        }

        setHasOptionsMenu(true);

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        // display place name
        mPlaceName = view.findViewById(R.id.place_name);
        mPlaceName.setText(mPlace.getName());

        // call setImageView
        mPlaceImage = view.findViewById(R.id.place_image);
        setImageView(mPlaceImage, mPlace.getGooglePlaceId());

        // display place address
        mPlaceAddress = view.findViewById(R.id.place_address);
        mPlaceAddress.setText(mPlace.getAddress());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_place_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.place_menu_delete_place) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(getString(R.string.delete_place_confirm, mPlace.getName()));
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPlaceManager.deletePlace(mPlace);

                    getActivity().onBackPressed();
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

        return super.onOptionsItemSelected(item);
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

        // use this for adding note?
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Takes in the target imageView and the google place ID,
     * finds first image and sets it to the image view
     *
     * @param imageView
     * @param placeId
     */

    private void setImageView(final ImageView imageView, String placeId) {
        Log.i(TAG, "setImageView: ");
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                if (photoMetadataBuffer.getCount() > 0) {
                    final PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            Log.i(TAG, "onComplete: ");

                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();

                            imageView.setImageBitmap(bitmap);

                            photoMetadataBuffer.release();

                            Log.i(TAG, "onComplete: result found: \n bitmap: " + bitmap + "\n photo: " + photo);
                        }
                    });
                }
            }
        });
    }
}
