package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

public class DeviceErrorModel extends BaseModel {

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

    @SerializedName("errorName")
    private String mErrorName;
    @SerializedName("description")
    private String mDescription;
}
