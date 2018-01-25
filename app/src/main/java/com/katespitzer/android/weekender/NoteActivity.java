package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.katespitzer.android.weekender.managers.NoteManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Trip;

import java.util.UUID;

/**
 * Created by kate on 1/16/18.
 *
 */

public class NoteActivity extends AppCompatActivity {

    private UUID mNoteId;
    private Note mNote;
    private Trip mTrip;

    private static final String EXTRA_NOTE_ID = "com.katespitzer.android.weekender.note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mNoteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);

        mNote = NoteManager.get(this).getNote(mNoteId);
        mTrip = TripManager.get(this).getTrip(mNote.getTripId());

        getSupportActionBar().setTitle(mTrip.getTitle());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = NoteFragment.newInstance(mNoteId);

        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit();

    }

    public static Intent newIntent(Context context, UUID noteId) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);

        return intent;
    }
}
