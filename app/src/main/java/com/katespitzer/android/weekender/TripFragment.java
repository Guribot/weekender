package com.katespitzer.android.weekender;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class TripFragment extends Fragment {
    private Trip mTrip;
    private List<Place> mTripPlaces;
    private Button mAddPlaceButton;

    private static final String TAG = "TripFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID tripId = (UUID) getActivity().getIntent()
                .getSerializableExtra(TripActivity.EXTRA_TRIP_ID);
        mTrip = TripList.get(getActivity()).getTrip(tripId);

        getActivity().setTitle(mTrip.getTitle());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);

        mTripPlaces = PlaceList.get(getActivity()).getPlacesForTrip(mTrip);

        mAddPlaceButton = view.findViewById(R.id.add_place_button);
        mAddPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: add Place");
                Intent intent = PlaceCreateActivity.newIntent(getActivity(), mTrip.getId());
                startActivity(intent);
            }
        });

        return view;
    }
}
