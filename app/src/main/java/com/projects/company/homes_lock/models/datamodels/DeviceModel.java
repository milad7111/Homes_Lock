package com.projects.company.homes_lock.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class DeviceModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("lockSSID")
    private String mLockSSID;
    @SerializedName("serialNumber")
    private String mSerialNumber;
    @SerializedName("lockStatus")
    private boolean mLockStatus;
    @SerializedName("doorStatus")
    private boolean mDoorStatus;
    @SerializedName("connectionStatus")
    private boolean mConnectionStatus;
    @SerializedName("batteryStatus")
    private int mBatteryStatus;
    @SerializedName("wifiStatus")
    private int mWifiStatus;
    @SerializedName("meanPowerCons")
    private int mMeanPowerCons;
    @SerializedName("temperature")
    private int mTemperature;
    @SerializedName("humidity")
    private int mHumidity;
    @SerializedName("COLevel")
    private int mCOLevel;
    @SerializedName("deviceHealth")
    private boolean mDeviceHealth;
    @SerializedName("FWVersion")
    private int mFWVersion;
    @SerializedName("lockPosition")
    private int mLockPosition;
}
