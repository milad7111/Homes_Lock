package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.projects.company.homes_lock.utils.helper.BleHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.Request;
import timber.log.Timber;

import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_PUBLIC_PRT;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SERVICE_UUID_SERIAL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getBleCommandPart;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getNibble;
import static com.projects.company.homes_lock.utils.helper.DataHelper.subArrayByte;

public class BleDeviceManager extends BleManager<IBleDeviceManagerCallbacks> {

    //region Declare Constants
    //endregion Declare Constants

    //region Arrays & Lists
    private List<byte[]> mBleCommandsPool = new ArrayList<>();
    //endregion Arrays & Lists

    //region Declare Variables
    private boolean bleBufferStatus = false;
    private byte[] lastNotifiedData = new byte[]{};
    //endregion Declare Variables

    //region Declare Objects
    private BluetoothGattCharacteristic mTXCharacteristic;
    private BluetoothGattCharacteristic mRXCharacteristic;

    private BluetoothGatt mBluetoothGatt = null;

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();

            requests.push(Request.newEnableNotificationsRequest(mTXCharacteristic));

            mBluetoothGatt = gatt;

            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID_SERIAL);
            if (mBluetoothGattService != null) {
                mTXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_TX);
                mRXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_RX);
            }

            bleBufferStatus = true;

            mBluetoothGatt = gatt;

            return mTXCharacteristic != null && mRXCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {
            mBluetoothGatt = null;
        }

        @Override
        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mBluetoothGatt = gatt;

            if (getNibble(characteristic.getValue()[1], true) == 4) {
                Timber.e("Buffer partition is free");
                bleBufferStatus = true;
                sendNextCommandFromBlePool();
            }

            switch (getNibble(characteristic.getValue()[1], false)) {
                case 0:
                    mCallbacks.onDataReceived(characteristic);
                    break;
                default:
                    handleReceivedResponse(characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mBluetoothGatt = gatt;

            mCallbacks.onDataSent(characteristic);
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mBluetoothGatt = gatt;

            if (!Arrays.equals(lastNotifiedData, characteristic.getValue())) {
                lastNotifiedData = characteristic.getValue();
                readCharacteristic(CHARACTERISTIC_UUID_TX);//Notify just get 20 bytes data, so read data to get all of it
            }

//            try {
//                String keyValue = new String(subArrayByte(characteristic.getValue(), 2, characteristic.getValue().length - 1));
//                new JSONObject(keyValue);//Just do this line to check if frame is valid
//
//                Log.e("pureReceivedResponse", new String(characteristic.getValue()));
//
//                if (getNibble(characteristic.getValue()[1], true) == 4) {
//                    Log.e(getClass().getName(), "Buffer partition is free");
//                    bleBufferStatus = true;
//                    sendNextCommandFromBlePool();
//                }
//
//                mCallbacks.onDataReceived(characteristic);
//            } catch (JSONException e) {
//                readCharacteristic(CHARACTERISTIC_UUID_TX);//Notify just get 20 bytes data, so read data to get all of it
//            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
//            mCallbacks.onReadRemoteRssi(gatt, rssi, status);
        }
    };
    //endregion Declare Objects

    //region Constructor
    public BleDeviceManager(final Context context) {
        super(context);
        BleHelper.myPhoneBleMacAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    }
    //endregion Constructor

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
    private void readCharacteristic(UUID characteristicUUID) {
        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);

        if (mBluetoothGattCharacteristic != null)
            readCharacteristic(mBluetoothGattCharacteristic);
    }

    private void writeCharacteristic(UUID characteristicUUID, byte[] value) {
        Timber.e("Partition command ---------------------------------> %s", new String(value));
        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);

        if (mBluetoothGattCharacteristic != null)
            writeCharacteristic(mBluetoothGattCharacteristic, value);
    }

    public void customWriteCharacteristic(UUID characteristicUUID, byte[] value) {
        Timber.e("Full command ************************************************ %s", new String(value));
        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);

        if (mBluetoothGattCharacteristic != null) {
            int parts = (value.length / 17) + ((value.length % 17 == 0) ? 0 : 1);

            if (parts == 1)
                addNewCommandToBlePool(getBleCommandPart(value, 0, 0));
            else
                for (int i = 0; i < parts; i++)
                    addNewCommandToBlePool(getBleCommandPart(value, i, parts - (i + 1)));
        }
    }

    private void addNewCommandToBlePool(byte[] command) {
        if (mBleCommandsPool == null)
            mBleCommandsPool = new ArrayList<>();

        mBleCommandsPool.add(command);
        sendNextCommandFromBlePool();
    }

    private void sendNextCommandFromBlePool() {
        if (mBleCommandsPool.size() > 0 && bleBufferStatus) {
            Timber.e("Buffer partition is full");
            bleBufferStatus = false;
            writeCharacteristic(CHARACTERISTIC_UUID_RX, mBleCommandsPool.get(0));
            mBleCommandsPool.remove(0);
        }
    }

    private void handleReceivedResponse(byte[] responseValue) {
        Timber.e(new String(responseValue));
        String keyValue = new String(subArrayByte(responseValue, 2, responseValue.length - 1));

        try {
            JSONObject keyCommandJson = new JSONObject(keyValue);
            String keyCommand = keyCommandJson.keys().next();

            switch (keyCommand) {
                case BLE_RESPONSE_PUBLIC_PRT:
                    Timber.e(new String(responseValue));
                    break;
                default:
                    Timber.e(keyCommand + " : " + keyCommandJson.getString(keyCommand));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void changeNotifyForCharacteristic(UUID characteristicUUID, boolean notifyStatus) {
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);
//
//        if (mBluetoothGattCharacteristic != null)
//            if (notifyStatus)
//                enableNotifications(mBluetoothGattCharacteristic);
//            else
//                disableNotifications(mBluetoothGattCharacteristic);
//    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(UUID characteristicUUID) {
        if (characteristicUUID.equals(CHARACTERISTIC_UUID_TX) && mTXCharacteristic != null)
            return mTXCharacteristic;
        else if (characteristicUUID.equals(CHARACTERISTIC_UUID_RX) && mRXCharacteristic != null)
            return mRXCharacteristic;

        Timber.e("BluetoothGattCharacteristic " + characteristicUUID + " Not Ready Yet.");

        return null;
    }

    public void readRemoteRssi() {
        if (mBluetoothGatt != null)
            mBluetoothGatt.readRemoteRssi();
    }
    //endregion Declare Methods
}
