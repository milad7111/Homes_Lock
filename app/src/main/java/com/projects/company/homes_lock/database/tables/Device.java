package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "device")
public class Device {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "lockSSID")
    private String mLockSSID;

    @ColumnInfo(name = "serialNumber")
    private String mSerialNumber;

    @ColumnInfo(name = "lockStatus")
    private boolean mLockStatus;

    @ColumnInfo(name = "doorStatus")
    private boolean mDoorStatus;

    @ColumnInfo(name = "connectionStatus")
    private boolean mConnectionStatus;

    @ColumnInfo(name = "batteryStatus")
    private int mBatteryStatus;

    @ColumnInfo(name = "wifiStatus")
    private int mWifiStatus;

    @ColumnInfo(name = "meanPowerCons")
    private int mMeanPowerCons;

    @ColumnInfo(name = "temperature")
    private int mTemperature;

    @ColumnInfo(name = "humidity")
    private int mHumidity;

    @ColumnInfo(name = "coLevel")
    private int mCOLevel;

    @ColumnInfo(name = "deviceHealth")
    private boolean mDeviceHealth;

    @ColumnInfo(name = "fwVersion")
    private int mFWVersion;

    @ColumnInfo(name = "lockPosition")
    private int mLockPosition;

    public Device(
            @NonNull String mObjectId,
            String mLockSSID,
            String mSerialNumber,
            boolean mLockStatus,
            boolean mDoorStatus,
            boolean mConnectionStatus,
            int mBatteryStatus,
            int mWifiStatus,
            int mMeanPowerCons,
            int mTemperature,
            int mHumidity,
            int mCOLevel,
            boolean mDeviceHealth,
            int mFWVersion,
            int mLockPosition) {
        this.mObjectId = mObjectId;
        this.mLockSSID = mLockSSID;
        this.mSerialNumber = mSerialNumber;
        this.mLockStatus = mLockStatus;
        this.mDoorStatus = mDoorStatus;
        this.mConnectionStatus = mConnectionStatus;
        this.mBatteryStatus = mBatteryStatus;
        this.mWifiStatus = mWifiStatus;
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

    public String getLockSSID() {
        return mLockSSID;
    }

    public void setLockSSID(String mLockSSID) {
        this.mLockSSID = mLockSSID;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    public boolean getLockStatus() {
        return mLockStatus;
    }

    public void setLockStatus(boolean mLockStatus) {
        this.mLockStatus = mLockStatus;
    }

    public boolean getDoorStatus() {
        return mDoorStatus;
    }

    public void setDoorStatus(boolean mDoorStatus) {
        this.mDoorStatus = mDoorStatus;
    }

    public boolean getConnectionStatus() {
        return mConnectionStatus;
    }

    public void setConnectionStatus(boolean mConnectionStatus) {
        this.mConnectionStatus = mConnectionStatus;
    }

    public int getBatteryStatus() {
        return mBatteryStatus;
    }

    public void setBatteryStatus(int mBatteryStatus) {
        this.mBatteryStatus = mBatteryStatus;
    }

    public int getWifiStatus() {
        return mWifiStatus;
    }

    public void setWifiStatus(int mWifiStatus) {
        this.mWifiStatus = mWifiStatus;
    }

    public int getMeanPowerCons() {
        return mMeanPowerCons;
    }

    public void setMeanPowerCons(int mMeanPowerCons) {
        this.mMeanPowerCons = mMeanPowerCons;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int mTemperature) {
        this.mTemperature = mTemperature;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getCOLevel() {
        return mCOLevel;
    }

    public void setCOLevel(int mCOLevel) {
        this.mCOLevel = mCOLevel;
    }

    public boolean getDeviceHealth() {
        return mDeviceHealth;
    }

    public void setDeviceHealth(boolean mDeviceHealth) {
        this.mDeviceHealth = mDeviceHealth;
    }

    public int getFWVersion() {
        return mFWVersion;
    }

    public void setFWVersion(int mFWVersion) {
        this.mFWVersion = mFWVersion;
    }

    public int getLockPosition() {
        return mLockPosition;
    }

    public void setLockPosition(int mLockPosition) {
        this.mLockPosition = mLockPosition;
    }
}