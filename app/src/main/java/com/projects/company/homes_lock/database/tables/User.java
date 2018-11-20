package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

import java.util.List;

@Entity(tableName = "user")
public class User extends BaseModel {

    //region Database attributes
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "isLock")
    @SerializedName("isLock")
    private String mIsLock;

    @ColumnInfo(name = "lastLogin")
    @SerializedName("lastLogin")
    private long mLastLogin;

    @ColumnInfo(name = "userStatus")
    @SerializedName("userStatus")
    private boolean mUserStatus;

    @ColumnInfo(name = "mobileNumber")
    @SerializedName("mobileNumber")
    private String mMobileNumber;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String mName;

    @ColumnInfo(name = "email")
    @SerializedName("email")
    private String mEmail;

    @ColumnInfo(name = "user-token")
    @SerializedName("user-token")
    private String mUserToken;
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
    @SerializedName("relatedUserLocks")
    private List<UserLock> mRelatedUserLocks;

    @Ignore
    @SerializedName("socialAccount")
    private String mSocialAccount;
    //endregion Ignore server attributes

    public User() {
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setIsLock(String mIsLock) {
        this.mIsLock = mIsLock;
    }

    public String getIsLock() {
        return mIsLock;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setLastLogin(long mLastLogin) {
        this.mLastLogin = mLastLogin;
    }

    public long getLastLogin() {
        return mLastLogin;
    }

    public void setUserToken(String mUserToken) {
        this.mUserToken = mUserToken;
    }

    public void setUserStatus(boolean mUserStatus) {
        this.mUserStatus = mUserStatus;
    }

    public boolean getUserStatus() {
        return mUserStatus;
    }

    public void setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public String getName() {
        return mName;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUserToken() {
        return mUserToken;
    }

    public boolean isActiveUser() {
        return mUserToken.equals(null);
    }

    public List<UserLock> getRelatedUserLocks() {
        return mRelatedUserLocks;
    }

    public void setRelatedUserLocks(List<UserLock> mRelatedUserLocks) {
        this.mRelatedUserLocks = mRelatedUserLocks;
    }
}