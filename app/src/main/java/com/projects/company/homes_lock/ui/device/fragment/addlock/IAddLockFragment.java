package com.projects.company.homes_lock.ui.device.fragment.addlock;

import android.bluetooth.BluetoothDevice;

/**
 * This is AddLockFragment Interface
 */

public interface IAddLockFragment {
    void onBleDeviceClick();

    void onBondingRequired(BluetoothDevice device);

    void onBonded(BluetoothDevice device);
}
