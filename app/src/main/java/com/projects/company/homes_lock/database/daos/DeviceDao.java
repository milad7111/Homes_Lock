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

    @Query("DELETE FROM device")
    void deleteAll();

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getAllDevices();

    @Query("SELECT COUNT(*) FROM device")
    LiveData<Integer> getAllDevicesCount();

    @Query("UPDATE device SET lockStatus = :mLockStatus WHERE objectId = :mDeviceObjectId")
    void setLockStatus(final String mDeviceObjectId, boolean mLockStatus);

    @Query("SELECT * FROM device WHERE objectId = :mDeviceObjectId")
    LiveData<Device> getDevice(final String mDeviceObjectId);

    @Query("UPDATE device SET doorStatus = :mDoorStatus WHERE objectId = :mDeviceObjectId")
    void setDoorStatus(final String mDeviceObjectId, int mDoorStatus);

    @Query("UPDATE device SET batteryStatus = :mBatteryStatus WHERE objectId = :mDeviceObjectId")
    void setBatteryStatus(String mDeviceObjectId, int mBatteryStatus);

    @Query("UPDATE device SET wifiStatus = :mWifiStatus & internetStatus = :mInternetStatus & wifiStrength = :mWifiStrength  WHERE objectId = :mDeviceObjectId")
    void setConnectionStatus(String mDeviceObjectId, boolean mWifiStatus, boolean mInternetStatus, int mWifiStrength);

    @Query("UPDATE device SET temperature = :mTemperature  WHERE objectId = :mDeviceObjectId")
    void setTemperature(String mDeviceObjectId, byte mTemperature);

    @Query("UPDATE device SET humidity = :mHumidity  WHERE objectId = :mDeviceObjectId")
    void setHumidity(String mDeviceObjectId, byte mHumidity);

    @Query("UPDATE device SET coLevel = :mCoLevel  WHERE objectId = :mDeviceObjectId")
    void setCoLevel(String mDeviceObjectId, byte mCoLevel);
}