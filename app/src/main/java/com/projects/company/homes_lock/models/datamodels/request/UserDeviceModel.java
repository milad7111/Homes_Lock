package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

public class UserDeviceModel extends BaseModel {

    @SerializedName("deviceName")
    private String mDeviceName;

    @SerializedName("adminStatus")
    private boolean mAdminStatus;

    @SerializedName("favorite")
    private boolean mFavorite;

    public UserDeviceModel(String mDeviceName, boolean mAdminStatus, boolean mFavorite) {
        this.mDeviceName = mDeviceName;
        this.mAdminStatus = mAdminStatus;
        this.mFavorite = mFavorite;
    }
}
