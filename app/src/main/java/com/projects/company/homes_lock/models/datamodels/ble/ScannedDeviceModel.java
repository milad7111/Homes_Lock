package com.projects.company.homes_lock.models.datamodels.ble;

import android.bluetooth.BluetoothDevice;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.base.BaseModel;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScannedDeviceModel extends BaseModel {

    //region Declare Objects
    private BluetoothDevice bluetoothDevice = null;
    private String mName;
    private String mMacAddress;
    private String mSerialNumber;
    private int mRSSI;
    private byte[] mScanRecord;
    //endregion Declare Objects

    //region Constructor
    public ScannedDeviceModel(final BluetoothLE bluetoothLEDevice) {
        this.bluetoothDevice = bluetoothLEDevice.getDevice();
        this.mName = this.bluetoothDevice.getName();
        this.mMacAddress = this.bluetoothDevice.getAddress();
        this.mRSSI = bluetoothLEDevice.getRssi();
    }

    public ScannedDeviceModel(final BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.mName = bluetoothDevice.getName();
        this.mMacAddress = bluetoothDevice.getAddress();
    }

    public ScannedDeviceModel(final BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        this.bluetoothDevice = bluetoothDevice;
        this.mName = bluetoothDevice.getName();
        this.mMacAddress = bluetoothDevice.getAddress();
        this.mRSSI = rssi;
        this.mScanRecord = scanRecord;
    }

    public ScannedDeviceModel(int mRSSI) {
        this.mRSSI = mRSSI;
    }
    //endregion Constructor

    //region Declare Methods
    public boolean matches(final ScanResult scanResult) {
        return bluetoothDevice.getAddress().equals(scanResult.getDevice().getAddress());
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
        return bluetoothDevice;
    }

    public int getRSSI() {
        return mRSSI;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    public void setFavoriteStatus(boolean checked) {
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public void setRSSI(int rssi) {
        this.mRSSI = rssi;
    }
    //endregion Declare Methods
}
