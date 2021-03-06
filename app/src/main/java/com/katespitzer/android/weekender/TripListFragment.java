package com.katespitzer.android.weekender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Trip;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kate on 1/2/18.
 *
 * manages the logic for displaying the list of trips to the user
 */

public class TripListFragment extends Fragment {
    private RecyclerView mTripRecyclerView;
    private TripAdapter mAdapter;

    private TextView mEmptyMessage;
    private Button mNewTripButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        mTripRecyclerView = (RecyclerView) view.findViewById(R.id.trip_recycler_view);
        mTripRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyMessage = view.findViewById(R.id.empty_trip_message);
        mNewTripButton = view.findViewById(R.id.new_trip_button);
        mNewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TripFormActivity.class);
                startActivityForResult(intent, HomeActivity.NEW_TRIP_REQUEST);
            }
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        TripManager tripManager = TripManager.get(getActivity());
        List<Trip> trips = tripManager.getTrips();

        if (trips.size() > 0) {
            mEmptyMessage.setVisibility(View.GONE);
            mNewTripButton.setEnabled(false);
            mNewTripButton.setVisibility(View.GONE);
            mTripRecyclerView.setVisibility(View.VISIBLE);

            if (mAdapter == null) {
                mAdapter = new TripAdapter(trips);
                mTripRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setTrips(trips);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mTripRecyclerView.setVisibility(View.GONE);

            mEmptyMessage.setVisibility(View.VISIBLE);
            mNewTripButton.setEnabled(true);
            mNewTripButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     *  TRIP HOLDER
     */

    private class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mLengthsTextView;
        private TextView mDatesTextView;
        private Trip mTrip;

        public TripHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_trip, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.trip_list_item_title);
            mLengthsTextView = itemView.findViewById(R.id.trip_list_item_lengths);
            mDatesTextView = itemView.findViewById(R.id.trip_list_item_dates);
        }

        public void bind(Trip trip) {
            mTrip = trip;
            mTitleTextView.setText(mTrip.getTitle());

            Date from = mTrip.getStartDate();
            Date to = mTrip.getEndDate();
            String fromDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(from);
            String toDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(to);
            String dateString = getString(R.string.trip_dates_format,
                    fromDateString,
                    toDateString);


            mLengthsTextView.setText(getString(R.string.trip_lengths_format, mTrip.getDayLength(), mTrip.getTripLength()));

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