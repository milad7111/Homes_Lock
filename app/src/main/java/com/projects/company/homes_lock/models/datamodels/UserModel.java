package com.projects.company.homes_lock.models.datamodels;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("objectId")
    private String mObjectId;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("mobileNumber")
    private String mMobileNumber;
}
