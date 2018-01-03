package com.katespitzer.android.weekender;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // all three of these callbacks are triggered after keypress, before character is displayed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTrip.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

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

        mSubmitButton = findViewById(R.id.new_trip_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TripCreateActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
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
}
