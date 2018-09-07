package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "email")
    private String mEmail;

    @ColumnInfo(name = "mobileNumber")
    private String mMobileNumber;

    public User(@NonNull String mObjectId, String mEmail, String mMobileNumber) {
        this.mObjectId = mObjectId;
        this.mEmail = mEmail;
        this.mMobileNumber = mMobileNumber;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }
}