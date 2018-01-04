package com.katespitzer.android.weekender;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class TripFragment extends Fragment {
    private Trip mTrip;

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

        return view;
    }
}
