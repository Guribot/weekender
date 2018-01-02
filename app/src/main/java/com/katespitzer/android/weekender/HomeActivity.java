package com.katespitzer.android.weekender;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button mMyTripsButton;
    private Button mNewTripButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HomeActivity", "in onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMyTripsButton = findViewById(R.id.my_trips_button);
        mMyTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTripList();
            }
        });

        mNewTripButton = findViewById(R.id.new_trip_button);
        mNewTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewTripButton.setEnabled(false);
            }
        });

        hideActionBar();
    }

    private void hideActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
    }

    private void startTripList() {
        Log.i("HomeActivity", "in startTripList()");
        Intent intent = new Intent(this, TripListActivity.class);
        startActivity(intent);
    }
}
