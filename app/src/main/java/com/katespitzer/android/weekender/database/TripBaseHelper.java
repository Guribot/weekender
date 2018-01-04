package com.katespitzer.android.weekender.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.katespitzer.android.weekender.database.TripDbSchema.TripTable;

/**
 * Created by kate on 1/3/18.
 */

public class TripBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tripBase.db";

    public TripBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TripTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TripTable.Cols.UUID + ", " +
                TripTable.Cols.TITLE + ", " +
                TripTable.Cols.START_DATE + ", " +
                TripTable.Cols.END_DATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
