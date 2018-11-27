package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

public class UserLockModel extends BaseModel {

    @SerializedName("lockName")
    private String mLockName;

    @SerializedName("adminStatus")
    private boolean mAdminStatus;

    @SerializedName("favorite")
    private boolean mFavorite;

    public UserLockModel(String mLockName, boolean mAdminStatus, boolean mFavorite) {
        this.mLockName = mLockName;
        this.mAdminStatus = mAdminStatus;
        this.mFavorite = mFavorite;
    }
}
