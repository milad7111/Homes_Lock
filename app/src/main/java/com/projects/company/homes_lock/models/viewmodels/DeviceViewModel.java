package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.datamodels.request.HelperModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.addlock.IAddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.ILockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.ISettingFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceManager;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import okhttp3.ResponseBody;

import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.createCommand;
import static com.projects.company.homes_lock.utils.helper.DataHelper.isInstanceOfList;
import static com.projects.company.homes_lock.utils.helper.DataHelper.subArrayByte;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        IBleDeviceManagerCallbacks {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    private String requestType = "";
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private IBleScanListener mIBleScanListener;
    private ILockPageFragment mILockPageFragment;
    private IAddLockFragment mIAddLockFragment;
    private ISettingFragment mISettingFragment;

    private final BleDeviceManager mBleDeviceManager;

    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();
    //endregion Declare Objects

    //region Constructor
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
    //endregion Declare Constructor

    //region Device Table
    public LiveData<List<Device>> getAllLocalDevices() {
        return mLocalRepository.getAllDevices();
    }

    public Device getUserLockInfo(String objectId) {
        return mLocalRepository.getUserLockInfo(objectId);
    }

    public List<Device> getAllUserLocks() {
        return mLocalRepository.getAllUserLocks();
    }

    public LiveData<Device> getDeviceInfo(String mActiveDeviceObjectId) {
        return mLocalRepository.getADevice(mActiveDeviceObjectId);
    }

    public void insertLocalDevice(Device device) {
        mLocalRepository.insertDevice(device);
    }

    private void insertLocalDevices(List<Device> devices) {
        for (Device device : devices)
            mLocalRepository.insertDevice(device);
    }

    public void deleteLocalDevice(Device device) {
        mLocalRepository.deleteDevice(device);
    }
    //endregion Device Table

    //region BLE CallBacks
    @Override
    public void onDeviceConnecting(final BluetoothDevice device) {
        mConnectionState.postValue(getApplication().getString(R.string.ble_state_connecting));
    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device) {
        mIsConnected.postValue(true);
        mConnectionState.postValue(getApplication().getString(R.string.ble_state_discovering_services));
    }

    @Override
    public void onDeviceDisconnecting(final BluetoothDevice device) {
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
//        DialogHelper.handleProgressDialog(false);
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
//        DialogHelper.handleProgressDialog(false);
    }

    @Override
    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
        mConnectionState.postValue(getApplication().getString(R.string.ble_state_initializing));
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
        mIsSupported.postValue(false);
    }

    @Override
    public void onBonded(final BluetoothDevice device) {
        if (mIBleScanListener != null)
            mIBleScanListener.onBonded(device);
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
                        mLocalRepository.updateDeviceLockStatus(LockPageFragment.getDevice().getObjectId(), responseValue[1] >> 4 == 1);
                        mLocalRepository.updateDeviceDoorStatus(LockPageFragment.getDevice().getObjectId(), responseValue[1] << 4);
                        mLocalRepository.updateDeviceBatteryStatus(LockPageFragment.getDevice().getObjectId(), responseValue[2]);
                        mLocalRepository.updateDeviceConnectionStatus(LockPageFragment.getDevice().getObjectId(), responseValue);
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
                        Log.d("Read deviceErrorData", Arrays.toString(subArrayByte(responseValue, 1, responseValue.length)));
                        break;
                    case 0x05:
                        mLocalRepository.updateDeviceTemperature(LockPageFragment.getDevice().getObjectId(), responseValue[1]);
                        mLocalRepository.updateDeviceHumidity(LockPageFragment.getDevice().getObjectId(), responseValue[2]);
                        mLocalRepository.updateDeviceCoLevel(LockPageFragment.getDevice().getObjectId(), responseValue[3]);
                        break;
                    case 0x06:
                        if (responseValue[1] == 0) {
                            getAvailableWifiNetworksAroundDevice(responseValue[2]);
                            if (mILockPageFragment != null)
                                mILockPageFragment.onGetAvailableWifiNetworksCountAroundDevice((int) responseValue[2]);
                        } else
                            getDeviceErrorFromBleDevice();
                        break;
                    case 0x07:
                        if (mILockPageFragment != null)
                            mILockPageFragment.onFindNewNetworkAroundDevice(
                                    new WifiNetworksModel(
                                            new String(subArrayByte(responseValue, 3, responseValue.length - 2)),
                                            0,
                                            responseValue[2]));
                        break;
                    case 0x08:
                        if (responseValue[1] == 0) {
                            Log.d("OnNotify :", "wrote SSID successful.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkSSIDSuccessful();
                        } else {
                            Log.d("OnNotify :", "SSID did not write.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkSSIDFailed();
                        }
                        break;
                    case 0x09:
                        if (responseValue[1] == 0) {
                            Log.d("OnNotify :", "wrote Password successful.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkPasswordSuccessful();
                        } else {
                            Log.d("OnNotify :", "Password did not write.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkPasswordFailed();
                        }
                        break;
                    case 0x0A:
                        if (responseValue[1] == 0) {
                            Log.d("OnNotify :", "wrote Security successful.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkAuthenticationTypeSuccessful();
                        } else {
                            Log.d("OnNotify :", "Security did not write.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkAuthenticationTypeFailed();
                        }
                        break;
                    case 0x0B:
                        if (responseValue[1] == 0) {
                            Log.d("OnNotify :", "Connection successful.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkSuccessful();
                        } else {
                            Log.d("OnNotify :", "Connection Failed.");
                            if (mILockPageFragment != null)
                                mILockPageFragment.onSetDeviceWifiNetworkFailed();
                        }
                        break;
                    case 0x0C:
                        if (mISettingFragment != null)
                            mISettingFragment.onSetDeviceSetting(responseValue[1] == 0);
                        break;
                    case 0x0D:
                        if (mISettingFragment != null)
                            mISettingFragment.onChangeOnlinePassword(responseValue[1] == 0);
                        break;
                    case 0x0E:
                        if (mISettingFragment != null)
                            mISettingFragment.onChangePairingPassword(responseValue[1] == 0);
                        break;
                }
            }
        }
    }

    @Override
    public void onDataSent(Object response) {
    }
    //endregion BLE CallBacks

    //region Network Callbacks
    @Override
    public void onResponse(Object response) {
        if (isInstanceOfList(response, Device.class.getName()))
            insertLocalDevices((List<Device>) response);
        else if (response instanceof ResponseBody) {
            switch (getRequestType()) {
                case "validateLockInOnlineDatabase":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onFindLockInOnlineDataBase(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "addLockToUserLock":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "addUserLockToUser":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "removeDeviceForAllMembers":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveAllLockMembers(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "removeDeviceForOneMember":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveDeviceForOneMember(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                default:
                    break;
            }
        } else if (response instanceof UserLock) {
            if (mIAddLockFragment != null)
                mIAddLockFragment.onInsertUserLockSuccessful((UserLock) response);
        } else if (response instanceof User) {
            if (mIAddLockFragment != null)
                mIAddLockFragment.onGetUserSuccessful((User) response);
        }
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        if (response instanceof FailureModel)
            Log.i(this.getClass().getSimpleName(), ((FailureModel) response).getFailureMessage());
        else if (response instanceof ResponseBodyFailureModel)
            Log.i(this.getClass().getSimpleName(), ((ResponseBodyFailureModel) response).getFailureMessage());
    }

    @Override
    public void onListNetworkListenerFailure(FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }
    //endregion Network Callbacks

    //region BLE Methods
    public void connect(IBleScanListener mIBleScanListener, final ScannedDeviceModel device) {
        this.mIBleScanListener = mIBleScanListener;
        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBleDeviceManager.setLogger(logSession);

        mBleDeviceManager.connect(device.getDevice());
    }

    public void disconnect() {
        mBleDeviceManager.disconnect();
    }

    public void sendLockCommand(boolean lockCommand) {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x02}, new byte[]{(byte) (lockCommand ? 0x01 : 0x02)}));
    }

    private void getDeviceInfoFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x01}, new byte[]{}));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x05}, new byte[]{}));
    }

    public void getAvailableWifiNetworksCountAroundDevice(Fragment fragment) {
        mILockPageFragment = (ILockPageFragment) fragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x06}, new byte[]{}));
    }

    private void getAvailableWifiNetworksAroundDevice(int networksCount) {
        for (int i = 0; i < networksCount; i++)
            mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x07}, new byte[]{(byte) i}));
    }

    public void setDeviceWifiNetwork(Fragment parentFragment, WifiNetworksModel wifiNetwork) {
        mILockPageFragment = (ILockPageFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x08}, wifiNetwork.getSSID().getBytes()));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x09}, wifiNetwork.getPassword().getBytes()));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0A}, new byte[]{(byte) wifiNetwork.getAuthenticateType()}));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0B}, new byte[]{0x01}));
    }

    private void getDeviceErrorFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x04}, new byte[]{}));
    }

    public void disconnectDeviceWifiNetwork() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0B}, new byte[]{0x00}));
    }

    public void setDeviceSetting(Fragment parentFragment, int selectedDoorInstallationOption, int selectedLockStagesOption) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0C}, new byte[]{0x00}));
    }

    public void changeOnlinePasswordViaBle(Fragment parentFragment, String oldPassword, String newPassword) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0D}, new byte[]{0x00}));
    }

    public void changePairingPasswordViaBle(Fragment parentFragment, String oldPassword, String newPassword) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0E}, new byte[]{0x00}));
    }

    public void removeDevice(Fragment parentFragment, boolean removeAllMembers, Device mDevice) {
        mISettingFragment = (ISettingFragment) parentFragment;
        if (mDevice.isLockSavedInServer()) {
            if (removeAllMembers) {
                setRequestType("removeDeviceForAllMembers");
                mNetworkRepository.removeDeviceForAllMembers(this, mDevice.getObjectId());
            } else {
                setRequestType("removeDeviceForOneMember");
                mNetworkRepository.removeDeviceForOneMember(this, mDevice.getObjectId(), mDevice.getUserLockObjectId());
            }
        } else
            mLocalRepository.deleteDevice(mDevice);
    }
    //endregion BLE Methods

    //region Online Methods
    public void validateLockInOnlineDatabase(Fragment fragment, String serialNumber) {
        setRequestType("validateLockInOnlineDatabase");
        mIAddLockFragment = (IAddLockFragment) fragment;
        mNetworkRepository.getDeviceObjectIdWithSerialNumber(this, serialNumber);
    }

    public void insertOnlineUserLock(UserLockModel userLock) {
        mNetworkRepository.insertUserLock(this, userLock);
    }

    public void addLockToUserLock(String userLockObjectId, String lockObjectId) {
        setRequestType("addLockToUserLock");
        mNetworkRepository.addLockToUserLock(this, userLockObjectId, new HelperModel(lockObjectId));
    }

    public void addUserLockToUser(String userLockObjectId) {
        setRequestType("addUserLockToUser");
        mNetworkRepository.addUserLockToUser(this, new HelperModel(userLockObjectId));
    }
    //endregion Online Methods

    //region Declare Methods
    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }

    private void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private String getRequestType() {
        return requestType;
    }
    //endregion Declare Methods

    //region SharePreferences
    //endregion SharePreferences
}