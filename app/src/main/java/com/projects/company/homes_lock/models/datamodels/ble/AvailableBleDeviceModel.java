package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class AvailableBleDeviceModel extends BaseModel {

    //region Declare Objects
    private int mIndex;
    private String mMacAddress;
    private String mPassword;
    private int mRSSI;
    private boolean mConnectionStatus;
    //endregion Declare Objects

    //region Constructor
    public AvailableBleDeviceModel(int mIndex, String mMacAddress, int mRSSI, boolean mConnectionStatus) {
        this.mIndex = mIndex;
        this.mMacAddress = mMacAddress;
        this.mRSSI = mRSSI;
        this.mConnectionStatus = mConnectionStatus;
    }

    public AvailableBleDeviceModel(int mIndex) {
        this.mIndex = mIndex;
        this.mMacAddress = null;
        this.mConnectionStatus = false;
    }
    //endregion Constructor

    //region Declare Methods
    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setMacAddress(String mMacAddress) {
        this.mMacAddress = mMacAddress;
    }

    public String getMacAddress() {
        return mMacAddress == null ? "" : mMacAddress;
    }

    public void setConnectionStatus(boolean mConnectionStatus) {
        this.mConnectionStatus = mConnectionStatus;
    }

    public boolean getConnectionStatus() {
        return mConnectionStatus;
    }

    public boolean isValidData() {
        return mIndex != SEARCHING_SCAN_MODE && mIndex != SEARCHING_TIMEOUT_MODE;
    }

    public boolean isInvalidData() {
        return mIndex == SEARCHING_SCAN_MODE || mIndex == SEARCHING_TIMEOUT_MODE;
    }

    public String getPassword() {
        return mPassword != null ? mPassword : "";
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }
    //endregion Declare Methods
}