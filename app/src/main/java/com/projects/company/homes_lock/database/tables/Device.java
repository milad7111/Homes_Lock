package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;
import com.projects.company.homes_lock.models.datamodels.response.DeviceErrorModel;
import com.projects.company.homes_lock.models.datamodels.response.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.UserModel;

import java.util.List;

//,
//        foreignKeys = @ForeignKey(
//        entity = User.class,
//        parentColumns = "objectId",
//        childColumns = "objectId",
//        onDelete = CASCADE)
@Entity(tableName = "device")
public class Device extends BaseModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "bleDeviceName")
    @SerializedName("bleDeviceName")
    private String mBleDeviceName;

    @ColumnInfo(name = "bleDeviceMacAddress")
    @SerializedName("bleDeviceMacAddress")
    private String mBleDeviceMacAddress;

    @ColumnInfo(name = "serialNumber")
    @SerializedName("serialNumber")
    private String mSerialNumber;

    @ColumnInfo(name = "lockStatus")
    @SerializedName("lockStatus")
    private Boolean mLockStatus;

    @ColumnInfo(name = "doorStatus")
    @SerializedName("doorStatus")
    private Boolean mDoorStatus;

    @ColumnInfo(name = "internetStatus")
    @SerializedName("internetStatus")
    private Boolean mInternetStatus;

    @ColumnInfo(name = "batteryStatus")
    @SerializedName("batteryStatus")
    private Integer mBatteryStatus;

    @ColumnInfo(name = "wifiStatus")
    @SerializedName("wifiStatus")
    private Boolean mWifiStatus;

    @ColumnInfo(name = "wifiStrength")
    @SerializedName("wifiStrength")
    private Integer mWifiStrength;

    @ColumnInfo(name = "meanPowerCons")
    @SerializedName("meanPowerCons")
    private Integer mMeanPowerCons;

    @ColumnInfo(name = "temperature")
    @SerializedName("temperature")
    private Integer mTemperature;

    @ColumnInfo(name = "humidity")
    @SerializedName("humidity")
    private Integer mHumidity;

    @ColumnInfo(name = "coLevel")
    @SerializedName("coLevel")
    private Integer mCOLevel;

    @ColumnInfo(name = "deviceHealth")
    @SerializedName("deviceHealth")
    private Boolean mDeviceHealth;

    @ColumnInfo(name = "fwVersion")
    @SerializedName("fwVersion")
    private Integer mFWVersion;

    @ColumnInfo(name = "lockPosition")
    @SerializedName("lockPosition")
    private Integer mLockPosition;

    //region other server attributes
    @Ignore
    @SerializedName("created")
    private Long mCreatedAt;

    @Ignore
    @SerializedName("ownerId")
    private String mOwnerId;

    @Ignore
    @SerializedName("updated")
    private Long mUpdated;

    @Ignore
    @SerializedName("___class")
    private String mServerTableName;

    @Ignore
    @SerializedName("relatedUsers")
    private List<UserLockModel> mRelatedUsers;

    @Ignore
    @SerializedName("relatedErrors")
    private List<DeviceErrorModel> mRelatedErrors;

    @Ignore
    @SerializedName("user")
    private UserModel mUser;
    //endregion other server attributes

    public Device(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
        this.mBleDeviceName = "BlueNRG";
        this.mBleDeviceMacAddress = "02:80:E1:00:34:12";
        this.mSerialNumber = mObjectId;
        this.mLockStatus = false;
        this.mDoorStatus = true;
        this.mInternetStatus = true;
        this.mBatteryStatus = 50;
        this.mWifiStatus = true;
        this.mWifiStrength = -20;
        this.mMeanPowerCons = 0;
        this.mTemperature = 10;
        this.mHumidity = 5;
        this.mCOLevel = 0;
        this.mDeviceHealth = true;
        this.mFWVersion = 1;
        this.mLockPosition = 1;
    }

    @Ignore
    public Device(
            @NonNull String mObjectId,
            String mBleDeviceName,
            String mBleDeviceMacAddress,
            String mSerialNumber,
            Boolean mLockStatus,
            Boolean mDoorStatus,
            Boolean mInternetStatus,
            Integer mBatteryStatus,
            Boolean mWifiStatus,
            Integer mWifiStrength,
            Integer mMeanPowerCons,
            Integer mTemperature,
            Integer mHumidity,
            Integer mCOLevel,
            Boolean mDeviceHealth,
            Integer mFWVersion,
            Integer mLockPosition) {
        this.mObjectId = mObjectId;
        this.mBleDeviceName = mBleDeviceName;
        this.mBleDeviceMacAddress = mBleDeviceMacAddress;
        this.mSerialNumber = mSerialNumber;
        this.mLockStatus = mLockStatus;
        this.mDoorStatus = mDoorStatus;
        this.mInternetStatus = mInternetStatus;
        this.mBatteryStatus = mBatteryStatus;
        this.mWifiStatus = mWifiStatus;
        this.mWifiStrength = mWifiStrength;
        this.mMeanPowerCons = mMeanPowerCons;
        this.mTemperature = mTemperature;
        this.mHumidity = mHumidity;
        this.mCOLevel = mCOLevel;
        this.mDeviceHealth = mDeviceHealth;
        this.mFWVersion = mFWVersion;
        this.mLockPosition = mLockPosition;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getBleDeviceName() {
        return mBleDeviceName;
    }

    public void setBleDeviceName(String mBleDeviceName) {
        this.mBleDeviceName = mBleDeviceName;
    }

    public String getBleDeviceMacAddress() {
        return mBleDeviceMacAddress;
    }

    public void setBleDeviceMacAddress(String mBleDeviceMacAddress) {
        this.mBleDeviceMacAddress = mBleDeviceMacAddress;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    public Boolean getLockStatus() {
        return mLockStatus;
    }

    public void setLockStatus(Boolean mLockStatus) {
        this.mLockStatus = mLockStatus;
    }

    public Boolean getDoorStatus() {
        return mDoorStatus;
    }

    public void setDoorStatus(Boolean mDoorStatus) {
        this.mDoorStatus = mDoorStatus;
    }

    public Boolean getInternetStatus() {
        return mInternetStatus;
    }

    public void setInternetStatus(Boolean mInternetStatus) {
        this.mInternetStatus = mInternetStatus;
    }

    public Integer getBatteryStatus() {
        return mBatteryStatus;
    }

    public void setBatteryStatus(Integer mBatteryStatus) {
        this.mBatteryStatus = mBatteryStatus;
    }

    public Boolean getWifiStatus() {
        return mWifiStatus;
    }

    public void setWifiStatus(Boolean mWifiStatus) {
        this.mWifiStatus = mWifiStatus;
    }

    public Integer getWifiStrength() {
        return mWifiStrength;
    }

    public void setWifiStrength(Integer mWifiStrength) {
        this.mWifiStrength = mWifiStrength;
    }

    public Integer getMeanPowerCons() {
        return mMeanPowerCons;
    }

    public void setMeanPowerCons(Integer mMeanPowerCons) {
        this.mMeanPowerCons = mMeanPowerCons;
    }

    public Integer getTemperature() {
        return mTemperature;
    }

    public void setTemperature(Integer mTemperature) {
        this.mTemperature = mTemperature;
    }

    public Integer getHumidity() {
        return mHumidity;
    }

    public void setHumidity(Integer mHumidity) {
        this.mHumidity = mHumidity;
    }

    public Integer getCOLevel() {
        return mCOLevel;
    }

    public void setCOLevel(Integer mCOLevel) {
        this.mCOLevel = mCOLevel;
    }

    public Boolean getDeviceHealth() {
        return mDeviceHealth;
    }

    public void setDeviceHealth(Boolean mDeviceHealth) {
        this.mDeviceHealth = mDeviceHealth;
    }

    public Integer getFWVersion() {
        return mFWVersion;
    }

    public void setFWVersion(Integer mFWVersion) {
        this.mFWVersion = mFWVersion;
    }

    public Integer getLockPosition() {
        return mLockPosition;
    }

    public void setLockPosition(Integer mLockPosition) {
        this.mLockPosition = mLockPosition;
    }
}