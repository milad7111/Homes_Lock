package com.projects.company.homes_lock.database.daos;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

public interface BaseDao<T> {
    @Insert
    void insert(T param);

    @Insert
    void insert(T... params);

    @Update
    void update(T param);

    @Delete
    void delete(T param);
}
