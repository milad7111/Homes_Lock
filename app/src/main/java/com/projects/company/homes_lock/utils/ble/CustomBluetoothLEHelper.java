package com.projects.company.homes_lock.utils.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.ederdoski.simpleble.utils.Functions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

public class CustomBluetoothLEHelper {

    private Activity act;

    private ArrayList<BluetoothLE> aDevices = new ArrayList<>();

    private CustomBleCallback bleCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 1;
    private int mConnectionState = STATE_DISCONNECTED;

    private static long SCAN_PERIOD = 10000;
    private static boolean mScanning = false;
    private static String FILTER_SERVICE = "";

    public CustomBluetoothLEHelper(Activity _act, CustomBleCallback bleCallback) {
        if (Functions.isBleSupported(_act)) {
            act = _act;
            BluetoothManager bluetoothManager = (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.enable();

            this.bleCallback = bleCallback;
        }
    }

    public CustomBluetoothLEHelper(CustomBleCallback bleCallback) {
            BluetoothManager bluetoothManager = (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.enable();

            this.bleCallback = bleCallback;
    }

    public void scanLeDevice(boolean enable) {
        Handler mHandler = new Handler();

        if (enable) {
            mScanning = true;

            mHandler.postDelayed(() -> {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }, SCAN_PERIOD);

            if (!FILTER_SERVICE.equals("")) {
                UUID[] filter = new UUID[1];
                filter[0] = UUID.fromString(FILTER_SERVICE);
                mBluetoothAdapter.startLeScan(filter, mLeScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            act.runOnUiThread(() -> {
                if (aDevices.size() > 0) {

                    boolean isNewItem = true;

                    for (int i = 0; i < aDevices.size(); i++) {
                        if (aDevices.get(i).getMacAddress().equals(device.getAddress())) {
                            isNewItem = false;
                        }
                    }

                    if (isNewItem) {
                        aDevices.add(new BluetoothLE(device.getName(), device.getAddress(), rssi, device));
                    }

                } else {
                    aDevices.add(new BluetoothLE(device.getName(), device.getAddress(), rssi, device));
                }
            });
        }
    };

    public ArrayList<BluetoothLE> getListDevices() {
        return aDevices;
    }

//    public void connect(BluetoothDevice device, BleCallback _bleCallback) {
//        if (mBluetoothGatt == null && !isConnected()) {
//            bleCallback = _bleCallback;
//            mBluetoothGatt = device.connectGatt(act, false, mGattCallback);
//        }
//    }

    public void disconnect() {
        if (mBluetoothGatt != null && isConnected()) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

//    public boolean isReadyForScan() {
//        return Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH)
//                && Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH_ADMIN)
//                && Permissions.checkPermisionStatus(act, Manifest.permission.ACCESS_COARSE_LOCATION) && Functions.getStatusGps(act);
//    }
//
//    public void write(String service, String characteristic, byte[] aBytes) {
//
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic;
//
//        mBluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic));
//        mBluetoothGattCharacteristic.setValue(aBytes);
//
//        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
//    }

//    public void write(String service, String characteristic, String aData) {
//
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic;
//
//        mBluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic));
//        mBluetoothGattCharacteristic.setValue(aData);
//
//        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
//    }
//
//    public void read(String service, String characteristic) {
//        mBluetoothGatt.readCharacteristic(mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic)));
//    }

//    public void readRemoteRSSI() {
//        mBluetoothGatt.readRemoteRssi();
//    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Timber.i("Attempting to start service discovery: %s", mBluetoothGatt.discoverServices());
                mConnectionState = STATE_CONNECTED;
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
            }

            if (bleCallback != null)
                bleCallback.onBleConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (bleCallback != null) bleCallback.onBleServiceDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (bleCallback != null) bleCallback.onBleWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (bleCallback != null) bleCallback.onBleRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (bleCallback != null)
                bleCallback.onBleCharacteristicChange(gatt, characteristic);
        }
    };

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void setScanPeriod(int scanPeriod) {
        SCAN_PERIOD = scanPeriod;
    }

    public long getScanPeriod() {
        return SCAN_PERIOD;
    }

//    public void setFilterService(String filterService) {
//        FILTER_SERVICE = filterService;
//    }

//    public void setGatt(BluetoothGattCallback mGattCallback) {
//        this.mGattCallback = mGattCallback;
//    }

    public BluetoothGattCallback getGatt() {
        return mGattCallback;
    }

    public BluetoothDevice checkBondedDevices(String macAddress) {
        for (BluetoothDevice bondedDevice : mBluetoothAdapter.getBondedDevices())
            if (bondedDevice.getAddress().equals(macAddress))
                return bondedDevice;

        return null;
    }

    public void unPairDevice(String macAddress) {
        BluetoothDevice device = checkBondedDevices(macAddress);

        if (device != null)
            try {
                Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                m.invoke(device, (Object[]) null);
            } catch (Exception e) {
                Timber.e(e);
            }
    }
}
