package com.projects.company.homes_lock.models.datamodels.ble;

import com.google.gson.annotations.SerializedName;

import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STATUS_IDLE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STATUS_LOCK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STATUS_UNLOCK;

public class WifiListModel extends BleBaseModel {

    @SerializedName("status")
    private String mLockStatus;

    public int getLockStatus() {
        switch (mLockStatus) {
            case LOCK_STATUS_UNLOCK:
                return 0;
            case LOCK_STATUS_LOCK:
                return 1;
            case LOCK_STATUS_IDLE:
                return 2;
            default:
                return 2;
        }
    }
}
