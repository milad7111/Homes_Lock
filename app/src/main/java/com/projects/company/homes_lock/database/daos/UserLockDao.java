package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.projects.company.homes_lock.database.tables.UserLock;

import java.util.List;

@Dao
public interface UserLockDao {

    @Insert
    void insert(UserLock userLock);

    @Delete
    void delete(UserLock... userLocks);

    @Update
    void update(UserLock... userLocks);

    @Query("DELETE FROM userLock")
    void deleteAll();

    @Query("SELECT * FROM userLock")
    LiveData<List<UserLock>> getAllUserLocks();

    @Query("SELECT * FROM userLock WHERE objectId=:deviceObjectId")
    List<UserLock> getUserLocksByDevice(final int deviceObjectId);

    @Query("SELECT * FROM userLock WHERE objectId=:userObjectId")
    List<UserLock> getUserLocksByUser(final int userObjectId);
}