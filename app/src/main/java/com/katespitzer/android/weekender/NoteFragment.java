package com.katespitzer.android.weekender;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.katespitzer.android.weekender.managers.NoteManager;
import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    private Note mNote;
    private Trip mTrip;

    private TextView mTitleView;
    private TextView mSourceView;
    private TextView mContentView;
    private UUID mNoteId;

    private static final String ARG_NOTE_ID = "note_id";

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        mTitleView = view.findViewById(R.id.note_title);

        mSourceView = view.findViewById(R.id.note_source);
        if (mNote.getPlaceId() != null) {
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

        switch (id) {
            case R.id.note_menu_edit_note:
                Intent intent = NoteFormActivity.newIntent(getActivity(), mNote);

                startActivity(intent);
                break;
            case R.id.note_menu_delete_note:
                deleteNoteWithConfirm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        displayNote();
        super.onResume();
    }

    private void deleteNoteWithConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permanently delete this note?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NoteManager.get(getActivity()).deleteNote(mNote);
                Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
