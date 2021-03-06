package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.UserLock;

import java.util.List;

@Dao
public abstract class LockUserDao {

    public LiveData<UserLock> getUserLockInfo(String mDeviceObjectId) {
        return getUserLock(mDeviceObjectId);
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
    abstract LiveData<UserLock> getUserLock(String mDeviceId);
}