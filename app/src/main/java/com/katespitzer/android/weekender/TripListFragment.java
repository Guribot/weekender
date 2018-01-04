package com.katespitzer.android.weekender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by kate on 1/2/18.
 *
 * manages the logic for displaying the list of trips to the user
 */

public class TripListFragment extends Fragment {
    private RecyclerView mTripRecyclerView;
    private TripAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        mTripRecyclerView = (RecyclerView) view.findViewById(R.id.trip_recycler_view);
        mTripRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        TripList tripList = TripList.get(getActivity());
        List<Trip> trips = tripList.getTrips();

        if (mAdapter == null) {
            mAdapter = new TripAdapter(trips);
            mTripRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTrips(trips);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     *  TRIP HOLDER
     */

    private class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDatesTextView;
        private Trip mTrip;

        public TripHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_trip, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.trip_list_item_title);
            mDatesTextView = itemView.findViewById(R.id.trip_list_item_dates);
        }

        public void bind(Trip trip) {
            mTrip = trip;
            mTitleTextView.setText(mTrip.getTitle());
            String dateString = getString(R.string.trip_dates_format,
                    mTrip.getStartDate(),
                    mTrip.getEndDate());
            mDatesTextView.setText(dateString);
        }

        @Override
        public void onClick(View v) {
            Intent intent = TripActivity.newIntent(getActivity(), mTrip.getId());

            startActivity(intent);
        }
    }

    /**
     *  TRIP ADAPTER
     */

    private class TripAdapter extends RecyclerView.Adapter<TripHolder> {
        private List<Trip> mTrips;

        public TripAdapter(List<Trip> trips) {
            mTrips = trips;
        }

        @Override
        public TripHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new TripHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TripHolder holder, int position) {
            Trip trip = mTrips.get(position);
            holder.bind(trip);
        }

        @Override
        public int getItemCount() {
            return mTrips.size();
        }

        public void setTrips(List<Trip> trips) {
            mTrips = trips;
        }
    }
}