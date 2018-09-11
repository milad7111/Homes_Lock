package com.projects.company.homes_lock.models.datamodels.request;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

public class LoginModel extends BaseModel {

    @SerializedName("login")
    private String mLogin;
    @SerializedName("password")
    private long mPassword;
}
