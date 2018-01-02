package com.katespitzer.android.weekender;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        hideActionBar();
    }

    private void hideActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
    }
}
