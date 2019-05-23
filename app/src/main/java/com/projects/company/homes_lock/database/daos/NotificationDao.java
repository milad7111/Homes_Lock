package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.Notification;

import java.util.List;

@Dao
public interface NotificationDao extends BaseDao<Device> {

    @Insert
    void insert(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notification")
    LiveData<List<Notification>> getAllNotifications();
}