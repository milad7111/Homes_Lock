package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

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
    private int mServerTableName;

    @SerializedName("count")
    private int mCount;
    @SerializedName("expirationDate")
    private long mExpirationDate;

}
