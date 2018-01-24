package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.katespitzer.android.weekender.adapters.PlaceNoteRecyclerViewAdapter;
import com.katespitzer.android.weekender.managers.NoteManager;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;

import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class PlaceNoteFragment extends Fragment {

    private Place mPlace;
    private List<Note> mNotes;
    private RecyclerView mRecyclerView;
    private Context mContext;

    private static final String ARG_PLACE_ID = "place_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceNoteFragment() {
    }

    @SuppressWarnings("unused")
    public static PlaceNoteFragment newInstance(UUID placeId) {
        PlaceNoteFragment fragment = new PlaceNoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UUID placeId = (UUID) getArguments().getSerializable(ARG_PLACE_ID);
            mPlace = PlaceManager.get(getActivity()).getPlace(placeId);
            mNotes = NoteManager.get(getActivity()).getNotesForPlace(mPlace);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placenote_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            mContext = view.getContext();
            mRecyclerView = (RecyclerView) view;
            updateUI();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_place_notes, menu);    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.place_menu_add_note) {

            Intent intent = NoteFormActivity.newIntent(getActivity(), mPlace);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();

        mNotes = NoteManager.get(getActivity()).getNotesForPlace(mPlace);
        updateUI();
    }

    private void updateUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new PlaceNoteRecyclerViewAdapter(mNotes));
    }

}
