package com.katespitzer.android.weekender.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katespitzer.android.weekender.database.DbSchema;
import com.katespitzer.android.weekender.models.Note;
import com.katespitzer.android.weekender.models.Place;
import com.katespitzer.android.weekender.models.Trip;
import com.katespitzer.android.weekender.database.DatabaseHelper;
import com.katespitzer.android.weekender.database.NoteCursorWrapper;
import com.katespitzer.android.weekender.database.DbSchema.NoteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kate on 1/8/18.
 */

public class NoteManager {
    private static NoteManager sNoteManager;
    // it may be unnecessary to declare the context, this is for potential future features
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static final String TAG = "NoteManager";

    /**
     * Constructor: takes in Context and initializes database
     *
     * @param context
     */
    private NoteManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DatabaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * returns singular NoteManager
     *
     * @param context
     * @return
     */
    public static NoteManager get(Context context) {
        Log.i(TAG, "in get()");

        if (sNoteManager == null) {
            sNoteManager = new NoteManager(context);
        }
        return sNoteManager;
    }


    /**
     * Takes in a UUID and returns the corresponding Note
     *
     * @param id
     * @return
     */
    public Note getNote(UUID id) {
        Log.i(TAG, "in getNote()");
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                // if there are no results, return null
                return null;
            }
            // if there are results, return the first one
            // (since search param is UUID, There Can Only Be One
            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            // close cursor!
            cursor.close();
        }

        // You Should Not Be Here
    }

    /**
     * Takes in a note and adds it to the db
     *
     * @param note
     */
    public void addNote(Note note) {
        Log.i(TAG, "in addNote()");
        ContentValues values = getContentValues(note);

        long r = mDatabase.insert(NoteTable.NAME, null, values);
    }

    /**
     * Takes in a note and updates the corresponding entry in the db
     *
     * @param note
     */
    public void updateNote(Note note) {
        Log.i(TAG, "updateNote()");
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Takes in a place and deletes all its notes from the database (if it's present)
     *
     * @param place
     */
    public void deleteNotesForPlace(Place place) {
        String uuidString = place.getId().toString();

        mDatabase.delete(DbSchema.NoteTable.NAME,
                NoteTable.Cols.PLACE_ID + " = ?",
                new String[] {uuidString}
        );
    }

    /**
     * Queries db with null query params (retrieving entire db)
     * iterates through db, parses row into Note, and adds to
     * returned List<Note>
     *
     * @return
     */
    public List<Note> getNotes() {
        Log.i(TAG, "getNotes()");
        List<Note> notes = new ArrayList<>();

        // querying with null args = returns everything
        NoteCursorWrapper cursor = queryNotes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            // always close your cursors!
            cursor.close();
        }

        return notes;
    }

    /**
     * Takes a trip as a parameters and returns List of all of its Notes
     * NOTE: This includes notes belonging to child Places
     * @param trip
     * @return
     */
    public List<Note> getNotesForTrip(Trip trip){
        List<Note> notes = new ArrayList<>();
        String whereClause = NoteTable.Cols.TRIP_ID + " = " + trip.getDbId();
//        String[] whereArgs = new String[] {String.valueOf(trip.getDbId())};
        NoteCursorWrapper cursor = queryNotes(whereClause, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    /**
     * Takes a place as a parameter and returns List of all its Notes
     */
    public List<Note> getNotesForPlace(Place place){
        List<Note> notes = new ArrayList<>();
        String whereClause = NoteTable.Cols.PLACE_ID + " = " + place.getDbId();
        NoteCursorWrapper cursor = queryNotes(whereClause, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    /**
     * Returns the size of the database
     * does this work?
     *
     * @return
     */
    public int size() {
        Log.i(TAG, "size()");
        return getNotes().size();
    }

    public void addNoteToTrip(Note note, Trip trip) {
        Log.i(TAG, "addNoteToTrip()");
        note.setTripId(trip.getDbId());
        addNote(note);
    }

    public void addNoteToPlace(Note note, Place place) {
        Log.i(TAG, "addNoteToPlace()");
        note.setTripId(place.getTripId());
        note.setPlaceId(place.getDbId());
        addNote(note);
    }

    public void deleteNote(Note note) {
        Log.i(TAG, "deleteNote: " + note);
        String uuidString = note.getId().toString();

        mDatabase.delete(DbSchema.NoteTable.NAME,
                DbSchema.NoteTable.Cols.UUID + " = ?",
                new String[] {uuidString}
        );
    }

    /**
     * Converts provided Note into equivalent ContentValues (for use in SQLite operations)
     *
     * @param note
     * @return
     */
    private static ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteTable.Cols.CONTENT, note.getContent());
        values.put(NoteTable.Cols.CREATED_DATE, note.getCreatedDate().getTime());
        values.put(NoteTable.Cols.TRIP_ID, note.getTripId());
        values.put(NoteTable.Cols.PLACE_ID, note.getPlaceId());

        return values;
    }

    /**
     * Takes in query parameters and returns a
     * cursor wrapped in a NoteCursorWrapper
     * effectively searching the db
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new NoteCursorWrapper(cursor);
    }
}
