package com.katespitzer.android.weekender.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripRouteFragment.OnListFragmentInteractionListener;
import com.katespitzer.android.weekender.models.Destination;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Destination} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DestinationRecyclerViewAdapter extends RecyclerView.Adapter<DestinationRecyclerViewAdapter.ViewHolder> {

    private final List<Destination> mDestinations;
    private final OnListFragmentInteractionListener mListener;

    public DestinationRecyclerViewAdapter(List<Destination> destinations, OnListFragmentInteractionListener listener) {
        mDestinations = destinations;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mDestination = mDestinations.get(position);
        holder.mNameView.setText(mDestinations.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mDestination);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDestinations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public Destination mDestination;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.trip_destination_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
