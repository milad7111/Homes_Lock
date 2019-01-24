package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity(tableName = "device")
public class Device extends BaseModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "bleDeviceMacAddress")
    @SerializedName("bleDeviceMacAddress")
    private String mBleDeviceMacAddress;

    @ColumnInfo(name = "serialNumber")
    @SerializedName("serialNumber")
    private String mSerialNumber;

    @Ignore
    @SerializedName("devicePassword")
    private String mDevicePassword;

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
    private Integer mBatteryPercentage;

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

    //region Ignore server attributes
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
    private List<UserLock> mRelatedUsers;

    @Ignore
    @SerializedName("relatedErrors")
    private List<DeviceError> mRelatedErrors;

    @Ignore
    @SerializedName("user")
    private User mUser;
    //endregion Ignore server attributes

    //region Not Database Attributes
    @ColumnInfo(name = "bleDeviceName")
    private String mBleDeviceName;

    @ColumnInfo(name = "favoriteStatus")
    private boolean mFavoriteStatus;
    //endregion Not Database Attributes

    //region Constructor
    //endregion Constructor

    public Device() {
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
        this.mBatteryPercentage = mBatteryStatus;
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

    public Device(TempDeviceModel device) {
        this.mObjectId = device.getDeviceSerialNumber();
        this.mBleDeviceName = device.getDeviceName();
        this.mBleDeviceMacAddress = device.getDeviceMacAddress();
        this.mSerialNumber = device.getDeviceSerialNumber();
        this.mFavoriteStatus = device.isFavoriteLock();
        this.mLockStatus = false;
        this.mDoorStatus = false;
        this.mInternetStatus = false;
        this.mBatteryPercentage = 0;
        this.mWifiStatus = false;
        this.mWifiStrength = 0;
        this.mMeanPowerCons = 0;
        this.mTemperature = 0;
        this.mHumidity = 0;
        this.mCOLevel = 0;
        this.mDeviceHealth = false;
        this.mFWVersion = 0;
        this.mLockPosition = 0;
    }

    public Device(Map updatedLock) {
        this.mObjectId = updatedLock.get("objectId").toString();
        this.mBleDeviceName = updatedLock.get("bleDeviceName").toString();
        this.mBleDeviceMacAddress = updatedLock.get("bleDeviceMacAddress").toString();
        this.mSerialNumber = updatedLock.get("serialNumber").toString();
        this.mLockStatus = Boolean.valueOf(updatedLock.get("lockStatus").toString());
        this.mDoorStatus = Boolean.valueOf(updatedLock.get("doorStatus").toString());
        this.mInternetStatus = Boolean.valueOf(updatedLock.get("internetStatus").toString());
        this.mBatteryPercentage = Integer.valueOf(updatedLock.get("batteryStatus").toString());
        this.mWifiStatus = Boolean.valueOf(updatedLock.get("wifiStatus").toString());
        this.mWifiStrength = Integer.valueOf(updatedLock.get("wifiStrength").toString());
        this.mMeanPowerCons = Integer.valueOf(updatedLock.get("meanPowerCons").toString());
        this.mTemperature = Integer.valueOf(updatedLock.get("temperature").toString());
        this.mHumidity = Integer.valueOf(updatedLock.get("humidity").toString());
        this.mCOLevel = Integer.valueOf(updatedLock.get("coLevel").toString());
        this.mDeviceHealth = Boolean.valueOf(updatedLock.get("deviceHealth").toString());
        this.mFWVersion = Integer.valueOf(updatedLock.get("fwVersion").toString());
        this.mLockPosition = Integer.valueOf(updatedLock.get("lockPosition").toString());
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

    public Integer getBatteryPercentage() {
        return mBatteryPercentage;
    }

    public void setBatteryPercentage(Integer mBatteryPercentage) {
        this.mBatteryPercentage = mBatteryPercentage;
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

    public void setUserLocks(List<UserLock> userLocks) {
        mRelatedUsers = userLocks;
    }

    public void setUserLocks(UserLock userLocks) {
        mRelatedUsers = new ArrayList<>();
        mRelatedUsers.add(userLocks);
    }

    public int getMemberAdminStatus() {
        if (mRelatedUsers != null && mRelatedUsers.size() != 0)
            return mRelatedUsers.get(0).getAdminStatus() ? DataHelper.MEMBER_STATUS_PRIMARY_ADMIN : DataHelper.MEMBER_STATUS_NOT_ADMIN;

        return -1;
    }

    public boolean isLockSavedInServerByCheckUserLocks() {
        if (mRelatedUsers != null)
            return mRelatedUsers.size() != 0;

        return false;
    }

    public int getAdminMembersCount() {
        int count = 0;
        for (UserLock userLock : mRelatedUsers)
            if (userLock.getAdminStatus())
                count++;

        return count;
    }

    public String getUserLockObjectId() {
        if (mRelatedUsers != null && mRelatedUsers.size() != 0)
            return mRelatedUsers.get(0).getObjectId();

        return null;
    }

    public boolean isLockSavedInServer(){
        return !this.getObjectId().equals(this.getSerialNumber());
    }

    public void setFavoriteStatus(boolean mFavoriteStatus) {
        this.mFavoriteStatus = mFavoriteStatus;
    }

    public boolean isFavoriteStatus() {
        return mFavoriteStatus;
    }
}