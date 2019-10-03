package com.projects.company.homes_lock.models.datamodels.ble;

import com.projects.company.homes_lock.base.BaseModel;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

public class WifiStateModel extends BaseModel {

    //region Declare Objects
    private String state;
    private String ssid;
    private String password;
    private int authType;
    //endregion Declare Objects

    //region Constructor
    public WifiStateModel(String state) {
        this.state = state;
        this.ssid = null;
        this.password = null;
        this.authType = -1;
    }

    public WifiStateModel(String state, String ssid, String password, int authType) {
        this.state = state;
        this.ssid = ssid;
        this.password = password;
        this.authType = authType;
    }
    //endregion Constructor

    //region Declare Methods
    public String getState() {
        return state;
    }

    public String getSSID() {
        return ssid;
    }
    //endregion Declare Methods
}
