package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.projects.company.homes_lock.database.tables.DeviceError;

import java.util.List;

@Dao
public interface DeviceErrorDao {

    @Insert
    void insert(DeviceError deviceError);

    @Delete
    void delete(DeviceError... deviceErrors);

    @Update
    void update(DeviceError... deviceErrors);

    @Query("SELECT * FROM deviceError")
    LiveData<List<DeviceError>> getAllDeviceErrors();

    @Query("SELECT * FROM deviceError WHERE objectId=:deviceObjectId")
    List<DeviceError> getDeviceErrorsByDevice(final int deviceObjectId);

    @Query("DELETE FROM deviceError")
    void clearAllData();
}