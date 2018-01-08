package com.katespitzer.android.weekender;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kate on 1/8/18.
 */

public class Note {
    private UUID mId;
    private String mTitle;
    private String mContent;
    private int mTripId;
    private int mPlaceId;
    private Date mCreatedDate;

    public Note() {
        this(UUID.randomUUID());
    }

    public Note(UUID uuid) {
        mId = uuid;
        mCreatedDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getTripId() {
        return mTripId;
    }

    public void setTripId(int tripId) {
        mTripId = tripId;
    }

    public int getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(int placeId) {
        mPlaceId = placeId;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        mCreatedDate = createdDate;
    }
}
