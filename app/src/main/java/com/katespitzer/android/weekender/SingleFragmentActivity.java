package com.katespitzer.android.weekender;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by kate on 1/2/18.
 *
 * Below layout based on that of Big Nerd Ranch's Android Programming book
 *
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        Log.i("SingleFragmentActivity", "in onCreate()");
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_fragment);

        // TODO: wrap head around what's happening below (better understand FragmentManager)

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            // createFragment returns a new instance of the subclassing FragmentActivity
            fragment = createFragment();
            // creates a new FragmentTransaction that "adds the fragment to the activity state"
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
