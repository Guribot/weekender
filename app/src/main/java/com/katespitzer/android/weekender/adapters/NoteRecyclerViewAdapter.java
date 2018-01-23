package com.katespitzer.android.weekender.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.TripNoteFragment.OnListFragmentInteractionListener;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Note> mNotes;
    private final OnListFragmentInteractionListener mListener;

    private static final String TAG = "NoteRecyclerViewAdapter";

    public NoteRecyclerViewAdapter(List<Note> notes, OnListFragmentInteractionListener listener) {
        mNotes = notes;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mNote = mNotes.get(position);
        holder.mTitleView.setText(mNotes.get(position).getTitle());
        // TODO: implement getContentSnippet()
        holder.mContentView.setText(mNotes.get(position).getContent());
        Date noteDate = mNotes.get(position).getCreatedDate();
        String stringDate = DateFormat.getDateInstance(DateFormat.SHORT).format(noteDate);
        holder.mDateView.setText(stringDate);

        UUID placeId = mNotes.get(position).getPlaceId();
        Log.i(TAG, "onBindViewHolder: place id is " + placeId);
        if (placeId != null) {
            Place place = PlaceManager.get(mContext).getPlace(placeId);
            holder.mPlaceView.setText("from " + place.getName());
        } else {
            holder.mPlaceView.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onNoteClicked(holder.mNote);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mPlaceView;
        public final TextView mContentView;
        public final TextView mDateView;
        public Note mNote;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.note_title);
            mPlaceView = (TextView) view.findViewById(R.id.note_source);
            mContentView = (TextView) view.findViewById(R.id.note_content_summary);
            mDateView = (TextView) view.findViewById(R.id.note_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void setNotes(List<Note> notes) {
        mNotes = notes;
    }
}
