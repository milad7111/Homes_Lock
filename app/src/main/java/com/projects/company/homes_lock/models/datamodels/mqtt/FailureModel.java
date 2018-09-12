package com.projects.company.homes_lock.models.datamodels.mqtt;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

public class FailureModel extends BaseModel {

    @SerializedName("message")
    private String mFailureMessage;

    public FailureModel(String mFailureMessage) {
        this.mFailureMessage = mFailureMessage;
    }

    public String getFailureMessage() {
        return mFailureMessage;
    }
}
