package com.projects.company.homes_lock.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.UserLock;

import java.util.List;

@Dao
public abstract class LockUserViewDao {

    public List<Device> getAllUserLocks() {
        List<Device> mDevices = getDevices();

        for (Device device : mDevices)
            device.setUserLocks(getUserLocks(device.getObjectId()));

        return mDevices;
    }

    @Query("SELECT * FROM device")
    abstract List<Device> getDevices();

    @Query("SELECT * FROM userLock WHERE deviceId = :mDeviceId")
    abstract List<UserLock> getUserLocks(String mDeviceId);
}