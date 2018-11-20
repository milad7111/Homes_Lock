package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.bluetooth.BluetoothDevice;

/**
 * This is LockPageFragment Interface
 */

public interface ILockPageFragment {
    void onBondingRequired(BluetoothDevice device);

    void onBonded(BluetoothDevice device);
}
