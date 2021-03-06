package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class ConnectedDeviceModel extends BaseModel {

    //region Declare Objects
    private int mIndex;
    private String mMacAddress;
    private boolean mConnectionStatus;
    private boolean mRole;//0: Server     1: Client
    //endregion Declare Objects

    //region Constructor
    public ConnectedDeviceModel(int mIndex, String mMacAddress, boolean mConnectionStatus, boolean mRole) {
        this.mIndex = mIndex;
        this.mMacAddress = mMacAddress;
        this.mConnectionStatus = mConnectionStatus;
        this.mRole = mRole;
    }

    public ConnectedDeviceModel(int mIndex) {
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

    public boolean isClient() {
        return mRole;
    }
    //endregion Declare Methods
}