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

import static com.projects.company.homes_lock.utils.helper.BleHelper.LED_UUID_SERVICE_CHARACTERISTIC_LED_BUTTON;

public class BleDeviceManager extends BleManager<IBleDeviceManagerCallbacks> {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Objects
    private BluetoothGatt mBluetoothGatt;
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
//            requests.push(Request.newReadRequest(mLedCharacteristic));
//            requests.push(Request.newReadRequest(mButtonCharacteristic));
//            requests.push(Request.newEnableNotificationsRequest(mButtonCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            mBluetoothGatt = gatt;
//            final BluetoothGattService service = gatt.getService(LBS_UUID_SERVICE);
//            if (service != null) {
//                mButtonCharacteristic = service.getCharacteristic(LBS_UUID_BUTTON_CHAR);
//                mLedCharacteristic = service.getCharacteristic(LBS_UUID_LED_CHAR);
//            }
//
//            boolean writeRequest = false;
//            if (mLedCharacteristic != null) {
//                final int rxProperties = mLedCharacteristic.getProperties();
//                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
//            }
//
//            return mButtonCharacteristic != null && mLedCharacteristic != null && writeRequest;
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {
        }

        @Override
        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(LED_UUID_SERVICE_CHARACTERISTIC_LED_BUTTON)) {
                mCallbacks.onDataReceived(characteristic);
            }
//            final int data = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            mCallbacks.onDataReceived(buttonPressed);
//            if (characteristic == mLedCharacteristic) {
//                final boolean ledOn = data == 0x01;
//                log(LogContract.Log.Level.APPLICATION, "LED " + (ledOn ? "ON" : "OFF"));
//                mCallbacks.onDataSent(ledOn);
//            } else {
//                final boolean buttonPressed = data == 0x01;
//                log(LogContract.Log.Level.APPLICATION, "Button " + (buttonPressed ? "pressed" : "released"));
//                mCallbacks.onDataReceived(buttonPressed);
//            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // This method is only called for LED characteristic
//            final int data = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            final boolean ledOn = data == 0x01;
//            log(LogContract.Log.Level.APPLICATION, "LED " + (ledOn ? "ON" : "OFF"));
            mCallbacks.onDataSent();
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
//            // This method is only called for Button characteristic
//            final int data = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//            final boolean buttonPressed = data == 0x01;
//            log(LogContract.Log.Level.APPLICATION, "Button " + (buttonPressed ? "pressed" : "released"));
//            mCallbacks.onDataReceived();
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
    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        if (mBluetoothGatt != null) {
            BluetoothGattCharacteristic mBluetoothGattCharacteristic =
                    mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            mBluetoothGatt.readCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, String s) {
        if (mBluetoothGatt != null) {
            BluetoothGattCharacteristic mBluetoothGattCharacteristic =
                    mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            mBluetoothGattCharacteristic.setValue(s.getBytes());
            mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
        }
    }
    //endregion Declare Methods
}
