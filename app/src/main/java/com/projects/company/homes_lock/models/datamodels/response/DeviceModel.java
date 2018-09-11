package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceModel extends BaseModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("created")
    private long mCreatedAt;
    @SerializedName("ownerId")
    private String mOwnerId;
    @SerializedName("updated")
    private long mUpdated;
    @SerializedName("___class")
    private int mServerTableName;

    @SerializedName("serialNumber")
    private String mSerialNumber;
    @SerializedName("coLevel")
    private int mCOLevel;
    @SerializedName("wifiStatus")
    private int mWifiStatus;
    @SerializedName("lockSSID")
    private String mLockSSID;
    @SerializedName("meanPowerCons")
    private int mMeanPowerCons;
    @SerializedName("lockStatus")
    private boolean mLockStatus;
    @SerializedName("batteryStatus")
    private int mBatteryStatus;
    @SerializedName("doorStatus")
    private boolean mDoorStatus;
    @SerializedName("connectionStatus")
    private boolean mConnectionStatus;
    @SerializedName("temperature")
    private int mTemperature;
    @SerializedName("humidity")
    private int mHumidity;
    @SerializedName("fwVersion")
    private int mFWVersion;
    @SerializedName("deviceHealth")
    private boolean mDeviceHealth;
    @SerializedName("lockPosition")
    private int mLockPosition;
    @SerializedName("relatedUsers")
    private List<UserLockModel> mRelatedUsers;
    @SerializedName("relatedErrors")
    private List<DeviceErrorModel> mRelatedErrors;
    @SerializedName("user")
    private UserModel mUser;
}
