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

import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BAT;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BCQ;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DID;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_FW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_HW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISI;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISO;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISQ;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PRD;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_RGH;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_RSS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_TYP;
import static com.projects.company.homes_lock.utils.helper.BleHelper.generateDeviceType;

@Entity(tableName = "device")
public class Device extends BaseModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "mac")
    @SerializedName("mac")
    private String mBleDeviceMacAddress;

    @ColumnInfo(name = "gwi")
    @SerializedName("gwi")
    private String mGateWayId;

    @ColumnInfo(name = "bcq")
    @SerializedName("bcq")
    private String mConnectedDevices;

    @ColumnInfo(name = "sn")
    @SerializedName("sn")
    private String mSerialNumber;

    @ColumnInfo(name = "isk")
    @SerializedName("isk")
    private Boolean mIsLocked = false;

    @ColumnInfo(name = "iso")
    @SerializedName("iso")
    private Boolean mIsDoorClosed = false;

    @ColumnInfo(name = "isi")
    @SerializedName("isi")
    private Boolean mInternetStatus = false;

    @ColumnInfo(name = "isr")
    @SerializedName("isr")
    private Boolean mRestApiServer = false;

    @ColumnInfo(name = "isq")
    @SerializedName("isq")
    private Boolean mMQTTServerStatus = false;

    @ColumnInfo(name = "bat")
    @SerializedName("bat")
    private Integer mBatteryPercentage;

    @ColumnInfo(name = "isw")
    @SerializedName("isw")
    private Boolean mWifiStatus = false;

    @ColumnInfo(name = "rss")
    @SerializedName("rss")
    private Integer mWifiStrength;

    @ColumnInfo(name = "deviceHealth")
    @SerializedName("deviceHealth")
    private Boolean mDeviceHealth;

    @ColumnInfo(name = "fw")
    @SerializedName("fw")
    private String mFWVersion;

    @ColumnInfo(name = "hw")
    @SerializedName("hw")
    private String mHWVersion;

    @ColumnInfo(name = "typ")
    @SerializedName("typ")
    private String mDeviceType;

    @ColumnInfo(name = "prd")
    @SerializedName("prd")
    private String mProductionDate;

    @ColumnInfo(name = "did")
    @SerializedName("did")
    private String mDynamicId;

    @ColumnInfo(name = "rgh")
    @SerializedName("rgh")
    private Boolean mDoorInstallation;

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

    //region Not Server Attributes
    @ColumnInfo(name = "bleDeviceName")
    private String mBleDeviceName;

    @ColumnInfo(name = "favoriteStatus")
    private boolean mFavoriteStatus;

    @ColumnInfo(name = "connectedClientsCount")
    private int mConnectedClientsCount;

    @ColumnInfo(name = "connectedServersCount")
    private int mConnectedServersCount;
    //endregion Not Server Attributes

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
            Boolean mDeviceHealth,
            String mFWVersion,
            String mHWVersion,
            String mDeviceType,
            String mProductionDate,
            String mDynamicId,
            int mConnectedClientsCount,
            int mConnectedServersCount,
            Boolean mDoorInstallation) {
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
        this.mDeviceHealth = mDeviceHealth;
        this.mFWVersion = mFWVersion;
        this.mHWVersion = mHWVersion;
        this.mDeviceType = mDeviceType;
        this.mProductionDate = mProductionDate;
        this.mDynamicId = mDynamicId;
        this.mDoorInstallation = mDoorInstallation;
        this.mConnectedClientsCount = mConnectedClientsCount;
        this.mConnectedServersCount = mConnectedServersCount;
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
        this.mDeviceHealth = false;
        this.mFWVersion = "0.0.0";
        this.mHWVersion = "0.0.0";
        this.mDeviceType = generateDeviceType(mBleDeviceMacAddress.split(":")[1]);
        this.mProductionDate = "0000:00:00";
        this.mDynamicId = "0.0.0";
        this.mConnectedClientsCount = 1;
        this.mConnectedServersCount = 0;
        this.mDoorInstallation = true;
        this.mGateWayId = "";
        this.mConnectedDevices = "0,0,0";
        this.mConnectedClientsCount = 0;
        this.mConnectedServersCount = 0;
    }

    public Device(Map updatedLock) {
        this.mObjectId = Objects.requireNonNull(updatedLock.get("objectId")).toString();
        this.mBleDeviceName = Objects.requireNonNull(updatedLock.get("bleDeviceName")).toString();
        this.mBleDeviceMacAddress = Objects.requireNonNull(updatedLock.get("mac")).toString();
        this.mSerialNumber = Objects.requireNonNull(updatedLock.get("sn")).toString();
        this.mIsLocked = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISK)).toString());
        this.mIsDoorClosed = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISO)).toString());
        this.mBatteryPercentage = Integer.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_BAT)).toString());
        this.mWifiStatus = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISW)).toString());
        this.mInternetStatus = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISI)).toString());
        this.mRestApiServer = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISR)).toString());
        this.mMQTTServerStatus = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_ISQ)).toString());
        this.mWifiStrength = Integer.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_RSS)).toString());
        this.mDeviceHealth = Boolean.valueOf(Objects.requireNonNull(updatedLock.get("deviceHealth")).toString());
        this.mFWVersion = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_FW)).toString());
        this.mHWVersion = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_HW)).toString());
        this.mDeviceType = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_TYP)).toString());
        this.mProductionDate = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_PRD)).toString());
        this.mDynamicId = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_DID)).toString());
        this.mDoorInstallation = Boolean.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_RGH)).toString());
        this.mGateWayId = String.valueOf(Objects.requireNonNull(updatedLock.get("gwi")).toString());
        this.mConnectedDevices = String.valueOf(Objects.requireNonNull(updatedLock.get(BLE_COMMAND_BCQ)).toString());
        this.mConnectedClientsCount = Integer.valueOf(this.mConnectedDevices.split(",")[1]);
        this.mConnectedServersCount = Integer.valueOf(this.mConnectedDevices.split(",")[2]);
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

//    public boolean isLockSavedInServerByCheckUserLocks() {
//        if (mRelatedUsers != null)
//            return mRelatedUsers.size() != 0;
//
//        return false;
//    }

//    public int getAdminMembersCount() {
//        int count = 0;
//        for (UserLock userLock : mRelatedUsers)
//            if (userLock.getAdminStatus())
//                count++;
//
//        return count;
//    }

    public String getUserLockObjectId() {
        if (mRelatedUsers != null && mRelatedUsers.size() != 0)
            return mRelatedUsers.get(0).getObjectId();

        return null;
    }

    public boolean isDeviceSavedInServer() {
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

    public void setConnectedClientsCount(int mConnectedClientsCount) {
        this.mConnectedClientsCount = mConnectedClientsCount;
    }

    public int getConnectedClientsCount() {
        return mConnectedClientsCount;
    }

    public void setConnectedServersCount(int mConnectedServersCount) {
        this.mConnectedServersCount = mConnectedServersCount;
    }

    public int getConnectedServersCount() {
        return mConnectedServersCount;
    }

    public String getGateWayId() {
        return mGateWayId;
    }

    public void setGateWayId(String mGateWayId) {
        this.mGateWayId = mGateWayId;
    }

    public String getConnectedDevices() {
        return mConnectedDevices;
    }

    public void setConnectedDevices(String mConnectedDevices) {
        this.mConnectedDevices = mConnectedDevices;

        try {
            this.mConnectedClientsCount = Integer.valueOf(this.mConnectedDevices.split(",")[1]);
            this.mConnectedServersCount = Integer.valueOf(this.mConnectedDevices.split(",")[2]);
        } catch (Exception e){}
    }
}