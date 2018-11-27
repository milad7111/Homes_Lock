package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

public class UserProserviceModel extends BaseModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("created")
    private long mCreatedAt;
    @SerializedName("ownerId")
    private String mOwnerId;
    @SerializedName("updated")
    private long mUpdated;
    @SerializedName("___class")
    private String mServerTableName;

    @SerializedName("count")
    private int mCount;
    @SerializedName("expirationDate")
    private long mExpirationDate;

}
