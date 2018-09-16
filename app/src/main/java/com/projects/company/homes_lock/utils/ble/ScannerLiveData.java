package com.projects.company.homes_lock.utils.ble;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * This class keeps the current list of discovered Bluetooth LE devices matching filter.
 * If a new device has been found it is added to the list and the LiveData i observers are
 * notified. If a packet from a device that's already in the list is found, the RSSI and name
 * are updated and observers are also notified. Observer may check {@link #getUpdatedDeviceIndex()}
 * to find out the index of the updated device.
 */
@SuppressWarnings("unused")
public class ScannerLiveData extends LiveData<ScannerLiveData> {
    private final List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();
    private Integer mUpdatedDeviceIndex;
    private boolean mScanningStarted;
    private boolean mBluetoothEnabled;
    private boolean mLocationEnabled;

    ScannerLiveData(final boolean bluetoothEnabled, final boolean locationEnabled) {
        mScanningStarted = false;
        mBluetoothEnabled = bluetoothEnabled;
        mLocationEnabled = locationEnabled;
        postValue(this);
    }

    void refresh() {
        postValue(this);
    }

    void scanningStarted() {
        mScanningStarted = true;
        postValue(this);
    }

    void scanningStopped() {
        mScanningStarted = false;
        postValue(this);
    }

    void bluetoothEnabled() {
        mBluetoothEnabled = true;
        postValue(this);
    }

    void bluetoothDisabled() {
        mBluetoothEnabled = false;
        mUpdatedDeviceIndex = null;
        mScannedDeviceModelList.clear();
        postValue(this);
    }

    void setLocationEnabled(final boolean enabled) {
        mLocationEnabled = enabled;
        postValue(this);
    }

    void deviceDiscovered(final ScanResult result) {
        ScannedDeviceModel device;

        final int index = indexOf(result);
        if (index == -1) {
            device = new ScannedDeviceModel(result);
            mScannedDeviceModelList.add(device);
//            mUpdatedDeviceIndex = null;
        } else {
            device = mScannedDeviceModelList.get(index);
//            mUpdatedDeviceIndex = index;
        }

        device.setRssi(result.getRssi());
        if (result.getScanRecord() != null)
            device.setName(result.getScanRecord().getDeviceName());

        postValue(this);
    }

    @NonNull
    public List<ScannedDeviceModel> getDevices() {
        return mScannedDeviceModelList;
    }

    @Nullable
    public Integer getUpdatedDeviceIndex() {
        final Integer i = mUpdatedDeviceIndex;
        mUpdatedDeviceIndex = null;
        return i;
    }

    public boolean isEmpty() {
        return mScannedDeviceModelList.isEmpty();
    }

    public boolean isScanning() {
        return mScanningStarted;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothEnabled;
    }

    public boolean isLocationEnabled() {
        return mLocationEnabled;
    }

    private int indexOf(final ScanResult result) {
        int i = 0;
        for (final ScannedDeviceModel device : mScannedDeviceModelList) {
            if (device.matches(result))
                return i;
            i++;
        }
        return -1;
    }
}
