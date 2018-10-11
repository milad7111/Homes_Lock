package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

public interface IBleScanListener<T> {
    void onFindBleCompleted(List<ScannedDeviceModel> response);

    void onFindBleFault(T response);

    void setReceiver(BroadcastReceiver mBroadcastReceiver);

    void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel);

    void setBluetoothGatt(BluetoothGatt mBluetoothGatt);

    void onDataReceived(T response);

    void onDataSent();
}
