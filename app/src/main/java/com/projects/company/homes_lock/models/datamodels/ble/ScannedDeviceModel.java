package com.projects.company.homes_lock.models.datamodels.ble;

import android.bluetooth.BluetoothDevice;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScannedDeviceModel extends BaseModel {

    private BluetoothDevice mDevice = null;
    private String mName;
    private String mMacAddress;
    private int mRSSI;

    public ScannedDeviceModel(final BluetoothLE bluetoothLE) {
        this.mDevice = bluetoothLE.getDevice();
        this.mName = this.mDevice.getName();
        this.mMacAddress = this.mDevice.getAddress();
        this.mRSSI = bluetoothLE.getRssi();
    }

    public ScannedDeviceModel() {
    }

    public boolean matches(final ScanResult scanResult) {
        return mDevice.getAddress().equals(scanResult.getDevice().getAddress());
    }

    public String getSpecialValue() {
        return mMacAddress;
    }

    public String getName() {
        return mName != null ? mName : "Unknown Device";
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getRSSI() {
        return mRSSI;
    }
}
