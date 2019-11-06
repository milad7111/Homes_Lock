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
    void delete(Device device);

    @Update
    void update(Device... device);

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getAllDevices();

    @Query("SELECT COUNT(*) FROM device")
    LiveData<Integer> getAllDevicesCount();

    @Query("UPDATE device SET isk = :mIsLocked WHERE objectId = :mDeviceObjectId")
    void setIsLocked(final String mDeviceObjectId, boolean mIsLocked);

    @Query("SELECT * FROM device WHERE objectId = :mDeviceObjectId")
    LiveData<Device> getDevice(final String mDeviceObjectId);

    @Query("SELECT * FROM device WHERE sn = :mDeviceSerialNumber")
    LiveData<Device> getDeviceBySerialNumber(final String mDeviceSerialNumber);

    @Query("UPDATE device SET iso = :mIsDoorClosed WHERE objectId = :mDeviceObjectId")
    void setIsDoorClosed(final String mDeviceObjectId, boolean mIsDoorClosed);

    @Query("UPDATE device SET bat = :mBatteryStatus WHERE objectId = :mDeviceObjectId")
    void setBatteryStatus(String mDeviceObjectId, int mBatteryStatus);

    @Query("UPDATE device SET isw = :mWifiStatus WHERE objectId = :mDeviceObjectId")
    void setWifiStatus(String mDeviceObjectId, boolean mWifiStatus);

    @Query("UPDATE device SET rss = :mWifiStrength WHERE objectId = :mDeviceObjectId")
    void setWifiStrength(String mDeviceObjectId, int mWifiStrength);

    @Query("UPDATE device SET isi = :mInternetStatus WHERE objectId = :mDeviceObjectId")
    void setInternetStatus(String mDeviceObjectId, boolean mInternetStatus);

    @Query("UPDATE device SET isq = :mMQTTServerStatus WHERE objectId = :mDeviceObjectId")
    void setMQTTServerStatus(String mDeviceObjectId, boolean mMQTTServerStatus);

    @Query("UPDATE device SET isr = :mRestApiServerStatus WHERE objectId = :mDeviceObjectId")
    void setRestApiServerStatus(String mDeviceObjectId, boolean mRestApiServerStatus);

    @Query("UPDATE device SET typ = :deviceType  WHERE objectId = :mDeviceObjectId")
    void setDeviceType(String mDeviceObjectId, String deviceType);

    @Query("UPDATE device SET fw = :firmwareVersion  WHERE objectId = :mDeviceObjectId")
    void setFirmwareVersion(String mDeviceObjectId, String firmwareVersion);

    @Query("UPDATE device SET hw = :hardwareVersion  WHERE objectId = :mDeviceObjectId")
    void setHardwareVersion(String mDeviceObjectId, String hardwareVersion);

    @Query("UPDATE device SET prd = :productionDate  WHERE objectId = :mDeviceObjectId")
    void setProductionDate(String mDeviceObjectId, String productionDate);

    @Query("UPDATE device SET sn = :serialNumber  WHERE objectId = :mDeviceObjectId")
    void setSerialNumber(String mDeviceObjectId, String serialNumber);

    @Query("UPDATE device SET did = :dynamicId  WHERE objectId = :mDeviceObjectId")
    void setDynamicId(String mDeviceObjectId, String dynamicId);

    @Query("UPDATE device SET bcq = :bcq  WHERE objectId = :mDeviceObjectId")
    void setConnectedDevicesCount(String mDeviceObjectId, String bcq);

    @Query("UPDATE device SET connectedClientsCount = :connectedClientsCount  WHERE objectId = :mDeviceObjectId")
    void setConnectedClientsCount(String mDeviceObjectId, int connectedClientsCount);

    @Query("UPDATE device SET connectedServersCount = :connectedServersCount  WHERE objectId = :mDeviceObjectId")
    void setConnectedServersCount(String mDeviceObjectId, int connectedServersCount);

    @Query("UPDATE device SET rgh = :doorInstallation  WHERE objectId = :mDeviceObjectId")
    void setDoorInstallation(String mDeviceObjectId, Boolean doorInstallation);

    @Query("UPDATE device SET cfg = :configStatus  WHERE objectId = :mDeviceObjectId")
    void setConfigStatus(String mDeviceObjectId, boolean configStatus);

    @Query("DELETE FROM device")
    void clearAllData();
}