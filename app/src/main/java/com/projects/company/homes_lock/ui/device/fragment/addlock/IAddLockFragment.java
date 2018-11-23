package com.projects.company.homes_lock.ui.device.fragment.addlock;

import android.bluetooth.BluetoothDevice;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.List;

/**
 * This is AddLockFragment Interface
 */
public interface IAddLockFragment {
    void onFindBleSuccess(List<ScannedDeviceModel> response);

    void onFindBleFault();

    void onBleDeviceClick(ScannedDeviceModel device);

    void onBondingRequired(BluetoothDevice device);

    void onBonded(BluetoothDevice device);
}
