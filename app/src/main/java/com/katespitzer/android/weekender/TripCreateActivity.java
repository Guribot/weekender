package com.katespitzer.android.weekender;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class TripCreateActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {

    private EditText mTitleEditText;
    private EditText mStartEditText;
    private EditText mEndEditText;
    private Button mSubmitButton;

    private Trip mTrip;

    private static final String TAG = "TripCreateActivity";
    private static final String DIALOG_START_DATE = "DialogStartDate";
    private static final String DIALOG_END_DATE = "DialogEndDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_create);

        mTrip = new Trip();

        mTitleEditText = findViewById(R.id.new_trip_title);

        mStartEditText = findViewById(R.id.new_trip_start);
        mStartEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(getString(R.string.trip_start_label), mTrip.getStartDate());
                dialog.show(fragmentManager, DIALOG_START_DATE);
            }
        });

        mEndEditText = findViewById(R.id.new_trip_end);
        mEndEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(getString(R.string.trip_end_label), mTrip.getEndDate());
                dialog.show(fragmentManager, DIALOG_END_DATE);
            }
        });
    }

    @Override
    public void onDateSubmit(Date date, String tag) {
        Log.i(TAG, "onDateSubmit()");
        switch (tag) {
            case DIALOG_START_DATE:
                mTrip.setStartDate(date);
                mStartEditText.setText(date.toString());
                break;
            case DIALOG_END_DATE:
                mTrip.setEndDate(date);
                mEndEditText.setText(date.toString());
                break;
        }
    }

    // Has extracting this method actually made the code more DRY, or just more complicated?
        // conclusion: way more complicated

//    private void setOnClickDatePick(View v, final int titleStringId, final String tag, Boolean isStart) {
//        final Date date = isStart ? mTrip.getStartDate() : mTrip.getEndDate();
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                String title = getString(titleStringId);
//                DatePickerFragment dialog = DatePickerFragment.newInstance(title, date);
//                dialog.show(fragmentManager, tag);
//            }
//        });
//    }
}
