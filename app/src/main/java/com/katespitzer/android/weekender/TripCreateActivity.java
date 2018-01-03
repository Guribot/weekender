package com.katespitzer.android.weekender;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class TripCreateActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mStartEditText;
    private EditText mEndEditText;
    private Button mSubmitButton;

    private Trip mTrip;
    private Date returnedDate;

    private static final String TAG = "TripCreateActivity";
    private static final String DIALOG_START_DATE = "DialogStartDate";
    private static final String DIALOG_END_DATE = "DialogEndDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_create);

        mTitleEditText = findViewById(R.id.new_trip_title);

        mStartEditText = findViewById(R.id.new_trip_start);
        setOnClickDatePick(mStartEditText, R.string.trip_start_label, DIALOG_START_DATE);

        mEndEditText = findViewById(R.id.new_trip_end);
        setOnClickDatePick(mEndEditText, R.string.trip_end_label, DIALOG_END_DATE);
    }

    private void setOnClickDatePick(View v, final int titleStringId, final String tag) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                String title = getString(titleStringId);
                DatePickerFragment dialog = DatePickerFragment.newInstance(title, null);
                dialog.show(fragmentManager, tag);
            }
        });
    }
}
