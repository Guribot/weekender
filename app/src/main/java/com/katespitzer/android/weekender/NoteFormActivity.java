package com.katespitzer.android.weekender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class NoteFormActivity extends AppCompatActivity {

    private Note mNote;

    private static final String EXTRA_PLACE_DBID = "com.katespitzer.android.weekender.place_db_id";
    private static final String EXTRA_TRIP_DBID = "com.katespitzer.android.weekender.trip_db_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        int placeId = getIntent().getIntExtra(EXTRA_PLACE_DBID, 0);
        int tripId = getIntent().getIntExtra(EXTRA_TRIP_DBID, 0);

        mNote.setPlaceId(placeId);
        mNote.setTripId(tripId);
    }

    public Intent newIntent(Place place) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLACE_DBID, place.getDbId());
        intent.putExtra(EXTRA_TRIP_DBID, place.getTripId());

        return intent;
    }

    public Intent newIntent(Trip trip) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TRIP_DBID, trip.getDbId());

        return intent;
    }
}
