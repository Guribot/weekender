package com.katespitzer.android.weekender;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TripListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Log.i("TripListActivity", "in createFragment()");
        return new TripListFragment();
    }
}
