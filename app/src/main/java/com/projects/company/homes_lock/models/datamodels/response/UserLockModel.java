package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

public class UserLockModel extends BaseModel {

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

    @SerializedName("adminStatus")
    private boolean mAdminStatus;
    @SerializedName("lockName")
    private String mLockName;
    @SerializedName("favorite")
    private boolean mFavorite;
}
