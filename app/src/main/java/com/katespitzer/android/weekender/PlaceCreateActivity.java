package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    private static final String TAG = "PlaceCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);

        UUID tripId = (UUID) getIntent().getSerializableExtra(EXTRA_TRIP_ID_FOR_PLACE);
        mTrip = TripList.get(getApplicationContext()).getTrip(tripId);

        mPlace = new Place();

        mNameEditText = findViewById(R.id.place_name_input);
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
                PlaceList pl = PlaceList.get(getApplicationContext());
                pl.addPlaceToTrip(mPlace, mTrip);
                finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, PlaceCreateActivity.class);
        intent.putExtra(EXTRA_TRIP_ID_FOR_PLACE, id);

        return intent;
    }
}
