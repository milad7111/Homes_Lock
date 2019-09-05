package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.backendless.rt.data.EventHandler;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;
import com.projects.company.homes_lock.models.datamodels.request.AddRelationHelperModel;
import com.projects.company.homes_lock.models.datamodels.request.TempDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.UserDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyModel;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.adddevice.IAddDeviceFragment;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.IDeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.gatewaypage.GatewayPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.gatewaypage.IGatewayPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.ILockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.IManageMembersFragment;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceManager;
import com.projects.company.homes_lock.utils.ble.IBleDeviceManagerCallbacks;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.SingleLiveEvent;
import com.projects.company.homes_lock.utils.helper.BleCommand;
import com.projects.company.homes_lock.utils.mqtt.IMQTTListener;
import com.projects.company.homes_lock.utils.mqtt.MQTTHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import okhttp3.ResponseBody;
import timber.log.Timber;

import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BAT;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BCL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BCQ;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BLL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_BSL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_CFG;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_CON;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DEM;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DEO;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DEP;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DID;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_DIS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ERR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_FW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_HW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISI;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISO;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISQ;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ISW;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_KNK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_LOC;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_NPR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_NPS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_OPR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_OPS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PIL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PLK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PLT;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PRD;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_PSK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_RGH;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_RSS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_RST;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_SEC;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_SET;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_SN;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_SSD;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_TYP;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_ULC;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_COMMAND_WFL;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_CONFIG;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_INTER;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_KEY;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_LOCK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_NPR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_NPS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_OPR;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_OPS;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_PER;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_SET;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_UNLOCK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_PUBLIC_END;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_PUBLIC_OK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_PUBLIC_WAIT;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_RX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;
import static com.projects.company.homes_lock.utils.helper.BleHelper.createBleReadMessage;
import static com.projects.company.homes_lock.utils.helper.BleHelper.createJSONObjectWithKeyValue;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getBleCommandPart;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getBleTimeOutBaseOnBleCommandType;
import static com.projects.company.homes_lock.utils.helper.DataHelper.isInstanceOfList;
import static com.projects.company.homes_lock.utils.helper.DataHelper.subArrayByte;

public class DeviceViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        IMQTTListener,
        IBleDeviceManagerCallbacks,
        ILocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    private String requestType = "";

    private boolean bleBufferStatus = false;
    private boolean lastISR = false;
    private boolean lastISQ = false;

    private Integer oldPairingPassword = 0;
    private Integer newPairingPassword = 0;

    private String oldOnlinePassword = "";
    private String newOnlinePassword = "";
    //endregion Declare Variables

    //region Declare Arrays & Lists
    private List<BleCommand> mBleCommandsPool = new ArrayList<>();
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    private IBleScanListener mIBleScanListener;
    private ILockPageFragment mILockPageFragment;
    private IGatewayPageFragment mIGatewayPageFragment;
    private IAddDeviceFragment mIAddDeviceFragment;
    private IManageMembersFragment mIManageMembersFragment;
    private IDeviceSettingFragment mIDeviceSettingFragment;
    private ILoginFragment mILoginFragment;

    private final BleDeviceManager mBleDeviceManager;
    private final MutableLiveData<String> mConnectionState = new MutableLiveData<>(); // Connecting, Connected, Disconnecting, Disconnected
    private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();
    private final MutableLiveData<Integer> mBleTimeOut = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();

    private AvailableBleDeviceModel mSelectedAvailableBleServer = new AvailableBleDeviceModel(0);

    private Device lastDeletedDevice = new Device();
    private EventHandler deviceEventHandler = null;
    //endregion Declare Objects

    //region Constructor
    public DeviceViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        this.mBleDeviceManager = new BleDeviceManager(getApplication());
        this.mBleDeviceManager.setGattCallbacks(this);
        //endregion Initialize Objects

        setRequestType("");
    }
    //endregion Constructor

    //region Device Table
    public LiveData<List<Device>> getAllLocalDevices() {
        return mLocalRepository.getAllDevices();
    }

    public LiveData<UserLock> getUserLockInfo(String deviceObjectId) {
        return mLocalRepository.getUserLockInfo(deviceObjectId);
    }

    public LiveData<Device> getDeviceInfo(String mActiveDeviceObjectId) {
        return mLocalRepository.getADevice(mActiveDeviceObjectId);
    }

    public void insertLocalDevice(IBleScanListener mIBleScanListener, Device device) {
        this.mIAddDeviceFragment = (IAddDeviceFragment) mIBleScanListener;
        mLocalRepository.insertDevice(this, device);
    }

    private void insertLocalDevices(List<Device> devices) {
        for (Device device : devices)
            mLocalRepository.insertDevice(this, device);
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
        mBleCommandsPool.clear();
        bleBufferStatus = true;
        mIsConnected.postValue(true);
        mConnectionState.postValue(getApplication().getString(R.string.ble_state_discovering_services));

        //TODO remove these lines after test ble without password
        if (mIBleScanListener != null)
            mIBleScanListener.onBonded(device);
    }

    @Override
    public void onDeviceDisconnecting(final BluetoothDevice device) {
        mBleCommandsPool.clear();
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
        mBleTimeOut.postValue(-1);
        mBleCommandsPool.clear();
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        mBleCommandsPool.clear();
        mIsConnected.postValue(false);
        mIsSupported.postValue(false);
        mBleTimeOut.postValue(-1);
        mBleCommandsPool.clear();
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        mBleCommandsPool.clear();
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
        //TODO uncomment these lines after test ble without password
//        if (mIBleScanListener != null)
//            mIBleScanListener.onBonded(device);
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

    @Override
    public void onDataSent(Object response) {
        Timber.d(Arrays.toString(((BluetoothGattCharacteristic) response).getValue()));
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if (mILockPageFragment != null)
            mILockPageFragment.onReadRemoteRssi(gatt, rssi, status);
        else if (mIGatewayPageFragment != null)
            mIGatewayPageFragment.onReadRemoteRssi(gatt, rssi, status);
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
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onFindLockInOnlineDataBaseSuccessful(
//                                ((ResponseBody) response).source().toString()
//                                        .replace("[text=", "")
//                                        .replace("]", "")
//                                        .replace("\"", ""));
//                    else
                    if (mILoginFragment != null)
                        mILoginFragment.onFindLockInOnlineDataBaseSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "addLockToUserLock":
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
//                    else
                    if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    else if (mILoginFragment != null)
                        mILoginFragment.onAddLockToUserLockSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "addUserLockToUser":
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
//                    else
                    if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    else if (mILoginFragment != null)
                        mILoginFragment.onAddUserLockToUserSuccessful(((ResponseBody) response).source().toString().equals("[text=1]"));
                    break;
                case "removeDeviceForAllMembers":
                    mLocalRepository.deleteDevice(this.lastDeletedDevice);
                    this.lastDeletedDevice = new Device();
                    if (mIDeviceSettingFragment != null)
                        mIDeviceSettingFragment.onRemoveAllDeviceMembersSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "removeDeviceForOneMember":
                    mLocalRepository.deleteDevice(this.lastDeletedDevice);
                    this.lastDeletedDevice = new Device();
                    if (mIDeviceSettingFragment != null)
                        mIDeviceSettingFragment.onRemoveDeviceForOneMemberSuccessful(
                                ((ResponseBody) response).source().toString()
                                        .replace("[text=", "")
                                        .replace("]", "")
                                        .replace("\"", ""));
                    break;
                case "enablePushNotification":
                    if (mILoginFragment != null)
                        mILoginFragment.onDeviceRegistrationForPushNotificationSuccessful(((ResponseBodyModel) response).getRegistrationId());
                    break;
                default:
                    break;
            }
        } else if (response instanceof UserLock) {
            if (getRequestType().equals("insertOnlineUserDevice")) {
//                if (mIAddDeviceFragment != null)
//                    mIAddDeviceFragment.onInsertUserLockSuccessful((UserLock) response);
//                else
                if (mILoginFragment != null)
                    mILoginFragment.onInsertUserLockSuccessful((UserLock) response);
                else if (mIManageMembersFragment != null)
                    mIManageMembersFragment.onInsertUserLockSuccessful((UserLock) response);
            } else if (!((UserLock) response).getAdminStatus()) {
                // this is when get access from user by admin in server
                if (mILockPageFragment != null)
                    mILockPageFragment.onRemoveAccessToDeviceForUser();
            }
        }
//        else if (response instanceof User) {
//            if (mIAddDeviceFragment != null)
//                mIAddDeviceFragment.onGetUserSuccessful((User) response);
//        }
        else if (response instanceof Device) {
            ((Device) response).setConnectedClientsCount(Integer.valueOf(((Device) response).getConnectedDevices().split(",")[1]));
            ((Device) response).setConnectedServersCount(Integer.valueOf(((Device) response).getConnectedDevices().split(",")[2]));

            if (mILockPageFragment != null)
                mILockPageFragment.onGetUpdatedDevice((Device) response);
            else if (mIGatewayPageFragment != null)
                mIGatewayPageFragment.onGetUpdatedDevice((Device) response);
        } else if (response instanceof TempDeviceModel) {
            if (((TempDeviceModel) response).isUpdateRequired())
                mNetworkRepository.getDeviceWithObjectId(this, ((TempDeviceModel) response).getObjectId());
        }
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        if (response instanceof ResponseBodyFailureModel) {
            switch (getRequestType()) {
                case "validateLockInOnlineDatabase":
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onFindLockInOnlineDataBaseFailed((ResponseBodyFailureModel) response);
//                    else
                    if (mILoginFragment != null)
                        mILoginFragment.onFindLockInOnlineDataBaseFailed((ResponseBodyFailureModel) response);
                    break;
                case "addLockToUserLock":
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onAddLockToUserLockFailed((ResponseBodyFailureModel) response);
                    if (mILoginFragment != null)
                        mILoginFragment.onAddLockToUserLockFailed((ResponseBodyFailureModel) response);
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddLockToUserLockFailed((ResponseBodyFailureModel) response);
                    break;
                case "addUserLockToUser":
//                    if (mIAddDeviceFragment != null)
//                        mIAddDeviceFragment.onAddUserLockToUserFailed((ResponseBodyFailureModel) response);
                    if (mILoginFragment != null)
                        mILoginFragment.onAddUserLockToUserFailed((ResponseBodyFailureModel) response);
                    else if (mIManageMembersFragment != null)
                        mIManageMembersFragment.onAddUserLockToUserFailed((ResponseBodyFailureModel) response);
                    break;
                case "removeDeviceForAllMembers":
                    this.lastDeletedDevice = new Device();
                    if (mIDeviceSettingFragment != null)
                        mIDeviceSettingFragment.onRemoveAllLockMembersFailed((ResponseBodyFailureModel) response);
                    break;
                case "removeDeviceForOneMember":
                    this.lastDeletedDevice = new Device();
                    if (mIDeviceSettingFragment != null)
                        mIDeviceSettingFragment.onRemoveDeviceForOneMemberFailed((ResponseBodyFailureModel) response);
                    break;
                case "enablePushNotification":
                    if (mILoginFragment != null)
                        mILoginFragment.onDeviceRegistrationForPushNotificationFailed((ResponseBodyFailureModel) response);
                    break;
                default:
                    break;
            }
        } else if (response instanceof FailureModel) {
//            if (mIAddDeviceFragment != null) {
//                switch (getRequestType()) {
//                    case "############"://TODO i do not know this label is true
//                        mIAddDeviceFragment.onGetUserFailed((FailureModel) response);
//                        break;
//                    case "insertOnlineUserDevice":
//                        mIAddDeviceFragment.onInsertUserLockFailed((FailureModel) response);
//                        break;
//                }
//            }
            if (mIManageMembersFragment != null)
                mIManageMembersFragment.onInsertUserLockFailed((FailureModel) response);
            else if (mILoginFragment != null) {
                if (getRequestType().equals("insertOnlineUserDevice"))
                    mILoginFragment.onInsertUserLockFailed((FailureModel) response);
            }
        }
    }

    @Override
    public void onListNetworkListenerFailure(FailureModel response) {
    }
    //endregion Network Callbacks

    //region Local Repository Callbacks
    @Override
    public void onDataInsert(Object object) {
        Timber.d("Data inserted");

        if (object instanceof Device) {
            if (mIAddDeviceFragment != null)
                mIAddDeviceFragment.onDataInsert(object);
        }
    }

    @Override
    public void onClearAllData() {
    }
    //endregion Local Repository Callbacks

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
        else if (mIBleScanListener instanceof IAddDeviceFragment)
            this.mIAddDeviceFragment = (IAddDeviceFragment) mIBleScanListener;

        final LogSession logSession = Logger.newSession(getApplication(), null, device.getMacAddress(), device.getName());
        mBleDeviceManager.setLogger(logSession);

        if (mBleDeviceManager.isConnected())
            this.mIsConnected.postValue(true);
        else
            mBleDeviceManager.connect(device.getDevice());
    }

    public void disconnect() {
        mBleDeviceManager.disconnect();
    }

    private void handleReceivedResponse(byte[] responseValue) {
        Timber.e(new String(responseValue));

        if (responseValue[1] == 0x40) {
            Timber.e("Buffer is free");
            bleBufferStatus = true;
        }

        String keyValue = new String(subArrayByte(responseValue, 2, responseValue.length - 1));

        //region switch response
        try {
            JSONObject keyCommandJson = new JSONObject(keyValue);
            String keyCommand = keyCommandJson.keys().next();

            switch (keyCommand) {
                case BLE_COMMAND_BAT:
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceBatteryPercentage(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getInt(keyCommand));
                    break;
                case BLE_COMMAND_ISK:
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceIsLocked(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    break;
                case BLE_COMMAND_ISO:
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceIsDoorClosed(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    break;
                case BLE_COMMAND_KNK:
                    Timber.e("Door knocked : %s", keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_LOC:
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            mILockPageFragment.onSendLockCommandSuccessful("lock");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_LOC + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            mILockPageFragment.onDoLockCommandSuccessful("lock");
                        }
                    }
                    break;
                case BLE_COMMAND_ULC:
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            mILockPageFragment.onSendLockCommandSuccessful("unlock");

                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_ULC + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            mILockPageFragment.onDoLockCommandSuccessful("unlock");
                        }
                    }
                    break;
                case BLE_COMMAND_TYP:
                    Timber.e("type setting IS: %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDeviceType(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateDeviceType(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_FW:
                    Timber.e("fw_ver setting IS: %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateFirmwareVersion(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateFirmwareVersion(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_HW:
                    Timber.e("hw_ver setting IS: %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateHardwareVersion(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateHardwareVersion(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_PRD:
                    Timber.e("prd setting IS: %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateProductionDate(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateProductionDate(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_SN:
                    Timber.e("sn setting IS: %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateSerialNumber(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateSerialNumber(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_DID:
                    Timber.e("did setting %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null)
                        mLocalRepository.updateDynamicId(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    else if (mIGatewayPageFragment != null)
                        mLocalRepository.updateDynamicId(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                    break;
                case BLE_COMMAND_BCQ:
                    Timber.e("bcq setting %s", keyCommandJson.getString(keyCommand));
                    if (mILockPageFragment != null) {
                        mLocalRepository.updateConnectedClientsCount(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                Integer.valueOf(keyCommandJson.getString(keyCommand).split(",")[1]));
                    } else if (mIGatewayPageFragment != null) {
                        mLocalRepository.updateConnectedDevicesCount(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getString(keyCommand));
                        mLocalRepository.updateConnectedClientsCount(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                Integer.valueOf(keyCommandJson.getString(keyCommand).split(",")[1]));
                        mLocalRepository.updateConnectedServersCount(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                Integer.valueOf(keyCommandJson.getString(keyCommand).split(",")[2]));
                    }
                    break;
                case BLE_COMMAND_OPS:
                    Timber.e("old pairing pass %s", keyCommandJson.getString(keyCommand));
                    if (mIDeviceSettingFragment != null && keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                        changePairingPasswordViaBleFinalStep();
                        mIDeviceSettingFragment.onCheckOldPairingPasswordSuccessful();
                    }
                    break;
                case BLE_COMMAND_NPS:
                    Timber.e("new pairing pass %s", keyCommandJson.getString(keyCommand));
                    if (mIDeviceSettingFragment != null && keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK))
                        mIDeviceSettingFragment.onChangePairingPasswordSuccessful();
                    break;
                case BLE_COMMAND_OPR:
                    Timber.e("old rest pass %s", keyCommandJson.getString(keyCommand));
                    if (mIDeviceSettingFragment != null && keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                        changeOnlinePasswordViaBleFinalStep();
                        mIDeviceSettingFragment.onCheckOldOnlinePasswordSuccessful();
                    }
                    break;
                case BLE_COMMAND_NPR:
                    Timber.e("new rest pass %s", keyCommandJson.getString(keyCommand));
                    if (mIDeviceSettingFragment != null)
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            mIDeviceSettingFragment.onChangeOnlinePasswordWait();
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_NPR + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            mIDeviceSettingFragment.onChangeOnlinePasswordSuccessful();
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_NPR));
                        }
                    break;
                case BLE_COMMAND_RGH:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK))
                            mIDeviceSettingFragment.onSetDoorInstallationSuccessful();
                        else //means if it has boolean value
                            mLocalRepository.updateDoorInstallation(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                    keyCommandJson.getBoolean(keyCommand));

                    }
                    break;
                case BLE_COMMAND_ISW:
                    if (mIGatewayPageFragment != null) {
                        mIGatewayPageFragment.onWifiStatusChange(keyCommandJson.getBoolean(keyCommand));
                        mLocalRepository.updateDeviceWifiStatus(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    }
                    break;
                case BLE_COMMAND_RSS:
                    if (mIGatewayPageFragment != null) {
                        mLocalRepository.updateConnectedWifiStrength(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getInt(keyCommand));
                    }
                    break;
                case BLE_COMMAND_ISI:
                    if (mIGatewayPageFragment != null) {
                        mIGatewayPageFragment.onInternetStatusChange(keyCommandJson.getBoolean(keyCommand));
                        mLocalRepository.updateDeviceInternetStatus(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    }
                    break;
                case BLE_COMMAND_ISQ:
                    if (mIGatewayPageFragment != null) {
                        mLocalRepository.updateDeviceMQTTServerStatus(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));

                        lastISQ = keyCommandJson.getBoolean(keyCommand);
                        this.mIGatewayPageFragment.onConnectToMqttServerSuccessful(lastISR, lastISQ);
                    }
                    break;
                case BLE_COMMAND_ISR:
                    if (mIGatewayPageFragment != null) {
                        mLocalRepository.updateDeviceRestApiServerStatus(((GatewayPageFragment) mIGatewayPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));

                        lastISR = keyCommandJson.getBoolean(keyCommand);
                        this.mIGatewayPageFragment.onConnectToServerSuccessful(lastISR, lastISQ);
                    }
                    break;
                case BLE_COMMAND_RST:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK))
                            mIDeviceSettingFragment.onResetBleDeviceSuccessful();
                    }
                    break;
                case BLE_COMMAND_BCL:
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Start getting connected devices ...");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_BCL + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_END)) {
                            Timber.i("Get connected devices FINISHED.");
                            mILockPageFragment.onGetConnectedDevicesEnd();
                        } else
                            mILockPageFragment.onGetNewConnectedDevice(
                                    new ConnectedDeviceModel(
                                            0,
                                            keyCommandJson.getString(keyCommand).split(",")[0],
                                            true,
                                            !keyCommandJson.getString(keyCommand).split(",")[1].equals("s")));//s: 0, c:1
                    } else if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Start getting connected devices ...");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_BCL + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_END)) {
                            Timber.i("Get connected devices FINISHED.");
                            mIGatewayPageFragment.onGetConnectedDevicesEnd();
                        } else
                            mIGatewayPageFragment.onGetNewConnectedDevice(new ConnectedDeviceModel(
                                    0,
                                    keyCommandJson.getString(keyCommand).split(",")[0],
                                    true,
                                    !keyCommandJson.getString(keyCommand).split(",")[1].equals("s")));//s: 0, c:1
                    }
                    break;
                case BLE_COMMAND_BSL:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Start getting available devices(bsl) ...");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_BSL + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_END))
                            Timber.i("Get available devices FINISHED.");
                        else
                            mIGatewayPageFragment.onGetNewAvailableBleDevice(new AvailableBleDeviceModel(
                                    keyCommandJson.getString(keyCommand).split(",")[0],
                                    0,
                                    !keyCommandJson.getString(keyCommand).split(",")[1].equals("0")
                                            && keyCommandJson.getString(keyCommand).split(",")[1].equals("1"),
                                    true));
                    }
                    break;
                case BLE_COMMAND_BLL:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Start getting available devices(bll) ...");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_BLL + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_END)) {
                            Timber.i("Get available devices FINISHED.");
                            mIGatewayPageFragment.onGetAvailableBleDevicesEnd();
                            bleBufferStatus = true;
                        } else
                            mIGatewayPageFragment.onGetNewAvailableBleDevice(new AvailableBleDeviceModel(
                                    keyCommandJson.getString(keyCommand).split(",")[0],
                                    Integer.valueOf(keyCommandJson.getString(keyCommand).split(",")[1]),
                                    !keyCommandJson.getString(keyCommand).split(",")[2].equals("0")
                                            && keyCommandJson.getString(keyCommand).split(",")[2].equals("1"),
                                    false));
                    }
                    break;
                case BLE_COMMAND_DIS:
                    if (mILockPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Device disconnects successfully.");
                            mILockPageFragment.onDeviceDisconnectedSuccessfully();
                        }
                    } else if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Device disconnects successfully.");
                            mIGatewayPageFragment.onDeviceDisconnectedSuccessfully();
                        }
                    }
                    break;
                case BLE_COMMAND_WFL:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Start getting wifi networks ...");
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_WFL + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_END)) {
                            Timber.i("Finish getting wifi networks ...");
                            bleBufferStatus = true;
                        } else
                            mIGatewayPageFragment.onFindNewNetworkAroundDevice(new WifiNetworksModel(
                                    keyCommandJson.getString(keyCommand).split(",")[0],
                                    Integer.valueOf(keyCommandJson.getString(keyCommand).split(",")[1])));
                    }
                    break;
                case BLE_COMMAND_CON:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Connecting to wifi network initializing ...");
                            mIGatewayPageFragment.onSetDeviceWifiNetworkSuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_SET:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_WAIT)) {
                            Timber.i("Send Initialize calibration lock done ...");
                            DeviceViewModel.this.mIDeviceSettingFragment.onSendCalibrationLockSuccessful();
                            bleBufferStatus = false;
                            Timber.e("Buffer is busy");
                            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(BLE_COMMAND_SET + BLE_RESPONSE_PUBLIC_WAIT));
                        } else if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Initialize calibration lock OK ...");
                            DeviceViewModel.this.mIDeviceSettingFragment.onInitializeCalibrationLockSuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_PIL:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Set Idle position done.");
                            DeviceViewModel.this.mIDeviceSettingFragment.onSetIdlePositionSuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_PLK:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Set Lock position done.");
                            DeviceViewModel.this.mIDeviceSettingFragment.onSetLockPositionSuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_PLT:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Set Latch position done.");
                            DeviceViewModel.this.mIDeviceSettingFragment.onSetLatchPositionSuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_CFG:
                    if (mIDeviceSettingFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Config done.");
                            DeviceViewModel.this.mIDeviceSettingFragment.onSetConfigSuccessful();
                            mLocalRepository.updateDeviceConfigStatus(((DeviceSettingFragment) mIDeviceSettingFragment).getDevice().getObjectId(),
                                    true);

                        }
                    } else if (mILockPageFragment != null) {
                        mLocalRepository.updateDeviceConfigStatus(((LockPageFragment) mILockPageFragment).getDevice().getObjectId(),
                                keyCommandJson.getBoolean(keyCommand));
                    }
                    break;
                case BLE_COMMAND_DEM:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Write Server MacAddress for gateway done.");
                            DeviceViewModel.this.mIGatewayPageFragment.onWriteServerMacAddressForGateWaySuccessful(mSelectedAvailableBleServer);
                        }
                    }
                    break;
                case BLE_COMMAND_DEP:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Write Server Password for gateway done.");
                            DeviceViewModel.this.mIGatewayPageFragment.onWriteServerPasswordForGateWaySuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_DEO:
                    if (mIGatewayPageFragment != null) {
                        if (keyCommandJson.get(keyCommand).equals(BLE_RESPONSE_PUBLIC_OK)) {
                            Timber.i("Connect to Server Command sent done.");
                            DeviceViewModel.this.mIGatewayPageFragment.onConnectCommandSentToGateWaySuccessful();
                        }
                    }
                    break;
                case BLE_COMMAND_ERR:
                    switch (keyCommandJson.getString(keyCommand)) {
                        case BLE_RESPONSE_ERR_INTER:
                            Timber.e("Device faces with internal Error, try last command again!: %s",
                                    keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_PER:
                            Timber.e("Don't have permission for last command!: %s",
                                    keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_KEY:
                            Timber.e("Key of Last command does not exist!: %s",
                                    keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_OPS:
                            if (mIDeviceSettingFragment != null)
                                mIDeviceSettingFragment.onCheckOldPairingPasswordFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_OPR:
                            if (mIDeviceSettingFragment != null)
                                mIDeviceSettingFragment.onCheckOldOnlinePasswordFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_NPS:
                            if (mIDeviceSettingFragment != null)
                                mIDeviceSettingFragment.onChangePairingPasswordFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_NPR:
                            if (mIDeviceSettingFragment != null)
                                mIDeviceSettingFragment.onChangeOnlinePasswordFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_SET:
                            if (mIDeviceSettingFragment != null) {
                                Timber.i("Initialize calibration lock failed ...");
                                mIDeviceSettingFragment.onInitializeCalibrationLockFailed();
                            }
                            break;
                        case BLE_RESPONSE_ERR_CONFIG:
                            if (mILockPageFragment != null)
                                mILockPageFragment.onDoLockCommandFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_LOCK:
                            if (mILockPageFragment != null)
                                mILockPageFragment.onDoLockCommandFailed(keyCommandJson.getString(keyCommand));
                            break;
                        case BLE_RESPONSE_ERR_UNLOCK:
                            if (mILockPageFragment != null)
                                mILockPageFragment.onDoLockCommandFailed(keyCommandJson.getString(keyCommand));
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //endregion switch response

        sendNextCommandFromBlePool();
    }

    public void sendLockCommand(Fragment parentFragment, String serialNumber, boolean lockCommand) {
        mILockPageFragment = (ILockPageFragment) parentFragment;

        if (isUserLoggedIn()) {
            (new MQTTHandler()).sendLockCommand(this, serialNumber,
                    getBleCommandPart(createBleReadMessage(lockCommand ? BLE_COMMAND_LOC : BLE_COMMAND_ULC), 0, 0));
        } else
            addNewCommandToBlePool(
                    new BleCommand(
                            createBleReadMessage(lockCommand ? BLE_COMMAND_LOC : BLE_COMMAND_ULC),
                            lockCommand ? BLE_COMMAND_LOC : BLE_COMMAND_ULC)
            );
    }

    public void getDeviceCommonSettingInfoFromBleDevice(Fragment parentFragment) {
        if (parentFragment instanceof ILockPageFragment)
            this.mILockPageFragment = (ILockPageFragment) parentFragment;
        else if (parentFragment instanceof IGatewayPageFragment)
            this.mIGatewayPageFragment = (IGatewayPageFragment) parentFragment;

        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BAT), BLE_COMMAND_BAT));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_TYP), BLE_COMMAND_TYP));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_FW), BLE_COMMAND_FW));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_HW), BLE_COMMAND_HW));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_PRD), BLE_COMMAND_PRD));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_SN), BLE_COMMAND_SN));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_DID), BLE_COMMAND_DID));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BCQ), BLE_COMMAND_BCQ));
    }

    public void getLockSpecifiedSettingInfoFromBleDevice(Fragment parentFragment) {
        if (parentFragment instanceof ILockPageFragment) {
            this.mILockPageFragment = (ILockPageFragment) parentFragment;
            addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_CFG), BLE_COMMAND_CFG));
        } else if (parentFragment instanceof IDeviceSettingFragment)
            this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;

        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_RGH), BLE_COMMAND_RGH));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISO), BLE_COMMAND_ISO));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISK), BLE_COMMAND_ISK));
    }

    public void getGatewaySpecifiedInfoFromBleDevice(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;

        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISW), BLE_COMMAND_ISW));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISI), BLE_COMMAND_ISI));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISQ), BLE_COMMAND_ISQ));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_ISR), BLE_COMMAND_ISR));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_RSS), BLE_COMMAND_RSS));
    }

    private void addNewCommandToBlePool(BleCommand bleCommand) {
        if (mBleCommandsPool == null)
            mBleCommandsPool = new ArrayList<>();

        mBleCommandsPool.add(bleCommand);
        sendNextCommandFromBlePool();
    }

    private void sendNextCommandFromBlePool() {
        if (mBleCommandsPool.size() > 0 && bleBufferStatus) {
            Timber.e("Buffer is full");
            bleBufferStatus = false;
            mBleDeviceManager.customWriteCharacteristic(CHARACTERISTIC_UUID_RX, mBleCommandsPool.get(0).getCommand());
            this.mBleTimeOut.postValue(getBleTimeOutBaseOnBleCommandType(mBleCommandsPool.get(0).getCommandType()));
            mBleCommandsPool.remove(0);
        }
    }

    public void getAvailableWifiNetworksAroundDevice(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        Timber.d("1: Send request to get wifi network list");
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_WFL), BLE_COMMAND_WFL));
    }

    public void getConnectedClients(ILockPageFragment mILockPageFragment) {
        this.mILockPageFragment = mILockPageFragment;
        Timber.d("1: Send request to get connected devices list");
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BCQ), BLE_COMMAND_BCQ));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BCL), BLE_COMMAND_BCL));
    }

    public void getConnectedClients(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        Timber.d("1: Send request to get connected devices list");
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BCQ), BLE_COMMAND_BCQ));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BCL), BLE_COMMAND_BCL));
    }

    public void getAvailableBleDevices(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        Timber.d("1: Send request to get available devices list");
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BSL), BLE_COMMAND_BSL));
        addNewCommandToBlePool(new BleCommand(createBleReadMessage(BLE_COMMAND_BLL), BLE_COMMAND_BLL));
    }

    public void setGatewayWifiNetwork(IGatewayPageFragment mIGatewayPageFragment, WifiNetworksModel wifiNetwork) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        addNewCommandToBlePool(
                new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_SSD, wifiNetwork.getSSID()).toString().getBytes(), BLE_COMMAND_SSD));
        addNewCommandToBlePool(
                new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_PSK, wifiNetwork.getPassword()).toString().getBytes(), BLE_COMMAND_PSK));
        addNewCommandToBlePool(
                new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_SEC, wifiNetwork.getAuthenticateType()).toString().getBytes(), BLE_COMMAND_SEC));
        addNewCommandToBlePool(
                new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_CON, true).toString().getBytes(), BLE_COMMAND_CON));
    }

    public void disconnectGatewayWifiNetwork(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_CON, false).toString().getBytes(), BLE_COMMAND_CON));
    }

    public void setDoorInstallation(Fragment parentFragment, boolean doorInstallation) {
        mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_RGH, doorInstallation).toString().getBytes(), BLE_COMMAND_RGH));
    }

    public void changePairingPasswordViaBle(Fragment parentFragment, Integer oldPassword, Integer newPassword) {
        mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;

        oldPairingPassword = oldPassword;
        newPairingPassword = newPassword;

        changePairingPasswordViaBleInitialStep();
    }

    public void changeOnlinePasswordViaBle(Fragment parentFragment, String oldPassword, String newPassword) {
        mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;

        oldOnlinePassword = oldPassword;
        newOnlinePassword = newPassword;

        changeOnlinePasswordViaBleInitialStep();
    }

    private void changePairingPasswordViaBleInitialStep() {
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_OPS, oldPairingPassword).toString().getBytes(), BLE_COMMAND_OPS));
    }

    private void changeOnlinePasswordViaBleInitialStep() {
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_OPR, oldOnlinePassword).toString().getBytes(), BLE_COMMAND_OPR));
    }

    private void changePairingPasswordViaBleFinalStep() {
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_NPS, newPairingPassword).toString().getBytes(), BLE_COMMAND_NPS));
    }

    private void changeOnlinePasswordViaBleFinalStep() {
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_NPR, newOnlinePassword).toString().getBytes(), BLE_COMMAND_NPR));
    }

    public void resetBleDevice(Fragment parentFragment) {
        mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_RST, JSONObject.NULL).toString().getBytes(), BLE_COMMAND_RST));
    }

    public LiveData<Boolean> isConnected() {
        return mIsConnected;
    }

    public LiveData<Boolean> isSupported() {
        return mIsSupported;
    }

    private void disconnectFromBleDevice(String macAddress) {
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_DIS, macAddress).toString().getBytes(), BLE_COMMAND_DIS));
    }

    public void initializeCalibrationLock(Fragment parentFragment) {
        this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_SET, JSONObject.NULL).toString().getBytes(), BLE_COMMAND_SET));
    }

    public void applyCalibrationIdlePosition(Fragment parentFragment) {
        this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_PIL, JSONObject.NULL).toString().getBytes(), BLE_COMMAND_PIL));
    }

    public void applyCalibrationLatchPosition(Fragment parentFragment) {
        this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_PLT, JSONObject.NULL).toString().getBytes(), BLE_COMMAND_PLT));
    }

    public void applyCalibrationLockPosition(Fragment parentFragment) {
        this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_PLK, JSONObject.NULL).toString().getBytes(), BLE_COMMAND_PLK));
    }

    public void sendConfigCommand(Fragment parentFragment) {
        this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_CFG, true).toString().getBytes(), BLE_COMMAND_CFG));
    }

    public void setServerMacAddressForGateWay(IGatewayPageFragment mIGatewayPageFragment, AvailableBleDeviceModel availableBleDeviceModel) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        this.mSelectedAvailableBleServer = availableBleDeviceModel;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_DEM, availableBleDeviceModel.getMacAddress()).toString().getBytes(), BLE_COMMAND_DEM));
    }

    public void setServerPasswordForGateWay(IGatewayPageFragment mIGatewayPageFragment, AvailableBleDeviceModel availableBleDeviceModel) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        addNewCommandToBlePool(new BleCommand((createJSONObjectWithKeyValue(BLE_COMMAND_DEP, availableBleDeviceModel.getPassword()).toString().getBytes()), BLE_COMMAND_DEP));
    }

    public void connectGateWayToServer(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_DEO, true).toString().getBytes(), BLE_COMMAND_DEO));
    }

    public void disconnectGateWayFromServer(IGatewayPageFragment mIGatewayPageFragment) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        addNewCommandToBlePool(new BleCommand(createJSONObjectWithKeyValue(BLE_COMMAND_DEO, false).toString().getBytes(), BLE_COMMAND_DEO));
    }
    //endregion BLE Methods

    //region Online Methods
//    public void validateLockInOnlineDatabase(AddDeviceFragment fragment, String serialNumber) {
//        setRequestType("validateLockInOnlineDatabase");
//        mIAddDeviceFragment = fragment;
//        mNetworkRepository.getDeviceObjectIdWithSerialNumber(this, serialNumber);
//    }

    public void validateLockInOnlineDatabase(Fragment fragment, String serialNumber) {
        setRequestType("validateLockInOnlineDatabase");

        if (fragment instanceof IAddDeviceFragment)
            DeviceViewModel.this.mIAddDeviceFragment = (IAddDeviceFragment) fragment;
        else if (fragment instanceof ILoginFragment)
            DeviceViewModel.this.mILoginFragment = (ILoginFragment) fragment;

        DeviceViewModel.this.mNetworkRepository.getDeviceObjectIdWithSerialNumber(this, serialNumber);
    }

    public void insertOnlineUserDevice(Fragment fragment, UserDeviceModel userLock) {
        setRequestType("insertOnlineUserDevice");

        if (fragment instanceof IAddDeviceFragment)
            DeviceViewModel.this.mIAddDeviceFragment = (IAddDeviceFragment) fragment;
        else if (fragment instanceof ILoginFragment)
            DeviceViewModel.this.mILoginFragment = (ILoginFragment) fragment;
        else if (fragment instanceof IManageMembersFragment)
            DeviceViewModel.this.mIManageMembersFragment = (IManageMembersFragment) fragment;

        DeviceViewModel.this.mNetworkRepository.insertUserLock(this, userLock);
    }

    public void addLockToUserLock(Fragment fragment, String userLockObjectId, String lockObjectId) {
        setRequestType("addLockToUserLock");

        if (fragment instanceof IAddDeviceFragment)
            DeviceViewModel.this.mIAddDeviceFragment = (IAddDeviceFragment) fragment;
        else if (fragment instanceof ILoginFragment)
            DeviceViewModel.this.mILoginFragment = (ILoginFragment) fragment;
        else if (fragment instanceof IManageMembersFragment)
            DeviceViewModel.this.mIManageMembersFragment = (IManageMembersFragment) fragment;

        mNetworkRepository.addLockToUserLock(this, userLockObjectId, new AddRelationHelperModel(lockObjectId));
    }

    public void addUserLockToUser(Fragment fragment, String userObjectId, String userLockObjectId) {
        setRequestType("addUserLockToUser");

        if (fragment instanceof IAddDeviceFragment)
            DeviceViewModel.this.mIAddDeviceFragment = (IAddDeviceFragment) fragment;
        else if (fragment instanceof ILoginFragment)
            DeviceViewModel.this.mILoginFragment = (ILoginFragment) fragment;
        else if (fragment instanceof IManageMembersFragment)
            DeviceViewModel.this.mIManageMembersFragment = (IManageMembersFragment) fragment;

        mNetworkRepository.addUserLockToUser(this, userObjectId, new AddRelationHelperModel(userLockObjectId));
    }

    public void setListenerForDevice(Fragment fragment, Device mDevice) {
        if (fragment instanceof LockPageFragment)
            mILockPageFragment = (ILockPageFragment) fragment;
        else if (fragment instanceof GatewayPageFragment)
            mIGatewayPageFragment = (IGatewayPageFragment) fragment;

        deviceEventHandler = mNetworkRepository.setListenerForDevice(this, mDevice);
    }

    public void removeListenerForDevice(Fragment fragment, Device mDevice) {
        if (fragment instanceof LockPageFragment)
            mILockPageFragment = (ILockPageFragment) fragment;
        else if (fragment instanceof GatewayPageFragment)
            mIGatewayPageFragment = (IGatewayPageFragment) fragment;

        if (deviceEventHandler != null)
            mNetworkRepository.removeListenerForDevice(this, mDevice, deviceEventHandler);
    }

    public void setListenerForUser(Fragment fragment, String mUserDeviceObjectId) {
        if (fragment instanceof LockPageFragment)
            mILockPageFragment = (ILockPageFragment) fragment;
        else if (fragment instanceof GatewayPageFragment)
            mIGatewayPageFragment = (IGatewayPageFragment) fragment;

        mNetworkRepository.setListenerForUser(this, mUserDeviceObjectId);
    }

    public void enablePushNotification(String serialNumber) {
        setRequestType("enablePushNotification");
        mNetworkRepository.enablePushNotification(this, serialNumber);
    }

    public void removeDevice(Fragment parentFragment, boolean removeAllMembers, Device mDevice) {
        DeviceViewModel.this.mIDeviceSettingFragment = (IDeviceSettingFragment) parentFragment;
        this.lastDeletedDevice = mDevice;

        if (mDevice.isDeviceSavedInServer()) {
            if (removeAllMembers) {
                setRequestType("removeDeviceForAllMembers");
                DeviceViewModel.this.mNetworkRepository.removeDeviceForAllMembers(this, mDevice.getObjectId());
            } else {
                setRequestType("removeDeviceForOneMember");
                DeviceViewModel.this.mNetworkRepository.removeDeviceForOneMember(this, mDevice.getUserLockObjectId());
            }
        } else
            mLocalRepository.deleteDevice(mDevice);
    }
    //endregion Online Methods

    //region Declare Methods
    public void initMQTT(Context context, String deviceSerialNumber) {
        if (isUserLoggedIn())
            MQTTHandler.setup(this, context, deviceSerialNumber);
    }

    public void disconnectMQTT() {
        MQTTHandler.mqttDisconnect();
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

    public void disconnectFromBleDevice(ILockPageFragment mILockPageFragment, ConnectedDeviceModel connectedDeviceModel) {
        this.mILockPageFragment = mILockPageFragment;
        disconnectFromBleDevice(connectedDeviceModel.getMacAddress());
    }

    public void disconnectFromBleDevice(IGatewayPageFragment mIGatewayPageFragment, ConnectedDeviceModel connectedDeviceModel) {
        this.mIGatewayPageFragment = mIGatewayPageFragment;
        disconnectFromBleDevice(connectedDeviceModel.getMacAddress());
    }

    public void setBleBufferStatus(boolean status) {
        bleBufferStatus = status;
        sendNextCommandFromBlePool();
    }

    public LiveData<Integer> getBleTimeOut() {
        return this.mBleTimeOut;
    }

    public void readRemoteRssi() {
        if (mBleDeviceManager != null)
            mBleDeviceManager.readRemoteRssi();
    }
    //endregion Declare Methods

    //region SharePreferences
    //endregion SharePreferences
}