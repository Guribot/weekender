package com.katespitzer.android.weekender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class NewTripActivity extends AppCompatActivity {

    private static final String TAG = "NewTripActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
    }
}
