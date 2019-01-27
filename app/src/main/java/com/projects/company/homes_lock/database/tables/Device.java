package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;
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

    @ColumnInfo(name = "isLocked")
    @SerializedName("isLocked")
    private Boolean mIsLocked = false;

    @ColumnInfo(name = "isDoorClosed")
    @SerializedName("isDoorClosed")
    private Boolean mIsDoorClosed = false;

    @ColumnInfo(name = "internetStatus")
    @SerializedName("internetStatus")
    private Boolean mInternetStatus = false;

    @ColumnInfo(name = "batteryStatus")
    @SerializedName("batteryStatus")
    private Integer mBatteryPercentage;

    @ColumnInfo(name = "wifiStatus")
    @SerializedName("wifiStatus")
    private Boolean mWifiStatus = false;

    @ColumnInfo(name = "wifiStrength")
    @SerializedName("wifiStrength")
    private Integer mWifiStrength;

    @ColumnInfo(name = "meanPowerCons")
    @SerializedName("meanPowerCons")
    private Integer mMeanPowerCons;

    @ColumnInfo(name = "temperature")
    @SerializedName("temperature")
    private Double mTemperature;

    @ColumnInfo(name = "humidity")
    @SerializedName("humidity")
    private Double mHumidity;

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
            Boolean mIsLocked,
            Boolean mIsDoorClosed,
            Boolean mInternetStatus,
            Integer mBatteryStatus,
            Boolean mWifiStatus,
            Integer mWifiStrength,
            Integer mMeanPowerCons,
            Double mTemperature,
            Double mHumidity,
            Integer mCOLevel,
            Boolean mDeviceHealth,
            Integer mFWVersion,
            Integer mLockPosition) {
        this.mObjectId = mObjectId;
        this.mBleDeviceName = mBleDeviceName;
        this.mBleDeviceMacAddress = mBleDeviceMacAddress;
        this.mSerialNumber = mSerialNumber;
        this.mIsLocked = mIsLocked;
        this.mIsDoorClosed = mIsDoorClosed;
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
        this.mIsLocked = false;
        this.mIsDoorClosed = false;
        this.mInternetStatus = false;
        this.mBatteryPercentage = 0;
        this.mWifiStatus = false;
        this.mWifiStrength = 0;
        this.mMeanPowerCons = 0;
        this.mTemperature = 0D;
        this.mHumidity = 0D;
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
        this.mIsLocked = Boolean.valueOf(updatedLock.get("isLocked").toString());
        this.mIsDoorClosed = Boolean.valueOf(updatedLock.get("isDoorClosed").toString());
        this.mInternetStatus = Boolean.valueOf(updatedLock.get("internetStatus").toString());
        this.mBatteryPercentage = Integer.valueOf(updatedLock.get("batteryStatus").toString());
        this.mWifiStatus = Boolean.valueOf(updatedLock.get("wifiStatus").toString());
        this.mWifiStrength = Integer.valueOf(updatedLock.get("wifiStrength").toString());
        this.mMeanPowerCons = Integer.valueOf(updatedLock.get("meanPowerCons").toString());
        this.mTemperature = Double.valueOf(updatedLock.get("temperature").toString());
        this.mHumidity = Double.valueOf(updatedLock.get("humidity").toString());
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

    public Boolean getIsLocked() {
        return mIsLocked;
    }

    public void setIsLocked(Boolean mIsLocked) {
        this.mIsLocked = mIsLocked;
    }

    public Boolean getIsDoorClosed() {
        return mIsDoorClosed;
    }

    public void setIsDoorClosed(Boolean mIsDoorClosed) {
        this.mIsDoorClosed = mIsDoorClosed;
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

    public Double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(Double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public Double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(Double mHumidity) {
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