package com.projects.company.homes_lock.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.UserLock;

import java.util.List;

@Dao
public abstract class LockUserViewDao {

    public Device getUserLockInfo(String objectId) {

        Device mDevice = getDevice(objectId);
        mDevice.setUserLocks(getUserLock(objectId));

        return mDevice;
    }

    public List<Device> getAllUserLocks() {
        List<Device> mDevices = getDevices();

        for (Device device : mDevices)
            device.setUserLocks(getUserLocks(device.getObjectId()));

        return mDevices;
    }

    @Query("SELECT * FROM device WHERE objectId = :mObjectId")
    abstract Device getDevice(String mObjectId);

    @Query("SELECT * FROM device")
    abstract List<Device> getDevices();

    @Query("SELECT * FROM userLock WHERE deviceId = :mDeviceId")
    abstract List<UserLock> getUserLocks(String mDeviceId);

    @Query("SELECT * FROM userLock WHERE deviceId = :mDeviceId")
    abstract UserLock getUserLock(String mDeviceId);
}