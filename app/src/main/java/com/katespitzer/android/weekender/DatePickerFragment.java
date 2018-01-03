package com.katespitzer.android.weekender;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by kate on 1/2/18.
 */

public class DatePickerFragment extends DialogFragment {

    private static String mTitleString;

    public static DatePickerFragment newInstance(String titleString) {

        mTitleString = titleString;

        Bundle args = new Bundle();

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mTitleString)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}
