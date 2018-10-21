package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;

import no.nordicsemi.android.ble.BleManagerCallbacks;

public interface IBleDeviceManagerCallbacks<T> extends BleManagerCallbacks {

    void onDataReceived(T value);

    void onDataSent(T value);

    void setBluetoothGatt(BluetoothGatt bluetoothGatt);
}
