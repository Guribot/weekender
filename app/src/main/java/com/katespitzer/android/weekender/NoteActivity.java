package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.UUID;

/**
 * Created by kate on 1/16/18.
 *
 */

public class NoteActivity extends AppCompatActivity implements NoteFragment.OnFragmentInteractionListener {

    private UUID mNoteId;

    private static final String TAG = "SingleFragmentActivity";
    private static final String EXTRA_NOTE_ID = "com.katespitzer.android.weekender.note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mNoteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
