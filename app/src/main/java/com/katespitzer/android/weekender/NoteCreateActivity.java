package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class NoteCreateActivity extends AppCompatActivity {

    private Note mNote;
    private Trip mTrip;
    private Place mPlace;
    private NoteManager mNoteManager;

    private EditText mTitleEditText;
    private EditText mContentEditText;
    private Button mSubmitButton;

    private static final String EXTRA_PLACE_ID = "com.katespitzer.android.weekender.place_db_id";
    private static final String EXTRA_TRIP_ID = "com.katespitzer.android.weekender.trip_db_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        mNote = new Note();

        UUID placeId = (UUID) getIntent().getSerializableExtra(EXTRA_PLACE_ID);
        UUID tripId = (UUID) getIntent().getSerializableExtra(EXTRA_TRIP_ID);

        if (placeId != null) {
            mPlace = PlaceManager.get(this).getPlace(placeId);
        }

        mTrip = TripManager.get(this).getTrip(tripId);

        mNoteManager = NoteManager.get(this);

        mTitleEditText = findViewById(R.id.note_title_input);
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContentEditText = findViewById(R.id.note_content_input);
        mContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSubmitButton = findViewById(R.id.note_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mPlace != null) {
                    mNoteManager.addNoteToPlace(mNote, mPlace);
                } else {
                    mNoteManager.addNoteToTrip(mNote, mTrip);
                }
                finish();
            }
        });

    }

    public static Intent newIntent(Context context, Place place) {
        Intent intent = new Intent(context, NoteCreateActivity.class);

        Trip trip = PlaceManager.get(context).getTripFor(place);

        intent.putExtra(EXTRA_PLACE_ID, place.getId());
        intent.putExtra(EXTRA_TRIP_ID, trip.getId());

        return intent;
    }

    public static Intent newIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, NoteCreateActivity.class);
        intent.putExtra(EXTRA_TRIP_ID, trip.getId());

        return intent;
    }
}
