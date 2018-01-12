package com.katespitzer.android.weekender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.katespitzer.android.weekender.managers.PlaceManager;
import com.katespitzer.android.weekender.models.Place;

import java.util.UUID;

public class PlaceActivity extends AppCompatActivity
        implements PlaceFragment.OnFragmentInteractionListener{

    private static final String TAG = "PlaceActivity";
    private static final String EXTRA_PLACE_ID = "com.katespitzer.android.weekender.place_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        UUID placeId = (UUID) getIntent().getSerializableExtra(EXTRA_PLACE_ID);

        Fragment fragment = PlaceFragment.newInstance(placeId);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.place_fragment_container, fragment)
                .commit();
    }

    public static Intent newIntent(Context context, UUID placeId) {
        Log.i(TAG, "newIntent: ");
        Intent intent = new Intent(context, PlaceActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, placeId);

        return intent;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
}
