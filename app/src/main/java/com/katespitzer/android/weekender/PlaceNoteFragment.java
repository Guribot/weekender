package com.katespitzer.android.weekender;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlaceNoteFragment extends Fragment {

    private Place mPlace;
    private List<Note> mNotes;

    private static final String TAG = "PlaceNoteFragment";
    private static final String ARG_PLACE_ID = "place_id";
    private OnListFragmentInteractionListener mListener;

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
            Log.i(TAG, "onCreate: notes are " + mNotes);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placenote_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PlaceNoteRecyclerViewAdapter(mNotes, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDestinationListItemInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnListFragmentInteractionListener {
        void onNoteClicked(Note note);
    }
}
