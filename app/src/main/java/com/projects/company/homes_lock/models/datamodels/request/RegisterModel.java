package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

public class RegisterModel extends BaseModel {

    @SerializedName("isLock")
    private String mIsLock;
    @SerializedName("mobileNumber")
    private String mMobileNumber;
    @SerializedName("name")
    private String mName;
    @SerializedName("email")
    private String mEmail;
}
