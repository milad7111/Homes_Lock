package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.base.BaseModel;

public class FailureModel extends BaseModel {

    @SerializedName("message")
    private String mFailureMessage;
    @SerializedName("code")
    private int mFailureCode;

    public FailureModel(String mFailureMessage) {
        this.mFailureMessage = mFailureMessage;
    }

    public FailureModel(String mFailureMessage, int mFailureCode) {
        this.mFailureMessage = mFailureMessage;
        this.mFailureCode = mFailureCode;
    }

    public String getFailureMessage() {
        return mFailureMessage;
    }

    public int getFailureCode() {
        return mFailureCode;
    }
}
