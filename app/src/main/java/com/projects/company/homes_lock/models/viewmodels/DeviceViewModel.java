package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.ILockPageFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceManager;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;

import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        IBleDeviceManagerCallbacks,
        IBleScanListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private final BleDeviceManager mBleDeviceManager;

    ILockPageFragment mILockPageFragment;

    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();
    //endregion Declare Objects

    public DeviceViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        this.mBleDeviceManager = new BleDeviceManager(getApplication());
        this.mBleDeviceManager.setGattCallbacks(this);
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

    @Override
    public void onResponse(Object response) {
        if (DataHelper.isInstanceOfList(response, Device.class.getName()))
            insertLocalDevices((List<Device>) response);
    }

    @Override
    public void onFailure(FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }

    @Override
    public void onFailure(Object response) {
        Log.i(this.getClass().getSimpleName(), ((FailureModel) response).getFailureMessage());
    }
    //endregion Device table

    //region BLE CallBacks
    @Override
    protected void onCleared() {
        super.onCleared();

        if (mBleDeviceManager.isConnected())
            disconnect();
    }

    @Override
    public void onDeviceConnecting(final BluetoothDevice device) {
        mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device) {
        mIsConnected.postValue(true);
        mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
    }

    @Override
    public void onDeviceDisconnecting(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        mIsConnected.postValue(false);
    }

    @Override
    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
        mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
    }

    @Override
    public void onDeviceReady(final BluetoothDevice device) {
        mIsSupported.postValue(true);
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(final BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(final BluetoothDevice device, final int value) {
    }

    @Override
    public void onBondingRequired(final BluetoothDevice device) {
        if (mILockPageFragment != null)
            mILockPageFragment.onBondingRequired(device);
    }

    @Override
    public void onBonded(final BluetoothDevice device) {
    }

    @Override
    public void onError(final BluetoothDevice device, final String message, final int errorCode) {
    }

    @Override
    public void onDeviceNotSupported(final BluetoothDevice device) {
        mIsSupported.postValue(false);
    }

    @Override
    public void onDataReceived(Object response) {
        UUID responseUUID;
        if (response instanceof BluetoothGattCharacteristic) {
            responseUUID = ((BluetoothGattCharacteristic) response).getUuid();
            if (responseUUID.equals(CHARACTERISTIC_UUID_TX)) {
                byte[] responseValue = ((BluetoothGattCharacteristic) response).getValue();

                switch (responseValue[0]) {
                    case 0x01:
                        mLocalRepository.updateDeviceLockStatus("fsafasfasfasf", responseValue[1] >> 4 == 1);
                        mLocalRepository.updateDeviceDoorStatus("fsafasfasfasf", responseValue[1] << 4);
                        mLocalRepository.updateDeviceBatteryStatus("fsafasfasfasf", responseValue[2]);
                        mLocalRepository.updateDeviceConnectionStatus("fsafasfasfasf", responseValue);
                        break;
                    case 0x02:
                        if (responseValue[1] == 0)
                            getDeviceInfoFromBleDevice();
                        else
                            getDeviceErrorFromBleDevice();
                        break;
                    case 0x03:
                        Log.d("Read deviceSerialNumber", responseValue[1] + "");
                        break;
                    case 0x04:
                        Log.d("Read deviceErrorData", DataHelper.subArrayByte(responseValue, 1, responseValue.length) + "");
                        break;
                    case 0x05:
                        mLocalRepository.updateDeviceTemperature("fsafasfasfasf", responseValue[1]);
                        mLocalRepository.updateDeviceHumidity("fsafasfasfasf", responseValue[2]);
                        mLocalRepository.updateDeviceCoLevel("fsafasfasfasf", responseValue[3]);
                        break;
                }
            }
        }
    }

    @Override
    public void onDataSent(Object response) {
    }

    @Override
    public void onFindBleCompleted(List response) {
    }

    @Override
    public void onFindBleFault(Object response) {
    }

    @Override
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
    }
    //endregion BLE CallBacks

    //region BLE Methods
    public void connect(ILockPageFragment mILockPageFragment, final ScannedDeviceModel device) {
        this.mILockPageFragment = mILockPageFragment;
        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBleDeviceManager.setLogger(logSession);
        mBleDeviceManager.connect(device.getDevice());
    }

    public void disconnect() {
        mBleDeviceManager.disconnect();
    }

    public void changeNotifyForCharacteristic(UUID characteristicUUID, boolean notifyStatus) {
        mBleDeviceManager.changeNotifyForCharacteristic(characteristicUUID, notifyStatus);
    }
    //endregion BLE Methods

    //region Declare Methods
    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public void sendLockCommand(boolean lockCommand) {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createCommand(new byte[]{0x02}, new byte[]{(byte) (lockCommand ? 0x01 : 0x02)}));
    }

    public LiveData<String> getConnectionState() {
        return mConnectionState;
    }

    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }

    private void getDeviceInfoFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createCommand(new byte[]{0x01}, new byte[]{}));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createCommand(new byte[]{0x05}, new byte[]{}));
    }

    private void getDeviceErrorFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createCommand(new byte[]{0x04}, new byte[]{}));
    }
    //endregion Declare Methods

    //region SharePreferences
    //endregion SharePreferences
}