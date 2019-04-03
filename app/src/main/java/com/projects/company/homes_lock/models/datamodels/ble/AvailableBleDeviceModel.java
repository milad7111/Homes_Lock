package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class AvailableBleDeviceModel extends BaseModel {

    //region Declare Objects
    private String mMacAddress;
    private int mPassword;
    private int mRSSI;
    private boolean mSaved;
    private boolean mConnectionStatus;
    //endregion Declare Objects

    //region Constructor
    public AvailableBleDeviceModel(String mMacAddress, int mRSSI, boolean mConnectionStatus, boolean mSaved) {
        this.mMacAddress = mMacAddress;
        this.mRSSI = mRSSI;
        this.mConnectionStatus = mConnectionStatus;
        this.mSaved = mSaved;
    }

    public AvailableBleDeviceModel(int mRSSI) {
        this.mRSSI = mRSSI;
        this.mMacAddress = null;
        this.mConnectionStatus = false;
        this.mSaved = false;
    }
    //endregion Constructor

    //region Declare Methods
    public void setRSSI(int mRSSI) {
        this.mRSSI = mRSSI;
    }

    public int getRSSI() {
        return mRSSI;
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
        return mRSSI != SEARCHING_SCAN_MODE && mRSSI != SEARCHING_TIMEOUT_MODE;
    }

    public boolean isInvalidData() {
        return mRSSI == SEARCHING_SCAN_MODE || mRSSI == SEARCHING_TIMEOUT_MODE;
    }

    public int getPassword() {
        return mPassword;
    }

    public void setPassword(int mPassword) {
        this.mPassword = mPassword;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public void setSaved(boolean mSaved) {
        this.mSaved = mSaved;
    }
    //endregion Declare Methods
}