package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

public interface IBleScanListener<T> {
    void onFindBleCompleted(List<ScannedDeviceModel> response);

    void onFindBleFault(T response);

    void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel);

    void onDataReceived(T value);

    void onDataSent(T value);
}
