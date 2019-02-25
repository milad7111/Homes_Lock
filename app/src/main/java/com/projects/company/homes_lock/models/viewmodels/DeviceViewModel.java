package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.datamodels.request.AddRelationHelperModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.addlock.AddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.addlock.IAddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.ILockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.IManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.ISettingFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceManager;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
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
        IMQTTListener,
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
    private IManageMembersFragment mIManageMembersFragment;
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
    //endregion Constructor

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
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
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
            if (responseUUID.equals(CHARACTERISTIC_UUID_TX))
                handleReceivedResponse(((BluetoothGattCharacteristic) response).getValue());
        } else
            handleReceivedResponse((byte[]) response);
    }

    private void handleReceivedResponse(byte[] responseValue) {
        Log.d("ResponseAll: ", responseValue.toString());
        for (byte aResponseValue : responseValue)
            Log.e("Response: ", String.format("%d", aResponseValue & 0xff));

        String keyValue = new String(subArrayByte(responseValue, 2, responseValue.length - 2));

        try {
            JSONObject keyCommandJson = new JSONObject(keyValue);
            String keyCommand = keyCommandJson.keys().next();

            switch (keyCommand) {
                case "batt":
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceBatteryPercentage(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getInt(keyCommand));
                    break;
                case "islock":
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceIsLocked(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    break;
                case "isopen":
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceIsDoorClosed(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    break;
                case "lock":
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand) == null)
                            mILockPageFragment.onSendLockCommandSuccessful();
                        else
                            mILockPageFragment.onSendLockCommandFailed();
                    }
                    break;
                case "unlock":
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand) == null)
                            mILockPageFragment.onSendLockCommandSuccessful();
                        else
                            mILockPageFragment.onSendLockCommandFailed();
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        switch (responseValue[0]) {
//            case 0x01:
//                if (mILockPageFragment != null) {
//                    if (responseValue[4] == 1)
//                        DialogHelper.handleProgressDialog(null, null, null, false);
//
//                    mLocalRepository.updateDeviceIsLocked(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[1] == 1);
//                    mLocalRepository.updateDeviceIsDoorClosed(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[2] == 1);
//                    mLocalRepository.updateDeviceBatteryPercentage(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[3]);
//                    mLocalRepository.updateDeviceWifiStatus(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[4] == 1);
//                    mLocalRepository.updateDeviceConnectedWifiStrength(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[5]);
//                    mLocalRepository.updateDeviceInternetStatus(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[6] == 1);
//                    mLocalRepository.updateDeviceMQTTServerStatus(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[7] == 1);
//                    mLocalRepository.updateDeviceRestApiServerStatus(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
//                            responseValue[8] == 1);
//                    mLocalRepository.updateDeviceTemperature(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(), responseValue[9]);
//                    mLocalRepository.updateDeviceHumidity(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(), responseValue[10]);
//                    mLocalRepository.updateDeviceCoLevel(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(), responseValue[11]);
//                }
//                break;
//            case 0x02:
//                if (responseValue[1] == 0)
//                    getDeviceInfoFromBleDevice();
//                else
//                    getDeviceErrorFromBleDevice();
//                break;
//            case 0x03:
//                Log.d("Read deviceSerialNumber", responseValue[1] + "");
//                break;
//            case 0x04:
//                Log.d("Read deviceErrorData", Arrays.toString(subArrayByte(responseValue, 1, responseValue.length)));
//                break;
//            case 0x05:
//                break;
//            case 0x06:
//                Log.d("Scenario Wifi", "2: Get response on 0x06 command");
//                if (responseValue[1] == 1) {
//                    Log.d("Scenario Wifi", "3: Get Initial response for 0x06 command");
//                    if (responseValue[2] == 0) {
//                        Log.d("Scenario Wifi", "4: Get Initial OK for 0x06 command");
//                        if (mILockPageFragment != null)
//                            mILockPageFragment.onSendRequestGetAvailableWifiSuccessful();
//                    } else if (responseValue[2] == 1) {
//                        Log.d("Scenario Wifi", "5: Get Initial Error for 0x06 command");
//                        getDeviceErrorFromBleDevice();
//                    }
//                } else if (responseValue[1] == 2) {
//                    Log.d("Scenario Wifi", "6: Get Final response for 0x06 command");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onGetAvailableWifiNetworksCountAroundDevice((int) responseValue[2]);
//                }
//                break;
//            case 0x07:
//                Log.d("Scenario Wifi", String.format("8: Get %dth Wifi network", (int) responseValue[1]));
//                if (mILockPageFragment != null) {
//                    Log.d("Scenario Wifi",
//                            String.format("8: Send %dth Wifi network with SSID: %S to mILockPageFragment",
//                                    (int) responseValue[1],
//                                    new String(subArrayByte(responseValue, 3, responseValue.length - 2))));
//                    mILockPageFragment.onFindNewNetworkAroundDevice(
//                            new WifiNetworksModel(
//                                    (int) responseValue[1],
//                                    new String(subArrayByte(responseValue, 3, responseValue.length - 2)),
//                                    0,
//                                    responseValue[2]));
//                }
//                break;
//            case 0x08:
//                if (responseValue[1] == 0) {
//                    Log.d("OnNotify :", "wrote SSID successful.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkSSIDSuccessful();
//                } else {
//                    Log.d("OnNotify :", "SSID did not write.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkSSIDFailed();
//                }
//                break;
//            case 0x09:
//                if (responseValue[1] == 0) {
//                    Log.d("OnNotify :", "wrote Password successful.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkPasswordSuccessful();
//                } else {
//                    Log.d("OnNotify :", "Password did not write.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkPasswordFailed();
//                }
//                break;
//            case 0x0A:
//                if (responseValue[1] == 0) {
//                    Log.d("OnNotify :", "wrote Security successful.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkAuthenticationTypeSuccessful();
//                } else {
//                    Log.d("OnNotify :", "Security did not write.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkAuthenticationTypeFailed();
//                }
//                break;
//            case 0x0B:
//                if (responseValue[1] == 0) {
//                    Log.d("OnNotify :", "Internet Connection Command successful.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkSuccessful();
//                } else {
//                    Log.d("OnNotify :", "Internet Connection Command Failed.");
//                    if (mILockPageFragment != null)
//                        mILockPageFragment.onSetDeviceWifiNetworkFailed();
//                }
//                break;
//            case 0x0C:
//                if (mISettingFragment != null)
//                    mISettingFragment.onSetDeviceSetting(responseValue[1] == 0);
//                break;
//            case 0x0D:
//                if (mISettingFragment != null)
//                    mISettingFragment.onChangeOnlinePassword(responseValue[1] == 0);
//                break;
//            case 0x0E:
//                if (mISettingFragment != null)
//                    mISettingFragment.onChangePairingPassword(responseValue[1] == 0);
//                break;
//        }
    }

    @Override
    public void onDataSent(Object response) {
        Log.d("onDataSent", Arrays.toString(((BluetoothGattCharacteristic) response).getValue()));
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
                        mIAddLockFragment.onFindLockInOnlineDataBaseSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "addLockToUserLock":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "addUserLockToUser":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "removeDeviceForAllMembers":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveAllLockMembersSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "removeDeviceForOneMember":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveDeviceForOneMemberSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "enablePushNotification":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onDeviceRegistrationPushNotificationSuccessful(((ResponseBodyModel) response).getRegistrationId());
                    break;
                default:
                    break;
            }
        } else if (response instanceof UserLock) {
            if (mIAddLockFragment != null)
                mIAddLockFragment.onInsertUserLockSuccessful((UserLock) response);
            else if (mIManageMembersFragment != null)
                mIManageMembersFragment.onInsertUserLockSuccessful((UserLock) response);
        } else if (response instanceof User) {
            if (mIAddLockFragment != null)
                mIAddLockFragment.onGetUserSuccessful((User) response);
        } else if (response instanceof Device) {
            if (mILockPageFragment != null)
                mILockPageFragment.onGetUpdatedDevice((Device) response);
        }
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        if (response instanceof ResponseBodyFailureModel) {
            switch (getRequestType()) {
                case "validateLockInOnlineDatabase":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onFindLockInOnlineDataBaseFailed((ResponseBodyFailureModel) response);
                    break;
                case "addLockToUserLock":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddLockToUserLockFailed((ResponseBodyFailureModel) response);
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddLockToUserLockFailed((ResponseBodyFailureModel) response);
                    break;
                case "addUserLockToUser":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onAddUserLockToUserFailed((ResponseBodyFailureModel) response);
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddUserLockToUserFailed((ResponseBodyFailureModel) response);
                    break;
                case "removeDeviceForAllMembers":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveAllLockMembersFailed((ResponseBodyFailureModel) response);
                    break;
                case "removeDeviceForOneMember":
                    if (mISettingFragment != null)
                        mISettingFragment.onRemoveDeviceForOneMemberFailed((ResponseBodyFailureModel) response);
                    break;
                case "enablePushNotification":
                    if (mIAddLockFragment != null)
                        mIAddLockFragment.onDeviceRegistrationPushNotificationFailed((ResponseBodyFailureModel) response);
                    break;
                default:
                    break;
            }
        } else if (response instanceof FailureModel) {
            if (mIAddLockFragment != null) {
                switch (getRequestType()) {
                    case "############"://TODO i do not know this label is true
                        mIAddLockFragment.onGetUserFailed((FailureModel) response);
                        break;
                    case "insertOnlineUserLock":
                        mIAddLockFragment.onInsertUserLockFailed((FailureModel) response);
                        break;
                }
            } else if (mIManageMembersFragment != null)
                mIManageMembersFragment.onInsertUserLockFailed((FailureModel) response);
        }
    }

    @Override
    public void onListNetworkListenerFailure(FailureModel response) {
    }
    //endregion Network Callbacks

    //region MQTT CallBacks
    @Override
    public void onConnectionToBrokerLost(Object response) {
    }

    @Override
    public void onMessageArrived(Object response) {
        onDataReceived(((MessageModel) response).getMqttMessagePayload());
    }

    @Override
    public void onDeliveryMessageComplete(Object response) {

    }

    @Override
    public void onConnectionSuccessful(Object response) {
        MQTTHandler.subscribe(this);
    }

    @Override
    public void onConnectionFailure(Object response) {
    }

    @Override
    public void onSubscribeSuccessful(Object response) {
    }

    @Override
    public void onSubscribeFailure(Object response) {
    }

    @Override
    public void onPublishSuccessful(Object response) {
    }

    @Override
    public void onPublishFailure(Object response) {
    }
    //endregion MQTT CallBacks

    //region BLE Methods
    public void connect(IBleScanListener mIBleScanListener, final ScannedDeviceModel device) {
        this.mIBleScanListener = mIBleScanListener;
        if (mIBleScanListener instanceof ILockPageFragment)
            this.mILockPageFragment = (ILockPageFragment) mIBleScanListener;
        else if (mIBleScanListener instanceof IAddLockFragment)
            this.mIAddLockFragment = (IAddLockFragment) mIBleScanListener;

        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBleDeviceManager.setLogger(logSession);
        mBleDeviceManager.connect(device.getDevice());
    }

    public void disconnect() {
        mBleDeviceManager.disconnect();
    }

    public void sendLockCommand(LockPageFragment fragment, boolean lockCommand) {
        mILockPageFragment = fragment;
        if (BaseApplication.isUserLoggedIn())
            MQTTHandler.toggle(this, "", createCommand(new byte[]{0x02}, new byte[]{(byte) (lockCommand ? 0x01 : 0x02)}));
        else {
            JSONObject command = new JSONObject();
            try {
                command = command.put(lockCommand ? "lock" : "unlock", JSONObject.NULL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createWriteMessage(command.toString()));
        }
    }

    public void getDeviceInfoFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createReadMessage("batt"));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createReadMessage("isopen"));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, BleHelper.createReadMessage("isopen"));
    }

    public void getAvailableWifiNetworksCountAroundDevice(ILockPageFragment mILockPageFragment) {
        this.mILockPageFragment = mILockPageFragment;
        Log.d("Scenario Wifi", "1: Send request to get wifi network list");
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x06}, new byte[]{}));
    }

    public void getAvailableWifiNetworksAroundDevice(ILockPageFragment mILockPageFragment, int networkIndex) {
        this.mILockPageFragment = mILockPageFragment;
        Log.d("Scenario Wifi", String.format("7: Send Request to get %dth wifi network.", networkIndex));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x07}, new byte[]{(byte) networkIndex}));
    }

    public void setDeviceWifiNetwork(ILockPageFragment mILockPageFragment, WifiNetworksModel wifiNetwork) {
        this.mILockPageFragment = mILockPageFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x08}, wifiNetwork.getSSID().getBytes()));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x09}, wifiNetwork.getPassword().getBytes()));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0A}, new byte[]{(byte) wifiNetwork.getAuthenticateType()}));
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0B}, new byte[]{0x01}));
//        getDeviceInfoFromBleDevice();
    }

    private void getDeviceErrorFromBleDevice() {
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x04}, new byte[]{}));
    }

    public void disconnectDeviceWifiNetwork(ILockPageFragment mILockPageFragment) {
        this.mILockPageFragment = mILockPageFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0B}, new byte[]{0x00}));
//        getDeviceInfoFromBleDevice();
    }

    public void setDeviceSetting(Fragment parentFragment, byte doorInstallationOption, byte lockStagesOption) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0C},
                BleHelper.mergeArrays(new byte[]{doorInstallationOption}, new byte[]{lockStagesOption})));
    }

    public void changeOnlinePasswordViaBle(Fragment parentFragment, String oldPassword, String newPassword) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0D}, new byte[]{0x00}));
    }

    public void changePairingPasswordViaBle(Fragment parentFragment, String oldPassword, String newPassword) {
        mISettingFragment = (ISettingFragment) parentFragment;
        mBleDeviceManager.writeCharacteristic(CHARACTERISTIC_UUID_RX, createCommand(new byte[]{0x0E}, new byte[]{0x00}));
    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }
    //endregion BLE Methods

    //region Online Methods
    public void validateLockInOnlineDatabase(AddLockFragment fragment, String serialNumber) {
        setRequestType("validateLockInOnlineDatabase");
        mIAddLockFragment = fragment;
        mNetworkRepository.getDeviceObjectIdWithSerialNumber(this, serialNumber);
    }

    public void validateLockInOnlineDatabase(Fragment fragment, String serialNumber) {
        setRequestType("validateLockInOnlineDatabase");
        mIAddLockFragment = (IAddLockFragment) fragment;
        mNetworkRepository.getDeviceObjectIdWithSerialNumber(this, serialNumber);
    }

    public void insertOnlineUserLock(UserLockModel userLock) {
        setRequestType("insertOnlineUserLock");
        mNetworkRepository.insertUserLock(this, userLock);
    }

    public void insertOnlineUserLock(IManageMembersFragment mIManageMembersFragment, UserLockModel userLock) {
        this.mIManageMembersFragment = mIManageMembersFragment;
        mNetworkRepository.insertUserLock(this, userLock);
    }

    public void addLockToUserLock(String userLockObjectId, String lockObjectId) {
        setRequestType("addLockToUserLock");
        mNetworkRepository.addLockToUserLock(this, userLockObjectId, new AddRelationHelperModel(lockObjectId));
    }

    public void addUserLockToUser(String userObjectId, String userLockObjectId) {
        setRequestType("addUserLockToUser");
        mNetworkRepository.addUserLockToUser(this, userObjectId, new AddRelationHelperModel(userLockObjectId));
    }

    public void setListenerForDevice(Fragment fragment, Device mDevice) {
        mILockPageFragment = (ILockPageFragment) fragment;
        mNetworkRepository.setListenerForDevice(this, mDevice);
    }

    public void enablePushNotification(String lockObjectId) {
        setRequestType("enablePushNotification");
        mNetworkRepository.enablePushNotification(this, Collections.singletonList(lockObjectId));
    }

    public void removeDevice(Fragment parentFragment, boolean removeAllMembers, Device mDevice) {
        mISettingFragment = (ISettingFragment) parentFragment;
        if (mDevice.isLockSavedInServerByCheckUserLocks()) {
            if (removeAllMembers) {
                setRequestType("removeDeviceForAllMembers");
                mNetworkRepository.removeDeviceForAllMembers(this, mDevice.getObjectId());
            } else {
                setRequestType("removeDeviceForOneMember");
                mNetworkRepository.removeDeviceForOneMember(this, mDevice.getUserLockObjectId());
            }
        } else
            mLocalRepository.deleteDevice(mDevice);
    }
    //endregion Online Methods

    //region Declare Methods
    public void initMQTT(Context context, String deviceObjectId) {
        if (BaseApplication.isUserLoggedIn())
            MQTTHandler.setup(this, context, deviceObjectId);
    }

    public void disconnectMQTT() {
        MQTTHandler.disconnect(this);
    }

    public void updateDevice(Device device) {
        mLocalRepository.updateDevice(device);
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