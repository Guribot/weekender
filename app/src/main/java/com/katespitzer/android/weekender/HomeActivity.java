package com.katespitzer.android.weekender;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private Button mMyTripsButton;
    private Button mNewTripButton;
    private Button mFindTripsButton;

    public static final int NEW_TRIP_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMyTripsButton = findViewById(R.id.my_trips_button);
        mMyTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTripList();
            }
        });

        mNewTripButton = findViewById(R.id.trip_submit_button);
        mNewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewTrip();
            }
        });

        mFindTripsButton = findViewById(R.id.find_trip_button);
        mFindTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });

        hideActionBar();
    }

    /**
     * Hides action bar and status bar
     */
    private void hideActionBar() {
        //get and hide action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        // get and hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    private void startTripList() {
        Intent intent = new Intent(this, TripListActivity.class);
        startActivity(intent);
    }

    private void startNewTrip() {
        Intent intent = new Intent(this, TripFormActivity.class);
        startActivityForResult(intent, NEW_TRIP_REQUEST);
    }
}
