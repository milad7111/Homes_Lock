package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;

public class UserLockModel extends BaseModel {

    @SerializedName("deviceName")
    private String mDeviceName;

    @SerializedName("adminStatus")
    private boolean mAdminStatus;

    @SerializedName("favorite")
    private boolean mFavorite;

    public UserLockModel(String mDeviceName, boolean mAdminStatus, boolean mFavorite) {
        this.mDeviceName = mDeviceName;
        this.mAdminStatus = mAdminStatus;
        this.mFavorite = mFavorite;
    }
}
