package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katespitzer.android.weekender.managers.NoteManager;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    private Note mNote;

    private TextView mTitleView;
    private TextView mSourceView;
    private TextView mContentView;
    private UUID mNoteId;

    private static final String TAG = "NoteFragment";
    private static final String ARG_NOTE_ID = "note_id";

    private OnFragmentInteractionListener mListener;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteId
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(UUID noteId) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
            mNote = NoteManager.get(getActivity()).getNote(mNoteId);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        mTitleView = view.findViewById(R.id.note_title);

        mSourceView = view.findViewById(R.id.note_source);
        if (mNote.getPlaceId() > 0) {
            Place place = PlaceManager.get(getActivity()).getPlace(mNote.getPlaceId());
            mSourceView.setText(place.getName());
        } else {
            mSourceView.setVisibility(View.GONE);
        }

        mContentView = view.findViewById(R.id.note_content);

        displayNote();

        return view;
    }

    private void displayNote() {
        mNote = NoteManager.get(getActivity()).getNote(mNoteId);

        mTitleView.setText(mNote.getTitle());
        mContentView.setText(mNote.getContent());
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.note_menu_edit_note) {
            Intent intent = NoteFormActivity.newIntent(getActivity(), mNote);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach: ");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        displayNote();
        super.onResume();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
