package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    //region Database attributes
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "adminStatus")
    @SerializedName("adminStatus")
    private boolean mAdminStatus;

    @ColumnInfo(name = "lockName")
    @SerializedName("lockName")
    private String mLockName;

    @ColumnInfo(name = "favorite")
    @SerializedName("favorite")
    private boolean mFavorite;
    //endregion Database attributes

    //region Ignore server attributes
    @Ignore
    @SerializedName("created")
    private Long mCreatedAt;

    @Ignore
    @SerializedName("ownerId")
    private String mOwnerId;

    @Ignore
    @SerializedName("updated")
    private Long mUpdated;

    @Ignore
    @SerializedName("___class")
    private String mServerTableName;
    //endregion Ignore server attributes

    public UserLock() {
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