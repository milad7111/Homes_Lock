package com.projects.company.homes_lock.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class UserLockModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("lockName")
    private String mLockName;
    @SerializedName("adminStatus")
    private boolean mAdminStatus;
    @SerializedName("favorite")
    private boolean mFavorite;
}
