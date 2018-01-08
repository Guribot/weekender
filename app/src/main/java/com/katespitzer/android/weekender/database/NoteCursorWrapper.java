package com.katespitzer.android.weekender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.katespitzer.android.weekender.Note;
import com.katespitzer.android.weekender.database.DbSchema.NoteTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/8/18.
 */

public class NoteCursorWrapper extends CursorWrapper {
    /**
     * Takes in a Cursor and returns a new instance of
     * a NoteCursorWrapper
     *
     * @param cursor
     */
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns a Note object built from
     * the Cursor used to instantiate the NoteCursorWrapper
     *
     * @return
     */
    public Note getNote() {
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String name = getString(getColumnIndex(NoteTable.Cols.TITLE));
        String content = getString(getColumnIndex(NoteTable.Cols.CONTENT));
        long createdDate = getLong(getColumnIndex(NoteTable.Cols.CREATED_DATE));
        int tripId = getInt(getColumnIndex(NoteTable.Cols.TRIP_ID));
        int placeId = getInt(getColumnIndex(NoteTable.Cols.PLACE_ID));
        int dbId = getInt(getColumnIndex("_id"));

        Note note = new Note();
        note.setId(UUID.fromString(uuidString));
        note.setTitle(name);
        note.setContent(content);
        note.setTripId(tripId);
        note.setPlaceId(placeId);
        note.setCreatedDate(new Date(createdDate));
        note.setDbId(dbId);

        return note;
    }
}
