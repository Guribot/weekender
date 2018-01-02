package com.katespitzer.android.weekender;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TripListActivity extends SingleFragmentActivity {

    private static final String TAG = "TripListActivity";

    @Override
    protected Fragment createFragment() {
        Log.i(TAG, "in createFragment()");
        return new TripListFragment();
    }
}
