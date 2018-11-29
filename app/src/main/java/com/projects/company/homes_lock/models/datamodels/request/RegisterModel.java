package com.projects.company.homes_lock.models.datamodels.request;

import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

public class RegisterModel extends BaseModel {

    @SerializedName("email")
    private String mEmail;

    @SerializedName("name")
    private String mName;

    @SerializedName("mobileNumber")
    private String mMobileNumber;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("isLock")
    private boolean mIsLock;

    public RegisterModel(String mEmail, String mName, String mMobileNumber, String mPassword) {
        this.mEmail = mEmail;
        this.mName = mName;
        this.mMobileNumber = mMobileNumber;
        this.mPassword = mPassword;
        this.mIsLock = false;
    }

    public String getPassword() {
        return mPassword;
    }
}
