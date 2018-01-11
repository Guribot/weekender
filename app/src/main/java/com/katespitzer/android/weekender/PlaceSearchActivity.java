package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.katespitzer.android.weekender.models.Place;

import java.util.UUID;

/**
 * Created by kate on 1/2/18.
 *
 * Below layout based on that of Big Nerd Ranch's Android Programming book
 *
 */

public class PlaceSearchActivity extends AppCompatActivity implements SearchResultFragment.OnListFragmentInteractionListener {
    private static final String TAG = "PlaceSearchActivity";

    private static final String EXTRA_TRIP_ID = "com.katespitzer.android.weekender.trip_id";
    private static final String EXTRA_QUERY = "com.katespitzer.android.weekender.query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // TODO: wrap head around what's happening below (better understand FragmentManager)

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        Intent intent = getIntent();
        UUID tripId = (UUID) intent.getSerializableExtra(EXTRA_TRIP_ID);
        String query = intent.getStringExtra(EXTRA_QUERY);

        if (fragment == null) {
            // createFragment returns a new instance of the subclassing FragmentActivity
            fragment = createFragment(query, tripId);
            // creates a new FragmentTransaction that "adds the fragment to the activity state"
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(Place result) {

    }

    public static Intent newIntent(Context context, String query, UUID tripId) {
        Intent intent = new Intent(context, PlaceSearchActivity.class);
        intent.putExtra(EXTRA_TRIP_ID, tripId);
        intent.putExtra(EXTRA_QUERY, query);

        return intent;
    }

    protected Fragment createFragment(String query, UUID tripId) {
        Fragment fragment = SearchResultFragment.newInstance(query, tripId);

        return fragment;
    };

}