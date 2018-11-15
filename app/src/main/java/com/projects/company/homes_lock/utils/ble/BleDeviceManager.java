package com.projects.company.homes_lock.utils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.ederdoski.simpleble.interfaces.BleCallback;

import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SERVICE_UUID_SERIAL;

public class BleDeviceManager {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Objects
    private CustomBluetoothLEHelper mCustomBluetoothLEHelper;
    private BluetoothGattCharacteristic mTXCharacteristic;
    private BluetoothGattCharacteristic mRXCharacteristic;

    //    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
//
//        @Override
//        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
//            final LinkedList<Request> requests = new LinkedList<>();
//
//            requests.push(Request.newEnableNotificationsRequest(mTXCharacteristic));
//
//            requests.push(Request.newWriteRequest(mRXCharacteristic, BleHelper.createCommand(new byte[]{0x01}, new byte[]{})));
//            requests.push(Request.newReadRequest(mTXCharacteristic));
//
//            requests.push(Request.newWriteRequest(mRXCharacteristic, BleHelper.createCommand(new byte[]{0x05}, new byte[]{})));
//            requests.push(Request.newReadRequest(mTXCharacteristic));
//
//            return requests;
//        }
//
//        @Override
//        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
//            BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID_SERIAL);
//            if (mBluetoothGattService != null) {
//                mTXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_TX);
//                mRXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_RX);
//            }
//
//            return mTXCharacteristic != null && mRXCharacteristic != null;
//        }
//
//        @Override
//        protected void onDeviceDisconnected() {
//        }
//
//        @Override
//        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
////            if (characteristic.getUuid().equals(CHARACTERISTIC_UUID_LOCK_STATUS))
//            mCallbacks.onDataReceived(characteristic);
//        }
//
//        @Override
//        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
//            mCallbacks.onDataSent(characteristic);
//        }
//
//        @Override
//        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
//            mCallbacks.onDataReceived(characteristic);
//        }
//    };


    private final BleCallback mGattCallback = new BleCallback() {
        @Override
        public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onBleConnectionStateChange(gatt, status, newState);
        }
        //        @Override
//        public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            switch (status) {
//                case BluetoothGatt.STATE_DISCONNECTED:
//                    mIsConnected.postValue(false);
//                    mConnectionState.postValue("Disconnected");
//                    break;
//                case BluetoothGatt.STATE_CONNECTING:
//                    mConnectionState.postValue("Connecting");
//                    break;
//                case BluetoothGatt.STATE_CONNECTED:
//                    mIsConnected.postValue(true);
//                    mConnectionState.postValue("Connected");
//                    break;
//                case BluetoothGatt.STATE_DISCONNECTING:
//                    mIsConnected.postValue(false);
//                    mConnectionState.postValue("Disconnecting");
//                    break;
//                default:
//                    break;
//            }
//        }

        @Override
        public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID_SERIAL);
            if (mBluetoothGattService != null) {
                mTXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_TX);
                mRXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_RX);
            }
        }

        @Override
        public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onBleCharacteristicChange(gatt, characteristic);
        }

        @Override
        public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onBleWrite(gatt, characteristic, status);
        }

        @Override
        public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onBleRead(gatt, characteristic, status);
        }
    };
    //endregion Declare Objects

    public BleDeviceManager() {
//        super(context);
    }

    //region BleManager CallBacks
//    @NonNull
//    @Override
//    protected BleManagerGattCallback getGattCallback() {
//        return mGattCallback;
//    }

//    @Override
//    protected boolean shouldAutoConnect() {
//        // If you want to connect to the device using autoConnect flag = true, return true here.
//        // Read the documentation of this method.
//        return super.shouldAutoConnect();
//    }
    //endregion BleManager CallBacks

    //region Declare Methods
//    public void readCharacteristic(UUID characteristicUUID) {
//        mCustomBluetoothLEHelper.read();
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);
//
//        if (mBluetoothGattCharacteristic != null)
//            readCharacteristic(mBluetoothGattCharacteristic);
//    }

//    public void writeCharacteristic(UUID characteristicUUID, byte[] value) {
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);
//
//        if (mBluetoothGattCharacteristic != null)
//            writeCharacteristic(mBluetoothGattCharacteristic, value);
//    }

//    public void changeNotifyForCharacteristic(UUID characteristicUUID, boolean notifyStatus) {
//        BluetoothGattCharacteristic mBluetoothGattCharacteristic = getBluetoothGattCharacteristic(characteristicUUID);
//
//        if (mBluetoothGattCharacteristic != null)
//            if (notifyStatus)
//                enableNotifications(mBluetoothGattCharacteristic);
//            else
//                disableNotifications(mBluetoothGattCharacteristic);
//    }

//    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(UUID characteristicUUID) {
//        if (characteristicUUID.equals(CHARACTERISTIC_UUID_TX) && mTXCharacteristic != null)
//            return mTXCharacteristic;
//        else if (characteristicUUID.equals(CHARACTERISTIC_UUID_RX) && mRXCharacteristic != null)
//            return mRXCharacteristic;
//
//        Log.e(this.getClass().getName(),
//                "BluetoothGattCharacteristic " + characteristicUUID + " Not Ready Yet.");
//
//        return null;
//    }


//    public boolean isBluetoothLEHelperScanning() {
//        return mBluetoothLEHelper.isScanning();
//    }

//    public boolean isBluetoothLEHelperReadyForScan() {
//        return mBluetoothLEHelper.isReadyForScan();
//    }
    //endregion Declare Methods
}
