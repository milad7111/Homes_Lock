package com.projects.company.homes_lock.models.datamodels.request;

import com.projects.company.homes_lock.base.BaseModel;

public class TempDeviceModel extends BaseModel {

    private boolean mUpdateRequired;
    private String mObjectId;

    private String mDeviceName;
    private String mDeviceSerialNumber;
    private boolean mFavoriteStatus;
    private String mDeviceMacAddress;

    public TempDeviceModel() {
    }

    public TempDeviceModel(String mObjectId) {
        this.mUpdateRequired = true;
        this.mObjectId = mObjectId;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getDeviceSerialNumber() {
        return mDeviceSerialNumber;
    }

    public void setDeviceSerialNumber(String mDeviceSerialNumber) {
        this.mDeviceSerialNumber = mDeviceSerialNumber;
    }

    public boolean isFavoriteLock() {
        return mFavoriteStatus;
    }

    public void setFavoriteStatus(boolean mFavoriteStatus) {
        this.mFavoriteStatus = mFavoriteStatus;
    }

    public String getDeviceMacAddress() {
        return mDeviceMacAddress;
    }

    public void setDeviceMacAddress(String mDeviceMacAddress) {
        this.mDeviceMacAddress = mDeviceMacAddress;
    }

    public boolean isUpdateRequired() {
        return mUpdateRequired;
    }

    public void setUpdateRequired(boolean mUpdateRequired) {
        this.mUpdateRequired = mUpdateRequired;
    }

    public String getObjectId() {
        return mObjectId;
    }
}
