package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel extends BaseModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("created")
    private long mCreatedAt;
    @SerializedName("ownerId")
    private String mOwnerId;
    @SerializedName("updated")
    private long mUpdated;
    @SerializedName("___class")
    private String mServerTableName;

    @SerializedName("isLock")
    private String mIsLock;
    @SerializedName("lastLogin")
    private long mLastLogin;
    @SerializedName("userStatus")
    private boolean mUserStatus;
    @SerializedName("mobileNumber")
    private String mMobileNumber;
    @SerializedName("name")
    private String mName;
    @SerializedName("socialAccount")
    private String mSocialAccount;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("user-token")
    private String mUserToken;
    @SerializedName("relatedDevices")
    private List<UserLockModel> mRelatedDevices;
}
