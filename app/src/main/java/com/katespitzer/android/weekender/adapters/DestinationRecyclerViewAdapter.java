package com.katespitzer.android.weekender.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripRouteFragment.OnDestinationListItemInteractionListener;
import com.katespitzer.android.weekender.models.Destination;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Destination} and makes a call to the
 * specified {@link OnDestinationListItemInteractionListener}.
 */
public class DestinationRecyclerViewAdapter extends RecyclerView.Adapter<DestinationRecyclerViewAdapter.ViewHolder> {

    private List<Destination> mDestinations;
    private Context mContext;
    private final OnDestinationListItemInteractionListener mListener;

    private Drawable mUpOn;
    private Drawable mUpOff;
    private Drawable mDownOn;
    private Drawable mDownOff;

    private static final String TAG = "DestintnRcyclrVwAdptr";

    public DestinationRecyclerViewAdapter(List<Destination> destinations, OnDestinationListItemInteractionListener listener) {
        mDestinations = destinations;
        Collections.sort(mDestinations);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_destination2, parent, false);

        mContext = parent.getContext();

        Resources resources = mContext.getResources();
        mUpOn = resources.getDrawable(R.drawable.ic_up);
        mUpOff = resources.getDrawable(R.drawable.ic_up_disabled);
        mDownOn = resources.getDrawable(R.drawable.ic_down);
        mDownOff = resources.getDrawable(R.drawable.ic_down_disabled);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mDestination = mDestinations.get(position);
        holder.mNameView.setText(mDestinations.get(position).getName());

        if (holder.mDestination.getPosition() == 0) {
            holder.mUpArrow.setEnabled(false);
            holder.mUpArrow.setImageDrawable(mUpOff);
        } else {
            holder.mUpArrow.setEnabled(true);
            holder.mUpArrow.setImageDrawable(mUpOn);
            holder.mUpArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: up arrow");
                    if (mListener != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onUpArrowClicked(holder.mDestination);
                    }
                }
            });
        }

        if (holder.mDestination.getPosition() == mDestinations.size() - 1) {
            holder.mDownArrow.setEnabled(false);
            holder.mDownArrow.setImageDrawable(mDownOff);
        } else {
            holder.mDownArrow.setEnabled(true);
            holder.mDownArrow.setImageDrawable(mDownOn);
            holder.mDownArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: down arrow");
                    if (mListener != null) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onDownArrowClicked(holder.mDestination);
                    }
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onArrowClicked(holder.mDestination);
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
        public final ImageButton mUpArrow;
        public final ImageButton mDownArrow;

        public RelativeLayout mViewForeground;
        public RelativeLayout mViewBackground;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.trip_destination_name);
            mViewForeground = view.findViewById(R.id.view_foreground);
            mViewBackground = view.findViewById(R.id.view_background);

            mUpArrow = view.findViewById(R.id.up_icon);
            mDownArrow = view.findViewById(R.id.down_icon);
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

    public void setDestinations(List<Destination> destinations) {
        Collections.sort(destinations);
        mDestinations = destinations;
    }
}
