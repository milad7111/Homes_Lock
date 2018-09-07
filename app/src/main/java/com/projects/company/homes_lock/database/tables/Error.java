package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "error")
public class Error {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "errorName")
    private String mErrorName;

    public Error(@NonNull String mObjectId, String mErrorName) {
        this.mObjectId = mObjectId;
        this.mErrorName = mErrorName;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getErrorName() {
        return mErrorName;
    }

    public void setErrorName(String mErrorName) {
        this.mErrorName = mErrorName;
    }
}