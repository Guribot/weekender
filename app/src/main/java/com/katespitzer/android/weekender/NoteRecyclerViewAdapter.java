package com.katespitzer.android.weekender;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.TripNoteFragment.OnListFragmentInteractionListener;
import com.katespitzer.android.weekender.dummy.DummyContent;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {

    private final List<Note> mNotes;
    private final OnListFragmentInteractionListener mListener;

    public NoteRecyclerViewAdapter(List<Note> notes, OnListFragmentInteractionListener listener) {
        mNotes = notes;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note, parent, false);
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mNote);
                }g
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
        public final TextView mContentView;
        public final TextView mDateView;
        public Note mNote;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.note_title);
            mContentView = (TextView) view.findViewById(R.id.note_content_summary);
            mDateView = (TextView) view.findViewById(R.id.note_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
