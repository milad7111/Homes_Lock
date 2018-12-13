package com.projects.company.homes_lock.database.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.projects.company.homes_lock.database.tables.Error;

import java.util.List;

@Dao
public interface ErrorDao {

    @Insert
    void insert(Error error);

    @Delete
    void delete(Error... errors);

    @Update
    void update(Error... errors);

    @Query("SELECT * FROM error")
    LiveData<List<Error>> getAllErrors();

    @Query("DELETE FROM error")
    void clearAllData();
}