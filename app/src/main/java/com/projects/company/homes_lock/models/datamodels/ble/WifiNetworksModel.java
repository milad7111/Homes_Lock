package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class WifiNetworksModel extends BaseModel {

    //region Declare Objects
    private int mIndex;
    private String mSSID;
    private String mPassword;
    private int mAuthenticateType;
    private int mRSSI;
    //endregion Declare Objects

    //region Constructor
    public WifiNetworksModel(int mIndex, String mSSID, int mAuthenticateType, int mRSSI) {
        this.mIndex = mIndex;
        this.mSSID = mSSID;
        this.mAuthenticateType = mAuthenticateType;
        this.mRSSI = mRSSI;
    }

    public WifiNetworksModel(String mSSID, int mRSSI) {
        this.mSSID = mSSID;
        this.mRSSI = mRSSI;
    }

    public WifiNetworksModel(int mRSSI) {
        this.mRSSI = mRSSI;
    }
    //endregion Constructor

    //region Declare Methods
    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public String getSSID() {
        return mSSID != null ? mSSID : "";
    }

    public void setSSID(String mSSID) {
        this.mSSID = mSSID;
    }

    public int getAuthenticateType() {
        return mAuthenticateType;
    }

    public void setAuthenticateType(int mAuthenticateType) {
        this.mAuthenticateType = mAuthenticateType;
    }

    public int getRSSI() {
        return mRSSI;
    }

    public void setRSSI(int mRSSI) {
        this.mRSSI = mRSSI;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public boolean isValidData() {
        return mRSSI != SEARCHING_SCAN_MODE && mRSSI != SEARCHING_TIMEOUT_MODE;
    }

    public boolean isInvalidData() {
        return mRSSI == SEARCHING_SCAN_MODE || mRSSI == SEARCHING_TIMEOUT_MODE;
    }
    //endregion Declare Methods
}
