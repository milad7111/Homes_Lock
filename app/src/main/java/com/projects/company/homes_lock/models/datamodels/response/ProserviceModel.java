package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

import java.util.List;

public class ProserviceModel extends BaseModel {

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

    @SerializedName("serviceName")
    private String mServiceName;
    @SerializedName("relatedUsers")
    private List<UserProserviceModel> mRelatedUsers;
}
