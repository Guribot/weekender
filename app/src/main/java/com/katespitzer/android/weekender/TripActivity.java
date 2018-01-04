package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.UUID;

/**
 * Created by kate on 1/4/18.
 */

public class TripActivity extends SingleFragmentActivity {
    private static final String TAG = "TripActivity";

    public static final String EXTRA_TRIP_ID = "com.katespitzer.android.weekender.trip_id";

    @Override
    protected Fragment createFragment() {
        return new TripFragment();
    }

    public static Intent newIntent(Context packageContext, UUID tripId) {
        Log.i(TAG, "newIntent with UUID: " + tripId);
        Intent intent = new Intent(packageContext, TripActivity.class);
        intent.putExtra(EXTRA_TRIP_ID, tripId);

        return intent;
    }
}
