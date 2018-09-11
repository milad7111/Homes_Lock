package com.projects.company.homes_lock.models.datamodels.response;

import com.google.gson.annotations.SerializedName;

public class FailureModel extends BaseModel {

    @SerializedName("message")
    private String mFailureMessage;
    @SerializedName("code")
    private String mFailureCode;

    public FailureModel(String mFailureMessage) {
        this.mFailureMessage = mFailureMessage;
    }
}
