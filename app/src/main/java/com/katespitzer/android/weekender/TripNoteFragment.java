package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.katespitzer.android.weekender.adapters.NoteRecyclerViewAdapter;
import com.katespitzer.android.weekender.managers.NoteManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Trip;

import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Notes.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnNoteInteractionListener}
 * interface.
 */
public class TripNoteFragment extends Fragment {

    private List<Note> mNotes;
    private Trip mTrip;
    private NoteRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private static final String ARG_TRIP_ID = "trip-id";

    private int mColumnCount = 1;
    private OnNoteInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TripNoteFragment() {
    }

    public static TripNoteFragment newInstance(UUID tripId) {
        TripNoteFragment fragment = new TripNoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID tripId = (UUID) getArguments().getSerializable(ARG_TRIP_ID);

        TripManager tripManager = TripManager.get(getActivity());
        NoteManager noteManager = NoteManager.get(getActivity());

        mTrip = tripManager.getTrip(tripId);
        mNotes = noteManager.getNotesForTrip(mTrip);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.trip_note_list);

        updateUI();

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_notes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.trip_menu_add_note) {

            Intent intent = NoteFormActivity.newIntent(getActivity(), mTrip);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();

        mNotes = NoteManager.get(getActivity()).getNotesForTrip(mTrip);
        mAdapter.setNotes(mNotes);
        mAdapter.notifyDataSetChanged();

        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoteInteractionListener) {
            mListener = (OnNoteInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDestinationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NoteRecyclerViewAdapter(mNotes, mListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNoteInteractionListener {
        void onNoteClicked(Note note);
    }
}
