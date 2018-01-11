package com.katespitzer.android.weekender.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.SearchResultFragment.OnListFragmentInteractionListener;
import com.katespitzer.android.weekender.models.Place;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Place} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultRecyclerViewAdapter.ViewHolder> {
    
    private static final String TAG = "SrchResultRcyclrVwAdptr";

    private List<Place> mResults;
    private final OnListFragmentInteractionListener mListener;

    public SearchResultRecyclerViewAdapter(List<Place> items, OnListFragmentInteractionListener listener) {
        Log.i(TAG, "SearchResultRecyclerViewAdapter: ");
        mResults = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_searchresult, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        holder.mResult = mResults.get(position);
        holder.mIdView.setText(mResults.get(position).getName());
        holder.mContentView.setText(mResults.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mResult);
                }
            }
        });
    }
    
    public void setValues(List<Place> values) {
        Log.i(TAG, "setValues: ");
        mResults = values;
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: ");
        return mResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Place mResult;

        public ViewHolder(View view) {
            super(view);
            Log.i(TAG, "ViewHolder: ");
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.result_name);
            mContentView = (TextView) view.findViewById(R.id.result_address);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
