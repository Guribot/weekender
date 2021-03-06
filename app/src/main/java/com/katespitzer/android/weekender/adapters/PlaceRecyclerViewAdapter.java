package com.katespitzer.android.weekender.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripPlaceFragment.OnPlaceInteractionListener;
import com.katespitzer.android.weekender.models.Place;

import java.util.List;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private final OnPlaceInteractionListener mListener;
    private GeoDataClient mGeoDataClient;

    public PlaceRecyclerViewAdapter(List<Place> places, OnPlaceInteractionListener listener) {
        mPlaces = places;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place_listitem, parent, false);

        mGeoDataClient = Places.getGeoDataClient(parent.getContext(), null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPlace = mPlaces.get(position);
        holder.mPlaceNameView.setText(mPlaces.get(position).getName());
        holder.mPlaceAddressView.setText(mPlaces.get(position).getAddress());
        
        // download Bitmap if it does not exist, display existing bitmap if it does
        if (mPlaces.get(position).getBitmap() == null) {
            setImageView(holder.mPlacePhotoView, mPlaces.get(position));
        } else {
            holder.mPlacePhotoView.setImageBitmap(mPlaces.get(position).getBitmap());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPlaceClicked(holder.mPlace);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPlaceNameView;
        public final TextView mPlaceAddressView;
        public final ImageView mPlacePhotoView;
        public Place mPlace;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPlaceNameView = (TextView) view.findViewById(R.id.place_name);
            mPlaceAddressView = (TextView) view.findViewById(R.id.place_address);
            mPlacePhotoView = (ImageView) view.findViewById(R.id.place_photo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPlaceAddressView.getText() + "'";
        }
    }

    public void setPlaces(List<Place> places) {
        mPlaces = places;
    }


    /**
     * From Google
     *
     */
    private void setImageView(final ImageView imageView, final Place place) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(place.getGooglePlaceId());
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
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
                        }
                    });
                }
            }
        });
    }
}
