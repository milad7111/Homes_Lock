package com.projects.company.homes_lock.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;

import java.util.List;

@Dao
public abstract class UserDao {

    public Long insert(User user) {
        Long id = _insertUser(user);

        if (user.getRelatedUserLocks() != null)
            for (UserLock userLock : user.getRelatedUserLocks()) {
                Device tempDevice = userLock.getRelatedDevice();
                tempDevice.setBleDeviceName(userLock.getDeviceName());

                insertDeviceForUser(tempDevice);

                userLock.setDeviceId(userLock.getRelatedDevice().getObjectId());
                userLock.setUserId(user.getObjectId());

                insertUserLockForUser(userLock);
            }

        return id;
    }

    private void insertDeviceForUser(Device device) {
        _insertDeviceForUser(device);
    }

    private void insertUserLockForUser(UserLock userLock) {
        _insertUserLockForUser(userLock);
    }

    public List<User> getUsersWithDevices() {
        List<User> users = _getAllUsers();

        for (User user : users) {
            List<UserLock> mUserLocks = _getUserLockByUserId(user.getObjectId());

            for (UserLock userLock : mUserLocks)
                userLock.setRelatedDevice(_getDeviceByObjectId(userLock.getDeviceId()));

            user.setRelatedUserLocks(mUserLocks);
        }

        return users;
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract Long _insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void _insertDeviceForUser(Device device);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void _insertUserLockForUser(UserLock userLock);

    @Query("SELECT * FROM user")
    abstract List<User> _getAllUsers();

    @Query("SELECT * FROM userLock WHERE userId = :mUserId")
    abstract List<UserLock> _getUserLockByUserId(String mUserId);

    @Query("SELECT * FROM device WHERE objectId = :mDeviceId")
    abstract Device _getDeviceByObjectId(String mDeviceId);

    @Query("DELETE FROM user")
    public void clearAllData() {
    }

    ;
}