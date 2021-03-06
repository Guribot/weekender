package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;

import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class PlaceCreateActivity extends AppCompatActivity {

    private Place mPlace;
    private Trip mTrip;
    private EditText mNameEditText;
    private EditText mAddressEditText;
    private Button mSubmitButton;

    private static final String EXTRA_TRIP_ID_FOR_PLACE = "com.katespitzer.android.weekender.trip_id_for_place";
    private static final String EXTRA_PLACE_ID = "com.katespitzer.android.weekender.place_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);

        UUID tripId = (UUID) getIntent().getSerializableExtra(EXTRA_TRIP_ID_FOR_PLACE);
        mTrip = TripManager.get(getApplicationContext()).getTrip(tripId);

        mPlace = new Place();

        if (getIntent().getSerializableExtra(EXTRA_PLACE_ID) != null) {
            UUID placeId = (UUID) getIntent().getSerializableExtra(EXTRA_PLACE_ID);
            mPlace = PlaceManager.get(this).getPlace(placeId);
        }

        mNameEditText = findViewById(R.id.place_name_input);
        mNameEditText.setText(mPlace.getName());
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlace.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAddressEditText = findViewById(R.id.place_address_input);
        mAddressEditText.setText(mPlace.getAddress());
        mAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlace.setAddress(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSubmitButton = findViewById(R.id.place_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceManager pl = PlaceManager.get(getApplicationContext());
                pl.addPlaceToTrip(mPlace, mTrip);
                finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, UUID tripId, @Nullable UUID placeId) {
        Intent intent = new Intent(packageContext, PlaceCreateActivity.class);
        intent.putExtra(EXTRA_TRIP_ID_FOR_PLACE, tripId);
        intent.putExtra(EXTRA_PLACE_ID, placeId);

        return intent;
    }
}
