package com.projects.company.homes_lock.utils.ble;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

public interface IBleScanListener<T> {
    void onFindBleSuccess(List<ScannedDeviceModel> response);

    void onFindBleFault();
}
