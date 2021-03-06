package com.katespitzer.android.weekender.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.katespitzer.android.weekender.database.DbSchema.DestinationTable;
import com.katespitzer.android.weekender.database.DbSchema.NoteTable;
import com.katespitzer.android.weekender.database.DbSchema.PlaceTable;
import com.katespitzer.android.weekender.database.DbSchema.RouteTable;
import com.katespitzer.android.weekender.database.DbSchema.TripTable;

/**
 * Created by kate on 1/3/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weekender.db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RouteTable.CREATE);
        db.execSQL(TripTable.CREATE);
        db.execSQL(PlaceTable.CREATE);
        db.execSQL(NoteTable.CREATE);
        db.execSQL(DestinationTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: implement
    }
}
