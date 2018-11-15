package com.projects.company.homes_lock.models.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.ederdoski.simpleble.interfaces.BleCallback;
import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SERVICE_UUID_SERIAL;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    private String mActiveDeviceObjectId;
    //endregion Declare Variables

    //region Declare Objects
    private CustomBluetoothLEHelper mCustomBluetoothLEHelper;
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private IBleScanListener mIBleScanListener;
    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsDeviceReady = new SingleLiveEvent<>();

    private BluetoothGattCharacteristic mTXCharacteristic;
    private BluetoothGattCharacteristic mRXCharacteristic;

    private BleCallback mBleCallback = new BleCallback() {
        @Override
        public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    mIsConnected.postValue(false);
                    mConnectionState.postValue("Disconnected");
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    mIsConnected.postValue(false);
                    mConnectionState.postValue("Connecting");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("BluetoothLEHelper", "Attempting to start service discovery.");
                    mIsConnected.postValue(true);
                    mConnectionState.postValue("Connected");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    mIsConnected.postValue(false);
                    mConnectionState.postValue("Disconnecting");
                    break;
            }
        }

        @Override
        public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID_SERIAL);
            if (mBluetoothGattService != null) {
                mTXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_TX);
                mRXCharacteristic = mBluetoothGattService.getCharacteristic(CHARACTERISTIC_UUID_RX);
            }

            if (mTXCharacteristic != null && mRXCharacteristic != null)
                mIsDeviceReady.postValue(true);
            else
                mIsDeviceReady.postValue(false);
        }

        @Override
        public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("GattCallBack", characteristic.getUuid().toString() + " get Notified : " + new String(characteristic.getValue()));
            onDataReceived(characteristic);
        }

        @Override
        public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            onDataSent(characteristic);
            Log.i("GattCallBack", characteristic.getUuid().toString() + " wrote Data : " + new String(characteristic.getValue()));
        }

        @Override
        public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("GattCallBack", characteristic.getUuid().toString() + " read Data : " + new String(characteristic.getValue()));
            onDataReceived(characteristic);
        }
    };
    //endregion Declare Objects

    public DeviceViewModel(Application application, IBleScanListener mIBleScanListener) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        this.mIBleScanListener = mIBleScanListener;

        mConnectionState.postValue("Disconnected"); // Connecting, Connected, Disconnecting, Disconnected
        mIsConnected.postValue(false);
        mIsDeviceReady.postValue(false);
        //endregion Initialize Objects
    }

    //region Device table
    public LiveData<List<Device>> getAllLocalDevices() {
        return mLocalRepository.getAllDevices();
    }

    public LiveData<Device> getDeviceInfo(String mActiveDeviceObjectId) {
        return mLocalRepository.getADevice(mActiveDeviceObjectId);
    }

    public void getAllServerDevices() {
        mNetworkRepository.getAllDevices(this);
    }

    public void insertLocalDevice(Device device) {
        mLocalRepository.insertDevice(device);
    }

    public void insertLocalDevices(List<Device> devices) {
        for (Device device : devices)
            mLocalRepository.insertDevice(device);
    }

    public void deleteLocalDevice(Device device) {
        mLocalRepository.deleteDevice(device);
    }

    private void updateDeviceLockStatus(String mDeviceObjectId, boolean mLockStatus) {

    }

    @Override
    public void onResponse(Object response) {
        if (DataHelper.isInstanceOfList(response, Device.class.getName()))
            insertLocalDevices((List<Device>) response);
    }

    @Override
    public void onFailure(com.projects.company.homes_lock.models.datamodels.response.FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }

    @Override
    public void onFailure(Object response) {
        FailureModel m = (FailureModel) response;
        Log.i(this.getClass().getSimpleName(), m.getFailureMessage());
    }
    //endregion Device table

    //region BLE CallBacks
    @Override
    protected void onCleared() {
        super.onCleared();
//        getApplication().unregisterReceiver(mBluetoothStateBroadcastReceiver);
//
//        if (BleHelper.isMarshmallowOrAbove())
//            getApplication().unregisterReceiver(mLocationProviderChangedReceiver);
//
//        if (mBleDeviceManager.isConnected())
//            disconnect();
    }

//    @Override
//    public void onDeviceConnecting(final BluetoothDevice device) {
//        mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
//    }

//    @Override
//    public void onDeviceConnected(final BluetoothDevice device) {
//        mIsConnected.postValue(true);
//        mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
//    }

//    @Override
//    public void onDeviceDisconnecting(final BluetoothDevice device) {
//        mIsConnected.postValue(false);
//    }
//
//    @Override
//    public void onDeviceDisconnected(final BluetoothDevice device) {
//        mIsConnected.postValue(false);
//    }
//
//    @Override
//    public void onLinklossOccur(final BluetoothDevice device) {
//        mIsConnected.postValue(false);
//    }
//
//    @Override
//    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
//        mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
//    }

//    @Override
//    public void onDeviceReady(final BluetoothDevice device) {
//        mIsDeviceReady.postValue(true);
//        mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services_completed, device.getName()));
//        mOnDeviceReady.postValue(null);
//    }
//
//    @Override
//    public boolean shouldEnableBatteryLevelNotifications(final BluetoothDevice device) {
//        // Blinky doesn't have Battery Service
//        return false;
//    }

//    @Override
//    public void onBatteryValueReceived(final BluetoothDevice device, final int value) {
//        // Blinky doesn't have Battery Service
//    }

//    @Override
//    public void onBondingRequired(final BluetoothDevice device) {
//    }

//    @Override
//    public void onBonded(final BluetoothDevice device) {
//        // Blinky does not require bonding
//    }

//    @Override
//    public void onError(final BluetoothDevice device, final String message, final int errorCode) {
//        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void onDeviceNotSupported(final BluetoothDevice device) {
//        mIsDeviceReady.postValue(false);
//    }

    public void onDataReceived(Object response) {
        UUID responseUUID;
        if (response instanceof BluetoothGattCharacteristic) {
            responseUUID = ((BluetoothGattCharacteristic) response).getUuid();
            if (responseUUID.equals(CHARACTERISTIC_UUID_TX)) {
                byte[] responseValue = ((BluetoothGattCharacteristic) response).getValue();

                new Thread() {
                    @Override
                    public void run() {
                        switch (responseValue[0]) {
                            case 0x01:
                                mLocalRepository.updateDeviceLockStatus("fsafasfasfasf", responseValue[1] >> 4 == 1);
                                mLocalRepository.updateDeviceDoorStatus("fsafasfasfasf", responseValue[1] << 4);
                                mLocalRepository.updateDeviceBatteryStatus("fsafasfasfasf", responseValue[2]);
                                mLocalRepository.updateDeviceConnectionStatus("fsafasfasfasf", responseValue);
                                break;
                            case 0x05:
                                mLocalRepository.updateDeviceTemperature("fsafasfasfasf", responseValue[1]);
                                mLocalRepository.updateDeviceHumidity("fsafasfasfasf", responseValue[2]);
                                mLocalRepository.updateDeviceCoLevel("fsafasfasfasf", responseValue[3]);
                                break;
                        }
                    }
                }.start();
            }
        }
    }

    private class updateDataTablesAsyncTask extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... responseValue) {
//            switch (responseValue[0]) {
//                case 0x01:
//                    mLocalRepository.updateDeviceLockStatus("fsafasfasfasf", true);
//                    mLocalRepository.updateDeviceLockStatus("fsafasfasfasf", responseValue[1] >> 4 == 1);
//                    mLocalRepository.updateDeviceDoorStatus("fsafasfasfasf", responseValue[1] << 4);
//                    mLocalRepository.updateDeviceBatteryStatus("fsafasfasfasf", responseValue[2]);
//                    mLocalRepository.updateDeviceConnectionStatus("fsafasfasfasf", responseValue);
//                    break;
//                case 0x05:
//                    mLocalRepository.updateDeviceTemperature("fsafasfasfasf", responseValue[1]);
//                    mLocalRepository.updateDeviceHumidity("fsafasfasfasf", responseValue[2]);
//                    mLocalRepository.updateDeviceCoLevel("fsafasfasfasf", responseValue[3]);
//                    break;
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    //endregion BLE CallBacks

    //region BLE Methods
    public void findBleDeviceWithMacAddressAndConnect(Activity activity, Context context, String bleDeviceMacAddress) {
        mCustomBluetoothLEHelper = new CustomBluetoothLEHelper(activity);

        if (BleHelper.isLocationRequired(context)) {
            if (BleHelper.isLocationPermissionsGranted(context)) {
                if (!BleHelper.isLocationEnabled(context))
                    BleHelper.enableLocation(activity);
                else {
                    if (BleHelper.isBleEnabled()) {
                        if (mCustomBluetoothLEHelper.isReadyForScan())
                            scanDevicesAndConnect(bleDeviceMacAddress);
                    } else BleHelper.enableBluetooth(activity);
                }
            } else {
                BleHelper.grantLocationPermission(activity);

                final boolean deniedForever = BleHelper.isLocationPermissionDeniedForever(activity);
                if (!deniedForever)
                    BleHelper.grantLocationPermission(activity);

                if (deniedForever)
                    BleHelper.handlePermissionSettings(activity);
            }
        } else {
            if (BleHelper.isBleEnabled()) {
                scanDevicesAndConnect(bleDeviceMacAddress);
            } else BleHelper.enableBluetooth(activity);
        }
    }

    private void scanDevicesAndConnect(String bleDeviceMacAddress) {
        if (!mCustomBluetoothLEHelper.isScanning()) {
            mCustomBluetoothLEHelper.setScanPeriod(1000);
            mCustomBluetoothLEHelper.scanLeDevice(true);

            new Handler().postDelayed(() -> {
                connectToSpecificBleDevice(getDeviceWithMacAddress(bleDeviceMacAddress));
            }, mCustomBluetoothLEHelper.getScanPeriod());
        }
    }

    private ScannedDeviceModel getDeviceWithMacAddress(String bleDeviceMacAddress) {
        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        if (mCustomBluetoothLEHelper.getListDevices().size() > 0)
            for (BluetoothLE device : mCustomBluetoothLEHelper.getListDevices())
                mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        mCustomBluetoothLEHelper.scanLeDevice(false);

        for (ScannedDeviceModel device : mScannedDeviceModelList)
            if (device.getMacAddress().equals(bleDeviceMacAddress))
                return device;

        return null;
    }

    private void connectToSpecificBleDevice(ScannedDeviceModel mScannedDevice) {
        try {
            if (mCustomBluetoothLEHelper != null)
                mCustomBluetoothLEHelper.connect(mScannedDevice.getDevice(), mBleCallback);
        } catch (NullPointerException e) {
            Log.e(getClass().getName(), "mCustomBluetoothLEHelper is null.");
        }
    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public void disconnect() {
        if (mCustomBluetoothLEHelper != null)
            mCustomBluetoothLEHelper.disconnect();
    }

    public void readCharacteristic(UUID characteristicUUID) {
        if (mCustomBluetoothLEHelper != null)
            mCustomBluetoothLEHelper.read(SERVICE_UUID_SERIAL, characteristicUUID);
    }

    public void writeCharacteristic(UUID characteristicUUID, byte[] value) {
        if (mCustomBluetoothLEHelper != null)
            mCustomBluetoothLEHelper.write(SERVICE_UUID_SERIAL, characteristicUUID, value);
    }

    public void changeNotifyForCharacteristic(UUID characteristicUUID, boolean notifyStatus) {
        if (mCustomBluetoothLEHelper != null) {
            mCustomBluetoothLEHelper.enableNotify(SERVICE_UUID_SERIAL, characteristicUUID, notifyStatus);
        }
    }

    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(UUID characteristicUUID) {
        if (characteristicUUID.equals(CHARACTERISTIC_UUID_TX) && mTXCharacteristic != null)
            return mTXCharacteristic;
        else if (characteristicUUID.equals(CHARACTERISTIC_UUID_RX) && mRXCharacteristic != null)
            return mRXCharacteristic;

        Log.e(this.getClass().getName(),
                "DeviceViewModel " + characteristicUUID + " Not Ready Yet.");

        return null;
    }

    public LiveData<Boolean> isDeviceReady() {
        return mIsDeviceReady;
    }
    //endregion BLE Methods

    //region SharePreferences
    //endregion SharePreferences
}