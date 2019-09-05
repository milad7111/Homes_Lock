package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;

import no.nordicsemi.android.ble.BleManagerCallbacks;

public interface IBleDeviceManagerCallbacks<T> extends BleManagerCallbacks {

    void onDataReceived(T value);

    void onDataSent(T value);

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
}
