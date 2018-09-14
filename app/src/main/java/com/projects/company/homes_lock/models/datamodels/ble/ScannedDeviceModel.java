package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

public class ScannedDeviceModel extends BaseModel {

    private String mName;
    private String mMacAddress;

    public ScannedDeviceModel(String mName, String mMacAddress) {
        this.mName = mName;
        this.mMacAddress = mMacAddress;
    }

    public String getSpecialValue() {
        return mMacAddress;
    }

    public String getName() {
        return mName;
    }

    public String getMacAddress() {
        return mMacAddress;
    }
}
