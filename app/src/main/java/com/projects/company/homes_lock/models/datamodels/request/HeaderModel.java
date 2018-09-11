package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

public class HeaderModel extends BaseModel {

    @SerializedName("Content-Type")
    private String mContentType = "application/json";
    @SerializedName("user-token")
    private long mUserToken;
}
