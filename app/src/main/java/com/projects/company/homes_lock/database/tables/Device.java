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
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_SN;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_TYP;
import static com.projects.company.homes_lock.utils.helper.BleHelper.generateDeviceType;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;

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

    @ColumnInfo(name = "gti")
    @SerializedName("gti")
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

    @ColumnInfo(name = "err")
    @SerializedName("err")
    private String mError;

    @ColumnInfo(name = "registrationId")
    @SerializedName("registrationId")
    private String mRegistrationId = "NOT_SET";

    //region Ignore server attributes
    @Ignore
    @SerializedName("created")
    private Long mCreatedAt;

    @Ignore
    @SerializedName("ownerId")
    private String mOwnerId;

    @ColumnInfo(name = "updated")
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

    @ColumnInfo(name = "memberAdminStatus")
    private boolean mMemberAdminStatus;

    @ColumnInfo(name = "userDeviceObjectId")
    private String mUserDeviceObjectId = "NOT_SET";
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
            Boolean mDoorInstallation,
            String mError) {
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
        this.mError = mError;
        this.mMemberAdminStatus = false;
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
        if (mBleDeviceMacAddress != null)
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
        this.mError = "Every things is OK.";
        this.mMemberAdminStatus = true;
    }

    public Device(Map updatedLock) {
        handleObjectId(updatedLock);
        handleBleDeviceName(updatedLock);
        handleErr(updatedLock);
        handleBcq(updatedLock);
        handleGti(updatedLock);
        handleRgh(updatedLock);
        handleDid(updatedLock);
        handlePrd(updatedLock);
        handleTyp(updatedLock);
        handleHw(updatedLock);
        handleFw(updatedLock);
        handleDeviceHealth(updatedLock);
        handleDeviceRss(updatedLock);
        handleIsq(updatedLock);
        handleIsr(updatedLock);
        handleIsi(updatedLock);
        handleIsw(updatedLock);
        handleBat(updatedLock);
        handleIso(updatedLock);
        handleIsk(updatedLock);
        handleSn(updatedLock);
        handleMac(updatedLock);
        handleUpdated(updatedLock);
        handleMemberAdminStatus(updatedLock);
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

//    public int getUserAdminStatus() {
//        if (mRelatedUsers != null && mRelatedUsers.size() != 0)
//            return mRelatedUsers.get(0).getAdminStatus() ? DataHelper.MEMBER_STATUS_PRIMARY_ADMIN : DataHelper.MEMBER_STATUS_NOT_ADMIN;
//
//        return DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
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
    }

    public String getError() {
        return mError;
    }

    public void setError(String mError) {
        this.mError = mError;
    }

    private void handleObjectId(Map updatedLock) {
        if (updatedLock.containsKey("objectId") && updatedLock.get("objectId") != null)
            this.mObjectId = updatedLock.get("objectId").toString();
    }

    private void handleBleDeviceName(Map updatedLock) {
        if (updatedLock.containsKey("bleDeviceName") && updatedLock.get("bleDeviceName") != null)
            this.mBleDeviceName = updatedLock.get("bleDeviceName").toString();
    }

    private void handleMac(Map updatedLock) {
        if (updatedLock.containsKey("mac") && updatedLock.get("mac") != null)
            this.mBleDeviceMacAddress = updatedLock.get("mac").toString();
    }

    private void handleUpdated(Map updatedLock) {
        if (updatedLock.containsKey("updated") && updatedLock.get("updated") != null)
            this.mUpdated = Long.valueOf(updatedLock.get("updated").toString());
    }

    private void handleMemberAdminStatus(Map updatedLock) {
        if (updatedLock.containsKey("memberAdminStatus"))
            this.mMemberAdminStatus = Boolean.valueOf(
                    updatedLock.get("memberAdminStatus") != null ? updatedLock.get("memberAdminStatus").toString() : "false");
    }

    private void handleSn(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_SN) && updatedLock.get(BLE_COMMAND_SN) != null)
            this.mSerialNumber = updatedLock.get(BLE_COMMAND_SN).toString();
    }

    private void handleIsk(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISK))
            this.mIsLocked = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISK) != null ? updatedLock.get(BLE_COMMAND_ISK).toString() : "false");
    }

    private void handleIso(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISO))
            this.mIsDoorClosed = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISO) != null ? updatedLock.get(BLE_COMMAND_ISO).toString() : "false");
    }

    private void handleBat(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_BAT))
            this.mBatteryPercentage = Integer.valueOf(
                    updatedLock.get(BLE_COMMAND_BAT) != null ? updatedLock.get(BLE_COMMAND_BAT).toString() : "0");
    }

    private void handleIsw(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISW))
            this.mWifiStatus = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISW) != null ? updatedLock.get(BLE_COMMAND_ISW).toString() : "false");
    }

    private void handleIsi(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISI))
            this.mInternetStatus = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISI) != null ? updatedLock.get(BLE_COMMAND_ISI).toString() : "false");
    }

    private void handleIsr(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISR))
            this.mRestApiServer = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISR) != null ? updatedLock.get(BLE_COMMAND_ISR).toString() : "false");
    }

    private void handleIsq(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_ISQ))
            this.mMQTTServerStatus = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_ISQ) != null ? updatedLock.get(BLE_COMMAND_ISQ).toString() : "false");
    }

    private void handleDeviceRss(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_RSS))
            this.mWifiStrength = Integer.valueOf(
                    updatedLock.get(BLE_COMMAND_RSS) != null ? updatedLock.get(BLE_COMMAND_RSS).toString() : "0");
    }

    private void handleDeviceHealth(Map updatedLock) {
        if (updatedLock.containsKey("deviceHealth"))
            this.mDeviceHealth = Boolean.valueOf(
                    updatedLock.get("deviceHealth") != null ? updatedLock.get("deviceHealth").toString() : "true");
    }

    private void handleFw(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_FW) && updatedLock.get(BLE_COMMAND_FW) != null)
            this.mFWVersion = String.valueOf(updatedLock.get(BLE_COMMAND_FW).toString());
    }

    private void handleHw(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_HW) && updatedLock.get(BLE_COMMAND_HW) != null)
            this.mHWVersion = String.valueOf(updatedLock.get(BLE_COMMAND_HW).toString());
    }

    private void handleTyp(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_TYP) && updatedLock.get(BLE_COMMAND_TYP) != null)
            this.mDeviceType = String.valueOf(updatedLock.get(BLE_COMMAND_TYP).toString());
    }

    private void handlePrd(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_PRD) && updatedLock.get(BLE_COMMAND_PRD) != null)
            this.mProductionDate = String.valueOf(updatedLock.get(BLE_COMMAND_PRD).toString());
    }

    private void handleDid(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_DID) && updatedLock.get(BLE_COMMAND_DID) != null)
            this.mDynamicId = String.valueOf(updatedLock.get(BLE_COMMAND_DID).toString());
    }

    private void handleRgh(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_RGH))
            this.mDoorInstallation = Boolean.valueOf(
                    updatedLock.get(BLE_COMMAND_RGH) != null ? updatedLock.get(BLE_COMMAND_RGH).toString() : "true");
    }

    private void handleGti(Map updatedLock) {
        if (updatedLock.containsKey("gti") && updatedLock.get("gti") != null)
            this.mGateWayId = String.valueOf(updatedLock.get("gti").toString());
    }

    private void handleBcq(Map updatedLock) {
        if (updatedLock.containsKey(BLE_COMMAND_BCQ) && updatedLock.get(BLE_COMMAND_BCQ) != null) {
            this.mConnectedDevices = String.valueOf(updatedLock.get(BLE_COMMAND_BCQ).toString());
            this.mConnectedClientsCount = Integer.valueOf(this.mConnectedDevices.split(",")[1]);
            this.mConnectedServersCount = Integer.valueOf(this.mConnectedDevices.split(",")[2]);
        }
    }

    private void handleErr(Map updatedLock) {
        if (updatedLock.containsKey("err") && updatedLock.get("err") != null)
            this.mError = String.valueOf(updatedLock.get("err").toString());
    }

    public Long getUpdated() {
        return mUpdated;
    }

    public void setUpdated(Long mUpdated) {
        this.mUpdated = mUpdated;
    }

    public void setMemberAdminStatus(boolean mMemberAdminStatus) {
        this.mMemberAdminStatus = mMemberAdminStatus;
    }

    public int getUserAdminStatus() {
        return this.mMemberAdminStatus ? MEMBER_STATUS_PRIMARY_ADMIN : MEMBER_STATUS_NOT_ADMIN;
    }

    public boolean getMemberAdminStatus() {
        return this.mMemberAdminStatus;
    }

    public String getRegistrationId() {
        return mRegistrationId;
    }

    public void setRegistrationId(String mRegistrationId) {
        this.mRegistrationId = mRegistrationId;
    }

    public String getUserDeviceObjectId() {
        return mUserDeviceObjectId;
    }

    public void setUserDeviceObjectId(String mUserDeviceObjectId) {
        this.mUserDeviceObjectId = mUserDeviceObjectId;
    }
}