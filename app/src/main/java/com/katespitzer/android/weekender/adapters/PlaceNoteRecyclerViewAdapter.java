package com.katespitzer.android.weekender.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.R;
import com.katespitzer.android.weekender.models.Note;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note}
 * TODO: Replace the implementation with code for your data type.
 */
public class PlaceNoteRecyclerViewAdapter extends RecyclerView.Adapter<PlaceNoteRecyclerViewAdapter.ViewHolder> {

    private final List<Note> mNotes;

    public PlaceNoteRecyclerViewAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placenote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mNote = mNotes.get(position);
        holder.mNoteTitle.setText(mNotes.get(position).getTitle());
        holder.mNoteContent.setText(mNotes.get(position).getContent());
        Date noteDate = mNotes.get(position).getCreatedDate();
        String stringDate = DateFormat.getDateInstance(DateFormat.SHORT).format(noteDate);
        holder.mNoteDate.setText(stringDate);
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNoteTitle;
        public final TextView mNoteContent;
        public final TextView mNoteDate;
        public Note mNote;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNoteTitle = (TextView) view.findViewById(R.id.note_title);
            mNoteContent = (TextView) view.findViewById(R.id.note_content_summary);
            mNoteDate = (TextView) view.findViewById(R.id.note_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNoteTitle.getText() + "'";
        }
    }
}
