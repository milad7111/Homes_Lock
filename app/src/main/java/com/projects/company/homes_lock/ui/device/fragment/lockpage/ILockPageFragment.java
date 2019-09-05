package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.bluetooth.BluetoothGatt;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;

/**
 * This is GatewayPageFragment Interface
 */
public interface ILockPageFragment {
    void onGetUpdatedDevice(Device response);

    void onGetNewConnectedDevice(ConnectedDeviceModel connectedDeviceModel);

    void onDeviceDisconnectedSuccessfully();

    void onRemoveAccessToDeviceForUser();

    void onSendLockCommandSuccessful(String command);

    void onSendLockCommandFailed(String error);

    void onDoLockCommandSuccessful(String command);

    void onDoLockCommandFailed(String error);

    void onGetConnectedDevicesEnd();

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

    void onReadBCQDone();
}
