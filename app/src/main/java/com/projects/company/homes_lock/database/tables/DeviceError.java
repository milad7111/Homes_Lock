package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

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
public class DeviceError extends BaseModel {

    //region Database attributes
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    @SerializedName("objectId")
    private String mObjectId;

    @ColumnInfo(name = "errorName")
    @SerializedName("errorName")
    private String mErrorName;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String mDescription;
    //endregion Database attributes

    //region Ignore server attributes
    @Ignore
    @SerializedName("created")
    private Long mCreatedAt;

    @Ignore
    @SerializedName("ownerId")
    private String mOwnerId;

    @Ignore
    @SerializedName("updated")
    private Long mUpdated;

    @Ignore
    @SerializedName("___class")
    private String mServerTableName;
    //endregion Ignore server attributes

    public DeviceError() {
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public Long getCreatedAt() {
        return mCreatedAt;
    }

    public void setErrorName(String mErrorName) {
        this.mErrorName = mErrorName;
    }

    public String getErrorName() {
        return mErrorName;
    }
}