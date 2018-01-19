package com.katespitzer.android.weekender;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.katespitzer.android.weekender.managers.TripManager;
import com.katespitzer.android.weekender.models.Trip;

import java.text.DateFormat;
import java.util.Date;

public class TripFormActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {

    private EditText mTitleEditText;
    private EditText mStartEditText;
    private EditText mEndEditText;
    private Button mSubmitButton;
    private Activity mActivity;

    private Trip mTrip;

    private static final String TAG = "TripFormActivity";
    private static final String DIALOG_START_DATE = "DialogStartDate";
    private static final String DIALOG_END_DATE = "DialogEndDate";

    /**
     * Overall logic for this Create Trip form.
     * - calls super, renders view, instantiates mTrip
     * - sets TextChangedListener for Title EditText to update the mTrip title as it's changed
     * - sets OnClickListener for Start Date EditText to update mTrip StartDate
     * - sets OnClickListener for End Date EditText to update mTrip EndDate
     * - sets OnClickListener for Submit Button to add mTrip to db and finish the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_form);

        mActivity = this;

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
        mTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // TODO: make this work
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "onFocusChange: ");
                if (!hasFocus) {
                    Log.i(TAG, "onFocusChange2: ");
                    hideKeyboard(v);
                }
            }
        });

        mStartEditText = findViewById(R.id.new_trip_start);
        mStartEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(getString(R.string.trip_start_label), mTrip.getStartDate());
                dialog.show(fragmentManager, DIALOG_START_DATE);
            }
        });

        mEndEditText = findViewById(R.id.new_trip_end);
        mEndEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(getString(R.string.trip_end_label), mTrip.getEndDate());
                dialog.show(fragmentManager, DIALOG_END_DATE);
            }
        });


        mSubmitButton = findViewById(R.id.trip_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripManager.get(mActivity)
                        .addTrip(mTrip);
                finish();
            }
        });
    }

    public void hideKeyboard(View view) {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDateSubmit(Date date, String tag) {
        Log.i(TAG, "onDateSubmit()");
        switch (tag) {
            case DIALOG_START_DATE:
                mTrip.setStartDate(date);
                mStartEditText.setText(dateFormat(date));
                break;
            case DIALOG_END_DATE:
                mTrip.setEndDate(date);
                mEndEditText.setText(dateFormat(date));
                break;
        }
    }

    private String dateFormat(Date date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM)
                .format(date);
    }
}
