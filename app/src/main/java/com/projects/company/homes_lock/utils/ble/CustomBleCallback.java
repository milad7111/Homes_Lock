package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface CustomBleCallback {
    void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    void onBleServiceDiscovered(BluetoothGatt gatt, int status);

    void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
}
