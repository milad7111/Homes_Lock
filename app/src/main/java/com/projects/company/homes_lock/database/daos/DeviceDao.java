package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.projects.company.homes_lock.database.tables.Device;

import java.util.List;

@Dao
public interface DeviceDao extends BaseDao<Device> {

    @Insert
    void insert(Device device);

    @Delete
    void delete(Device... device);

    @Update
    void update(Device... device);

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getAllDevices();

    @Query("SELECT COUNT(*) FROM device")
    LiveData<Integer> getAllDevicesCount();

    @Query("UPDATE device SET isLocked = :mIsLocked WHERE objectId = :mDeviceObjectId")
    void setIsLocked(final String mDeviceObjectId, boolean mIsLocked);

    @Query("SELECT * FROM device WHERE objectId = :mDeviceObjectId")
    LiveData<Device> getDevice(final String mDeviceObjectId);

    @Query("UPDATE device SET isDoorClosed = :mIsDoorClosed WHERE objectId = :mDeviceObjectId")
    void setIsDoorClosed(final String mDeviceObjectId, boolean mIsDoorClosed);

    @Query("UPDATE device SET batteryStatus = :mBatteryStatus WHERE objectId = :mDeviceObjectId")
    void setBatteryStatus(String mDeviceObjectId, int mBatteryStatus);

    @Query("UPDATE device SET wifiStatus = :mWifiStatus WHERE objectId = :mDeviceObjectId")
    void setWifiStatus(String mDeviceObjectId, boolean mWifiStatus);

    @Query("UPDATE device SET wifiStrength = :mWifiStrength WHERE objectId = :mDeviceObjectId")
    void setWifiStrength(String mDeviceObjectId, int mWifiStrength);

    @Query("UPDATE device SET internetStatus = :mInternetStatus WHERE objectId = :mDeviceObjectId")
    void setInternetStatus(String mDeviceObjectId, boolean mInternetStatus);

    @Query("UPDATE device SET mqttServerStatus = :mMQTTServerStatus WHERE objectId = :mDeviceObjectId")
    void setMQTTServerStatus(String mDeviceObjectId, boolean mMQTTServerStatus);

    @Query("UPDATE device SET restApiServerStatus = :mRestApiServerStatus WHERE objectId = :mDeviceObjectId")
    void setRestApiServerStatus(String mDeviceObjectId, boolean mRestApiServerStatus);

    @Query("UPDATE device SET temperature = :mTemperature  WHERE objectId = :mDeviceObjectId")
    void setTemperature(String mDeviceObjectId, byte mTemperature);

    @Query("UPDATE device SET humidity = :mHumidity  WHERE objectId = :mDeviceObjectId")
    void setHumidity(String mDeviceObjectId, byte mHumidity);

    @Query("UPDATE device SET coLevel = :mCoLevel  WHERE objectId = :mDeviceObjectId")
    void setCoLevel(String mDeviceObjectId, byte mCoLevel);

    @Query("DELETE FROM device")
    void clearAllData();
}