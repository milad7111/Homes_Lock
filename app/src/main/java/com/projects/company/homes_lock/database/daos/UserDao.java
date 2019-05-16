package com.projects.company.homes_lock.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;

@Dao
public abstract class UserDao {

    public Long insert(User user) {
        Long id = _insertUser(user);

        if (user.getRelatedUserLocks() != null)
            for (UserLock userLock : user.getRelatedUserLocks()) {
                Device tempDevice = userLock.getRelatedDevice();
                tempDevice.setBleDeviceName(userLock.getDeviceName());

                tempDevice.setConnectedClientsCount(Integer.valueOf(tempDevice.getConnectedDevices().split(",")[1]));
                tempDevice.setConnectedServersCount(Integer.valueOf(tempDevice.getConnectedDevices().split(",")[2]));

                tempDevice.setMemberAdminStatus(userLock.getAdminStatus());

                insertDeviceForUser(tempDevice);

                userLock.setDeviceId(userLock.getRelatedDevice().getObjectId());
                userLock.setUserId(user.getObjectId());

                insertUserLockForUser(userLock);
            }

        return id;
    }


    public void deleteDevice(String mObjectId) {
        _deleteUserLockForUser(mObjectId);
        _deleteDevice(mObjectId);
    }

    private void insertDeviceForUser(Device device) {
        _insertDeviceForUser(device);
    }

    private void insertUserLockForUser(UserLock userLock) {
        _insertUserLockForUser(userLock);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract Long _insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void _insertDeviceForUser(Device device);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void _insertUserLockForUser(UserLock userLock);

    @Query("DELETE FROM userLock WHERE deviceId = :mDeviceId")
    abstract void _deleteUserLockForUser(String mDeviceId);

    @Query("DELETE FROM device WHERE objectId= :mObjectId")
    abstract void _deleteDevice(String mObjectId);

    @Query("DELETE FROM user")
    public void clearAllData() {
    }
}