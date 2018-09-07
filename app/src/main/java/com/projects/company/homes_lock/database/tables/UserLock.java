package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "userLock",
        foreignKeys = {
                @ForeignKey(
                        entity = Device.class,
                        parentColumns = "objectId",
                        childColumns = "objectId",
                        onDelete = CASCADE),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "objectId",
                        childColumns = "objectId",
                        onDelete = CASCADE)
        })
public class UserLock {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "lockName")
    private String mLockName;

    @ColumnInfo(name = "adminStatus")
    private boolean mAdminStatus;

    @ColumnInfo(name = "favorite")
    private boolean mFavorite;

    public UserLock(@NonNull String mObjectId, String mLockName, boolean mAdminStatus, boolean mFavorite) {
        this.mObjectId = mObjectId;
        this.mLockName = mLockName;
        this.mAdminStatus = mAdminStatus;
        this.mFavorite = mFavorite;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getLockName() {
        return mLockName;
    }

    public void setLockName(String mLockName) {
        this.mLockName = mLockName;
    }

    public boolean getAdminStatus() {
        return mAdminStatus;
    }

    public void setAdminStatus(boolean mAdminStatus) {
        this.mAdminStatus = mAdminStatus;
    }

    public boolean getFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }
}