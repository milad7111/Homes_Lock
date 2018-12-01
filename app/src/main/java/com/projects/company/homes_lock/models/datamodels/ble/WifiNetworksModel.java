package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

public class WifiNetworksModel extends BaseModel {

    //region Declare Objects
    private String mSSID;
    private int mAuthenticateType;
    private int mRSSI;
    //endregion Declare Objects

    //region Constructor
    public WifiNetworksModel(String mSSID, int mAuthenticateType, int mRSSI) {
        this.mSSID = mSSID;
        this.mAuthenticateType = mAuthenticateType;
        this.mRSSI = mRSSI;
    }

    public WifiNetworksModel(int mRSSI) {
        this.mRSSI = mRSSI;
    }
    //endregion Constructor

    //region Declare Methods
    public String getSSID() {
        return mSSID;
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
    //endregion Declare Methods
}
