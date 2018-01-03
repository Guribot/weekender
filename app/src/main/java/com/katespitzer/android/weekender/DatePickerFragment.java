package com.katespitzer.android.weekender;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kate on 1/2/18.
 */

public class DatePickerFragment extends DialogFragment {

    private DatePicker mDatePicker;
    private static String mTitleString;
    private String mTag;

    private static final String ARG_DATE = "date";
    private static final String TAG = "DatePickerFragment";

    public interface DatePickerListener {
        public void onDateSubmit(Date date, String tag);
    }

    DatePickerListener mListener;

    // https://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the listener
            mListener = (DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DatePickerListener");
        }
    }

    public static DatePickerFragment newInstance(String titleString, Date date) {
        Log.i(TAG, "newInstance()");

        mTitleString = titleString;

        Bundle args = new Bundle();
        if (date == null) {
            Log.i(TAG, "newInstance: Date was null!");
            date = new Date();
        }

        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mTag = this.getTag();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mTitleString)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        mListener.onDateSubmit(date, mTag);
                    }
                })
                .create();
    }
}
