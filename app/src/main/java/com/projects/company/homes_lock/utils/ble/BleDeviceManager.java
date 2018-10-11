package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.Request;

import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_UUID_SERVICE_CHARACTERISTIC_LOCK_STATUS;

public class BleDeviceManager extends BleManager<IBleDeviceManagerCallbacks> {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Objects
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
//            requests.push(Request.newEnableNotificationsRequest(mButtonCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            mCallbacks.setBluetoothGatt(gatt);
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {
        }

        @Override
        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(LOCK_UUID_SERVICE_CHARACTERISTIC_LOCK_STATUS)) {
                mCallbacks.onDataReceived(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // This method is only called for LED characteristic
            mCallbacks.onDataSent();
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // This method is only called for Button characteristic
            mCallbacks.onDataReceived(characteristic);
        }
    };
    //endregion Declare Objects

    public BleDeviceManager(final Context context) {
        super(context);
    }

    //region BleManager CallBacks
    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    @Override
    protected boolean shouldAutoConnect() {
        // If you want to connect to the device using autoConnect flag = true, return true here.
        // Read the documentation of this method.
        return super.shouldAutoConnect();
    }
    //endregion BleManager CallBacks

    //region Declare Methods
    public void readCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUUID, UUID characteristicUUID) {
        if (bluetoothGatt != null) {
            BluetoothGattCharacteristic mBluetoothGattCharacteristic =
                    bluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            bluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    public void writeCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUUID, UUID characteristicUUID, String s) {
        if (bluetoothGatt != null) {
            BluetoothGattCharacteristic mBluetoothGattCharacteristic =
                    bluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            mBluetoothGattCharacteristic.setValue(s.getBytes());
            bluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    public void setNotifyForCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUUID, UUID characteristicUUID, boolean notifyStatus) {
        if (bluetoothGatt != null) {
            BluetoothGattCharacteristic mBluetoothGattCharacteristic =
                    bluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            bluetoothGatt.setCharacteristicNotification(mBluetoothGattCharacteristic, notifyStatus);
        }
    }
    //endregion Declare Methods
}
