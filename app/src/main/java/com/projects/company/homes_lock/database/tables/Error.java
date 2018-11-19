package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "error")
public class Error {

    //region Database attributes
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "objectId")
    private String mObjectId;

    @ColumnInfo(name = "errorName")
    private String mErrorName;
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

    public Error() {
    }

    public void setObjectId(@NonNull String mObjectId) {
        this.mObjectId = mObjectId;
    }

    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    public void setErrorName(String mErrorName) {
        this.mErrorName = mErrorName;
    }

    public String getErrorName() {
        return mErrorName;
    }
}