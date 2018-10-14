package com.projects.company.homes_lock.models.datamodels.ble;

import com.google.gson.annotations.SerializedName;

public class LockCommandModel extends BleBaseModel {

    @SerializedName("command")
    private String mCommand;

    public String getCommand() {
        return mCommand;
    }

    public void setCommand(String mCommand) {
        this.mCommand = mCommand;
    }
}
