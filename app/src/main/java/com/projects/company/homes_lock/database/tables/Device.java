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
import java.util.Objects;

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

    @ColumnInfo(name = "mqttServerStatus")
    private Boolean mMQTTServerStatus = false;

    @ColumnInfo(name = "restApiServerStatus")
    private Boolean mRestApiServer = false;

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
    private String mFWVersion;

    @ColumnInfo(name = "hwVersion")
    @SerializedName("hwVersion")
    private String mHWVersion;

    @ColumnInfo(name = "deviceType")
    @SerializedName("deviceType")
    private String mDeviceType;

    @ColumnInfo(name = "productionDate")
    @SerializedName("productionDate")
    private String mProductionDate;

    @ColumnInfo(name = "dynamicId")
    @SerializedName("dynamicId")
    private String mDynamicId;

    @ColumnInfo(name = "connectedDevicesCount")
//    @SerializedName("connectedDevicesCount")
    private int mConnectedDevicesCount;

    @ColumnInfo(name = "doorInstallation")
    @SerializedName("doorInstallation")
    private Boolean mDoorInstallation;

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
            String mFWVersion,
            String mHWVersion,
            String mDeviceType,
            String mProductionDate,
            String mDynamicId,
            int mConnectedDevicesCount,
            Boolean mDoorInstallation,
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
        this.mHWVersion = mHWVersion;
        this.mDeviceType = mDeviceType;
        this.mProductionDate = mProductionDate;
        this.mDynamicId = mDynamicId;
        this.mDoorInstallation = mDoorInstallation;
        this.mConnectedDevicesCount = mConnectedDevicesCount;
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
        this.mFWVersion = "0.0.0";
        this.mHWVersion = "0.0.0";
        this.mDeviceType = generateDeviceType();
        this.mProductionDate = "0000:00:00 00:00";
        this.mDynamicId = "000_0000_000_0000";
        this.mConnectedDevicesCount = 1;
        this.mDoorInstallation = true;
        this.mLockPosition = 0;
    }

    public Device(Map updatedLock) {
        this.mObjectId = Objects.requireNonNull(updatedLock.get("objectId")).toString();
        this.mBleDeviceName = Objects.requireNonNull(updatedLock.get("bleDeviceName")).toString();
        this.mBleDeviceMacAddress = Objects.requireNonNull(updatedLock.get("bleDeviceMacAddress")).toString();
        this.mSerialNumber = Objects.requireNonNull(updatedLock.get("serialNumber")).toString();
        this.mIsLocked = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("isLocked")).toString());
        this.mIsDoorClosed = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("isDoorClosed")).toString());
        this.mInternetStatus = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("internetStatus")).toString());
        this.mBatteryPercentage = Integer.valueOf(Objects.requireNonNull(updatedLock.get("batteryStatus")).toString());
        this.mWifiStatus = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("wifiStatus")).toString());
        this.mWifiStrength = Integer.valueOf(Objects.requireNonNull(updatedLock.get("wifiStrength")).toString());
        this.mMeanPowerCons = Integer.valueOf(Objects.requireNonNull(updatedLock.get("meanPowerCons")).toString());
        this.mTemperature = Double.valueOf(Objects.requireNonNull(updatedLock.get("temperature")).toString());
        this.mHumidity = Double.valueOf(Objects.requireNonNull(updatedLock.get("humidity")).toString());
        this.mCOLevel = Integer.valueOf(Objects.requireNonNull(updatedLock.get("coLevel")).toString());
        this.mDeviceHealth = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("deviceHealth")).toString());
        this.mFWVersion = String.valueOf(Objects.requireNonNull(updatedLock.get("fwVersion")).toString());
        this.mHWVersion = String.valueOf(Objects.requireNonNull(updatedLock.get("hwVersion")).toString());
        this.mDeviceType = String.valueOf(Objects.requireNonNull(updatedLock.get("deviceType")).toString());
        this.mProductionDate = String.valueOf(Objects.requireNonNull(updatedLock.get("productionDate")).toString());
        this.mDynamicId = String.valueOf(Objects.requireNonNull(updatedLock.get("dynamicId")).toString());
        this.mDoorInstallation = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("doorInstallation")).toString());
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

    public void setInternetStatus(Boolean mInternetStatus) {
        this.mInternetStatus = mInternetStatus;
    }

    public Boolean getMQTTServerStatus() {
        return mMQTTServerStatus;
    }

    public void setMQTTServerStatus(Boolean mMQTTServerStatus) {
        this.mMQTTServerStatus = mMQTTServerStatus;
    }

    public Boolean getRestApiServer() {
        return mRestApiServer;
    }

    public void setRestApiServer(Boolean mRestApiServer) {
        this.mRestApiServer = mRestApiServer;
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

    public String getFWVersion() {
        return mFWVersion;
    }

    public void setFWVersion(String mFWVersion) {
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

        return DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
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

    public boolean isLockSavedInServer() {
        return !this.getObjectId().equals(this.getSerialNumber());
    }

    public void setFavoriteStatus(boolean mFavoriteStatus) {
        this.mFavoriteStatus = mFavoriteStatus;
    }

    public boolean isFavoriteStatus() {
        return mFavoriteStatus;
    }

    public String getHWVersion() {
        return mHWVersion;
    }

    public void setHWVersion(String mHWVersion) {
        this.mHWVersion = mHWVersion;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String mDeviceType) {
        this.mDeviceType = mDeviceType;
    }

    public String getProductionDate() {
        return mProductionDate;
    }

    public void setProductionDate(String mProductionDate) {
        this.mProductionDate = mProductionDate;
    }

    public String getDynamicId() {
        return mDynamicId;
    }

    public void setDynamicId(String mDynamicId) {
        this.mDynamicId = mDynamicId;
    }

    public Boolean getDoorInstallation() {
        return mDoorInstallation;
    }

    public void setDoorInstallation(Boolean mDoorInstallation) {
        this.mDoorInstallation = mDoorInstallation;
    }

    public void setConnectedDevicesCount(int mConnectedDevicesCount) {
        this.mConnectedDevicesCount = mConnectedDevicesCount;
    }

    public int getConnectedDevicesCount() {
        return mConnectedDevicesCount;
    }

    private String generateDeviceType() {
        switch (mBleDeviceMacAddress.split(":")[1]) {
            case "6E":
                return "LOCK";
            case "6F":
                return "GATEWAY";
            default:
                return "UNKNOWN";
        }
    }
}