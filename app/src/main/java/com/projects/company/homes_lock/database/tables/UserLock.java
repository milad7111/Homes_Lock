package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

@Entity(tableName = "userLock",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "objectId", childColumns = "userId"),
                @ForeignKey(entity = Device.class, parentColumns = "objectId", childColumns = "deviceId")},
        indices = {@Index(value = {"userId"}), @Index(value = {"deviceId"})})
public class UserLock extends BaseModel {

    //region Database attributes
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "userId")
    private String mUserId;

    @ColumnInfo(name = "deviceId")
    private String mDeviceId;

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

    @Ignore
    @SerializedName("relatedDevice")
    private Device mRelatedDevice;
    //endregion Ignore server attributes

    public UserLock() {
    }

    @Ignore
    public UserLock(String mUserId, String mDeviceId, String mLockName, boolean mFavorite) {
        this.mUserId = mUserId;
        this.mDeviceId = mDeviceId;
        this.mLockName = mLockName;
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

    public Device getRelatedDevice() {
        return mRelatedDevice;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setRelatedDevice(Device mRelatedDevice) {
        this.mRelatedDevice = mRelatedDevice;
    }
}