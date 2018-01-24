package com.katespitzer.android.weekender.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripRouteFragment.OnDestinationInteractionListener;
import com.katespitzer.android.weekender.models.Destination;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Destination} and makes a call to the
 * specified {@link OnDestinationInteractionListener}.
 */
public class DestinationRecyclerViewAdapter extends RecyclerView.Adapter<DestinationRecyclerViewAdapter.ViewHolder> {

    // Declarations

    private List<Destination> mDestinations;
    private Context mContext;
    private final OnDestinationInteractionListener mListener;

    private int mSelectedPos;

    private Drawable mUpOn;
    private Drawable mUpOff;
    private Drawable mDownOn;
    private Drawable mDownOff;

    private int mActiveColor;
    private int mDefaultColor;

    /**
     * public creator method
     * deselects list items, sets & sorts destination list (by position), sets listener
     *
     * @param destinations list of Destinations to display
     * @param listener listener for interface with TripRouteFragment to handle interaction with Destination menu
     */
    public DestinationRecyclerViewAdapter(List<Destination> destinations, OnDestinationInteractionListener listener) {
        // set selected destination to -1 (nonexistent)
        mSelectedPos = -1;

        // set and sort list of Destinations (by position attribute)
        mDestinations = destinations;
        Collections.sort(mDestinations);

        // set interaction listener
        mListener = listener;
    }

    /**
     * Inflates view & assigns View variables
     *
     * @param parent containing view object
     * @param viewType unused
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_destination2, parent, false);

        // save context for use in callbacks
        mContext = parent.getContext();

        // save up and down arrow images for use in callbacks
        Resources resources = mContext.getResources();
        mUpOn = resources.getDrawable(R.drawable.ic_up);
        mUpOff = resources.getDrawable(R.drawable.ic_up_disabled);
        mDownOn = resources.getDrawable(R.drawable.ic_down);
        mDownOff = resources.getDrawable(R.drawable.ic_down_disabled);

        // save active and inactive colors for use in callbacks
        mActiveColor = resources.getColor(R.color.colorAccent);
        mDefaultColor = resources.getColor(R.color.wallet_hint_foreground_holo_light);

        return new ViewHolder(view);
    }

    /**
     * Bind data to view
     *
     * @param holder view holder containing view
     * @param position index in list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // assign Destination model to view holder
        holder.mDestination = mDestinations.get(position);

        // set Name textview to Destination name
        holder.mNameView.setText(mDestinations.get(position).getName());

        // OnClick listener for list item
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    // if clicked destination is currently selected, deselect it
                    // if not, select it
                    mSelectedPos = (mSelectedPos == holder.getAdapterPosition()) ? -1 : holder.getAdapterPosition();

                    // notify TripRouteFragment of selection change
                    mListener.onDestinationClicked(holder.mDestination);
                }
            }
        });

        // if the current Destination is the selected one
        if (holder.mDestination.getPosition() == mSelectedPos) {
            // set the name to the active color
            holder.mNameView.setTextColor(mActiveColor);

            // when nav button is clicked
            holder.mNavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        // notify TripRouteFragment of click
                        mListener.onNavClicked(holder.mDestination);
                    }
                }
            });

            // if destination is first in the list
            if (holder.mDestination.getPosition() == 0) {
                // disable the up arrow and display disabled version of button
                holder.mUpArrow.setEnabled(false);
                holder.mUpArrow.setImageDrawable(mUpOff);
            } else { // if destination is not first
                // enable the up arrow and display enabled version of button
                holder.mUpArrow.setEnabled(true);
                holder.mUpArrow.setImageDrawable(mUpOn);
                // when up arrow is clicked
                holder.mUpArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            // notify TripRouteFragment of click
                            mListener.onUpArrowClicked(holder.mDestination);
                        }
                        // move selection up 1 spot
                        mSelectedPos = mSelectedPos - 1;
                    }
                });
            }

            // if destination is last in the list
            if (holder.mDestination.getPosition() == mDestinations.size() - 1) {
                // disable the down arrow and display disabled version of button
                holder.mDownArrow.setEnabled(false);
                holder.mDownArrow.setImageDrawable(mDownOff);
            } else { // if destination is not last
                // enable the down arrow and display enabled version of button
                holder.mDownArrow.setEnabled(true);
                holder.mDownArrow.setImageDrawable(mDownOn);
                // when down arrow is clicked
                holder.mDownArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            // notify TripRouteFragment of click
                            mListener.onDownArrowClicked(holder.mDestination);
                        }
                        // move selection down 1 spot
                        mSelectedPos = mSelectedPos + 1;
                    }
                });
            }
        } else { // if current destination is not selected
            // set the name to the default color
            holder.mNameView.setTextColor(mDefaultColor);

            // make menu invisible
            // OPTIMIZATION: could the entire menu be grouped together and enabled/hidden at once?
            holder.mNavButton.setEnabled(false);
            holder.mNavButton.setVisibility(View.INVISIBLE);
            holder.mUpArrow.setEnabled(false);
            holder.mUpArrow.setVisibility(View.INVISIBLE);
            holder.mDownArrow.setEnabled(false);
            holder.mDownArrow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDestinations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public Destination mDestination;
        public final ImageButton mNavButton;
        public final ImageButton mUpArrow;
        public final ImageButton mDownArrow;

        public RelativeLayout mViewForeground;
        public RelativeLayout mViewBackground;

        public ViewHolder(View view) {
            super(view);
            // set view
            mView = view;

            // assign View object variables
            mNameView = view.findViewById(R.id.trip_destination_name);
            mViewForeground = view.findViewById(R.id.view_foreground);
            mViewBackground = view.findViewById(R.id.view_background);

            mNavButton = view.findViewById(R.id.nav_icon);

            mUpArrow = view.findViewById(R.id.up_icon);
            mDownArrow = view.findViewById(R.id.down_icon);
        }

        /**
         * add name of Destination to String
         * @return
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    /**
     * Removes Destination at position from list of Destinations and notifies dataset of removal
     * @param position position on list of removed Destination
     */
    public void removeDestination(int position) {
        mDestinations.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Adds Destination at position and notifies dataset of addition
     * @param destination Destination to be restored
     * @param position position on list of removed Destination
     */
    public void restoreDestination(Destination destination, int position) {
        mDestinations.add(position, destination);
        notifyItemInserted(position);
    }

    /**
     * sorts provided list and sets it to the list of Destinations
     * @param destinations new list of Destinations to render
     */
    public void setDestinations(List<Destination> destinations) {
        Collections.sort(destinations);
        mDestinations = destinations;
    }
}
