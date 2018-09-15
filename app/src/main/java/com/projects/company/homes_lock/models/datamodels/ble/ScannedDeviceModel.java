package com.projects.company.homes_lock.models.datamodels.ble;

import android.bluetooth.BluetoothDevice;

import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScannedDeviceModel extends BaseModel {

    private final BluetoothDevice mDevice;;
    private String mName;
    private String mMacAddress;
//    private int mRSSI;

    public ScannedDeviceModel(final ScanResult scanResult) {
        this.mDevice = scanResult.getDevice();
        this.mName = this.mDevice.getName();
        this.mMacAddress = this.mDevice.getAddress();
//        this.mRSSI = scanResult.getRssi();
    }

//    public ScannedDeviceModel(String mName, String mMacAddress, int mRSSI) {
//        this.mName = mName;
//        this.mMacAddress = mMacAddress;
//    }

    public String getSpecialValue() {
        return mMacAddress;
    }

    public String getName() {
        return mName != null ? mName : "Unknown Device";
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }
}
