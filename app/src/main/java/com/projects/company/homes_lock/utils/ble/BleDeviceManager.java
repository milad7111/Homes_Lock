package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.Request;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SERVICE_UUID_SERIAL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;

public class BleDeviceManager extends BleManager<IBleDeviceManagerCallbacks> {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Objects
    private BluetoothGattCharacteristic mTXCharacteristic;
    private BluetoothGattCharacteristic mRXCharacteristic;

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
//            requests.push(Request.newEnableNotificationsRequest(mLockStatusCharacteristic));
//            requests.push(Request.newReadRequest(mLockStatusCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID_SERIAL);
            if (mBluetoothGattService != null) {
                mTXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_TX);
                mRXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_RX);
            }

            return mTXCharacteristic != null && mRXCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {
        }

        @Override
        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
//            if (characteristic.getUuid().equals(CHARACTERISTIC_UUID_LOCK_STATUS))
                mCallbacks.onDataReceived(characteristic);
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mCallbacks.onDataSent(characteristic);
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
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
    public void readCharacteristic(UUID characteristicUUID) {
        readCharacteristic(getBluetoothGattCharacteristic(characteristicUUID));
    }

    public void writeCharacteristic(UUID characteristicUUID, String s) {
        writeCharacteristic(getBluetoothGattCharacteristic(characteristicUUID), s.getBytes());
    }

    public void changeNotifyForCharacteristic(UUID characteristicUUID, boolean notifyStatus) {
        if (notifyStatus)
            enableNotifications(getBluetoothGattCharacteristic(characteristicUUID));
        else
            disableNotifications(getBluetoothGattCharacteristic(characteristicUUID));
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(UUID characteristicUUID) {
        if (characteristicUUID.equals(CHARACTERISTIC_UUID_TX))
            return mTXCharacteristic;
        else if (characteristicUUID.equals(CHARACTERISTIC_UUID_RX))
            return mRXCharacteristic;

        return null;
    }
    //endregion Declare Methods
}
