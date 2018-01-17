package com.katespitzer.android.weekender.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripRouteFragment.OnListFragmentInteractionListener;
import com.katespitzer.android.weekender.models.Destination;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Destination} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
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
                .inflate(R.layout.fragment_destination2, parent, false);
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

        public RelativeLayout mViewForeground;
        public RelativeLayout mViewBackground;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.trip_destination_name);
            mViewForeground = view.findViewById(R.id.view_foreground);
            mViewBackground = view.findViewById(R.id.view_background);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public void removeDestination(int position) {
        mDestinations.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreDestination(Destination destination, int position) {
        mDestinations.add(position, destination);
        notifyItemInserted(position);
    }
}
