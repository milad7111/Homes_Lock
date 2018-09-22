package com.projects.company.homes_lock.utils.ble;

import no.nordicsemi.android.ble.BleManagerCallbacks;

public interface IBleDeviceManagerCallbacks<T> extends BleManagerCallbacks {

    void onDataReceived(T response);

    void onDataSent();
}
