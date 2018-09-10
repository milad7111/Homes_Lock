package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "deviceError",
        foreignKeys = {
                @ForeignKey(
                        entity = Error.class,
                        parentColumns = "objectId",
                        childColumns = "objectId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Device.class,
                        parentColumns = "objectId",
                        childColumns = "objectId",
                        onDelete = CASCADE)
        })
public class DeviceError {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "description")
    private String mDescription;

    @ColumnInfo(name = "createdAt")
    private String mCreatedAt;

    public DeviceError(@NonNull String mObjectId, String mDescription, String mCreatedAt) {
        this.mObjectId = mObjectId;
        this.mDescription = mDescription;
        this.mCreatedAt = mCreatedAt;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }
}