package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.utils.ble.ConnectedClientsAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBleCallback;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.support.v4.content.ContextCompat.getColor;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_CONFIG;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_LOCK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.BLE_RESPONSE_ERR_UNLOCK;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.BleHelper.isMyPhone;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusText;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.disableLockCommandRingImageView;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.enableLockCommandRingImageView;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.getDialogLayoutParams;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBatteryStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleMoreInfoImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setConnectedClientsStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setIsLockedImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setTypeface;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class LockPageFragment extends BaseFragment
        implements
        IBleScanListener,
        ILockPageFragment,
        CustomBleCallback,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private ImageView imgIsLockedLockPage;
    private ImageView imgIsLockedLockPageCenter;
    private ImageView imgIsLockedLockPageRing = null;
    private ImageView imgBatteryStatusLockPage;
    private ImageView imgConnectedClientsLockPage;
    private ImageView imgBleLockPage;
    private ImageView imgManageMembersLockPage;
    private ImageView imgMoreInfoLockPage;

    private TextView txvDeviceNameLockPage;
    private TextView txvNewUpdateLockPage;
    private TextView txvBriefStatusLockPage;
    private TextView txvDeviceTypeLockPage;
    private TextView txvValidDataStatusLockPage;

    private RelativeLayout rllValidDataStatusLockPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean isConnectedToBleDevice = false;
    private boolean lockUnlockCommandInProcess = false;
    //endregion Declare Variables

    //region Declare Arrays & Lists
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private Context mContext;
    private DeviceViewModel mDeviceViewModel;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private Device mDevice;
    private ConnectedClientsAdapter mConnectedClientsAdapter;

    private Dialog mConnectedClientsListDialog;
    private Dialog disconnectClientDialog;
    private Dialog loseAccessDialog;

    private ScheduledFuture freeBleBuffer;
    private Handler mProximityHandler;
    private Runnable mProximityRunnable;
    //endregion Declare Objects

    //region Constructor
    public LockPageFragment() {
    }

    public static LockPageFragment newInstance(Device device) {
        LockPageFragment fragment = new LockPageFragment();
        fragment.mDevice = device;
        return fragment;
    }
    //endregion Constructor

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        isConnectedToBleDevice = false;
        //endregion Initialize Variables

        //region Initialize Objects
        mContext = getContext();
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mDeviceViewModel.isConnected().observe(this, isConnected -> {
            if (isConnected != null) {
                isConnectedToBleDevice = isConnected;
                setBleConnectionStatusImage(imgBleLockPage, isConnected);

                if (!isUserLoggedIn() && !isConnected) {
                    updateViewData(true);
                    closeAllDialogs();

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
            }
        });
        this.mDeviceViewModel.isSupported().observe(this, isSupported -> {
            if (isSupported != null)
                if (isSupported) {
                    this.mDeviceViewModel.getLockSpecifiedSettingInfoFromBleDevice(this);
                    this.mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice(this);
                    updateViewData(!isConnectedToBleDevice);
                } else if (!isUserLoggedIn()) {
                    updateViewData(true);

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
        });
        this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, device -> {
            mDevice = device;

            if (device == null) {
                mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
                    ((DeviceActivity) getActivity()).setViewPagerAdapter(
                            new CustomDeviceAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), devices));
                });
            } else
                updateViewData(!isUserLoggedIn() && !isConnectedToBleDevice);
        });
        this.mDeviceViewModel.getBleTimeOut().observe(this, this::handleBleBufferStatus);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgIsLockedLockPage = view.findViewById(R.id.img_lock_status_lock_page);
        imgIsLockedLockPageCenter = view.findViewById(R.id.img_lock_status_lock_page_center);
        imgIsLockedLockPageRing = view.findViewById(R.id.img_lock_status_lock_page_ring);
        imgBatteryStatusLockPage = view.findViewById(R.id.img_battery_status_lock_page);
        imgConnectedClientsLockPage = view.findViewById(R.id.img_connected_devices_lock_page);
        imgBleLockPage = view.findViewById(R.id.img_ble_lock_page);
        imgManageMembersLockPage = view.findViewById(R.id.img_manage_members_lock_page);
        imgMoreInfoLockPage = view.findViewById(R.id.img_more_info_lock_page);

        txvDeviceNameLockPage = view.findViewById(R.id.txv_device_name_lock_page);
        txvNewUpdateLockPage = view.findViewById(R.id.txv_new_update_lock_page);
        txvBriefStatusLockPage = view.findViewById(R.id.txv_brief_status_lock_page);
        txvDeviceTypeLockPage = view.findViewById(R.id.txv_device_type_lock_page);
        txvValidDataStatusLockPage = view.findViewById(R.id.txv_valid_data_status_lock_page);

        rllValidDataStatusLockPage = view.findViewById(R.id.rll_valid_data_status_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgIsLockedLockPageRing.setOnClickListener(this);
        imgBatteryStatusLockPage.setOnClickListener(this);
        imgConnectedClientsLockPage.setOnClickListener(this);
        imgBleLockPage.setOnClickListener(this);
        imgManageMembersLockPage.setOnClickListener(this);
        imgMoreInfoLockPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        closeProgressDialog();

        updateViewData(!isUserLoggedIn());

        if (isUserLoggedIn()) {
            Objects.requireNonNull(LockPageFragment.this.getActivity()).runOnUiThread(() -> {
                this.mDeviceViewModel.setListenerForDevice(this, mDevice);
                this.mDeviceViewModel.initMQTT(getActivity(), mDevice.getGateWayId());
                this.mDeviceViewModel.getUserLockInfo(mDevice.getObjectId()).observe(this, new Observer<UserLock>() {
                    @Override
                    public void onChanged(@Nullable UserLock userLock) {
                        if (userLock != null && !userLock.getAdminStatus()) {
                            mDeviceViewModel.setListenerForUser(LockPageFragment.this, userLock.getObjectId());
                            mDeviceViewModel.getUserLockInfo(mDevice.getObjectId()).removeObserver(this);
                        }
                    }
                });
            });
        }
        //endregion init
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isUserLoggedIn()) {
            disconnectDevice();
            this.mDeviceViewModel.removeListenerForDevice(this, mDevice);
        }

        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_lock_status_lock_page_ring:
                if (isConnectedToBleDevice)
                    sendLockCommand(!mDevice.getIsLocked());
                else if (isUserLoggedIn()) {
                    if (mDevice.getInternetStatus())
                        sendLockCommand(!mDevice.getIsLocked());
                    else
                        showToast("Device is not connected to internet");
                }
                break;
            case R.id.img_battery_status_lock_page:
                if (isUserLoggedIn() || isConnectedToBleDevice)
                    showToast(String.format("Battery percentage is %s%%", mDevice.getBatteryPercentage().toString()));
                break;
            case R.id.img_connected_devices_lock_page:
                if (isConnectedToBleDevice)
                    handleConnectedClients();
                else if (isUserLoggedIn())
                    showToast(getConnectedClientsMessage(mDevice.getConnectedClientsCount()));
                break;
            case R.id.img_ble_lock_page:
                handleLockBleConnection();
                break;
            case R.id.img_manage_members_lock_page:
                handleDeviceMembers();
                break;
            case R.id.img_more_info_lock_page:
                if (isUserLoggedIn() || isConnectedToBleDevice)
                    addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                            R.id.frl_device_activity,
                            DeviceSettingFragment.newInstance(mDevice, "LOCK", mDeviceViewModel));
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    private String getConnectedClientsMessage(int connectedClientsCount) {
        switch (connectedClientsCount) {
            case 0:
                return "No device is connect to Lock";
            case 1:
                return "One device is connected to Lock";
            default:
                return String.format("%d devices are connected to Lock", connectedClientsCount);
        }
    }
    //endregion Main CallBacks

    //region BLE CallBacks
    @Override
    public void onFindBleSuccess(List response) {
    }

    @Override
    public void onFindBleFault() {
    }

    @Override
    public void onAdapterItemClick(BaseModel client) {
        handleDialogDisconnectClientFromDevice((ConnectedDeviceModel) client);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onSendLockCommandSuccessful(String command) {
        showToast(String.format("%s command sent.", command));
        Timber.d("Command : %s , received by device successfully.", command);
    }

    @Override
    public void onSendLockCommandFailed(String error) {
        showToast(String.format("%s failed: ", error));

//        Objects.requireNonNull(LockPageFragment.this.getActivity()).runOnUiThread(() -> imgIsLockedLockPage.setEnabled(true));
    }

    @Override
    public void onGetNewConnectedDevice(ConnectedDeviceModel connectedDeviceModel) {
        if (connectedDeviceModel.isClient() && !isMyPhone(connectedDeviceModel.getMacAddress())) {
            connectedDeviceModel.setIndex(mConnectedClientsAdapter.getItemCount());
            mConnectedClientsAdapter.addConnectedDevice(connectedDeviceModel);
        }
    }

    @Override
    public void onDeviceDisconnectedSuccessfully() {
        closeProgressDialog();

        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        if (mConnectedClientsListDialog != null) {
            if (!mConnectedClientsListDialog.isShowing())
                mConnectedClientsListDialog.show();

//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            this.mDeviceViewModel.getConnectedClients(this);
        } else
            handleConnectedClients();
    }

    @Override
    public void onRemoveAccessToDeviceForUser() {
        handleDialogLoseAccess();
    }

    @Override
    public void onDoLockCommandSuccessful(String command) {
        showToast(String.format("%s done.: ", command));
        ViewHelper.changeLockStatus = true;
        lockUnlockCommandInProcess = false;
    }

    @Override
    public void onDoLockCommandFailed(String error) {
        disableLockCommandRingImageView(imgIsLockedLockPageRing);

        switch (error) {
            case BLE_RESPONSE_ERR_LOCK:
                showToast("Not Lock");
                break;
            case BLE_RESPONSE_ERR_UNLOCK:
                showToast("Not Unlock");
                break;
            case BLE_RESPONSE_ERR_CONFIG:
                showToast("Please config lock in setting page.");
                break;
        }

        lockUnlockCommandInProcess = false;
    }

    @Override
    public void onGetConnectedDevicesEnd() {
        getActivity().runOnUiThread(() -> {
            if (mConnectedClientsListDialog != null)
                mConnectedClientsListDialog.findViewById(R.id.prg_top_dialog_connected_device).setVisibility(INVISIBLE);
        });
    }

    @Override
    public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    }

    @Override
    public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
    }

    @Override
    public void onBleCharacteristicChange(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if (rssi >= -30)
            showToast("1   خیلی نزدیک شدی");
        else if (rssi >= -35)
            showToast("2   تقریبا نزدیک شدی");
        else if (rssi >= -40)
            showToast("3   نزدیک شدی");
        else if (rssi >= -45)
            showToast("4   نسبتا دوری");
        else if (rssi >= -50)
            showToast("5   نسبتا دوری");
        else if (rssi >= -60)
            showToast("6   داری نزدیک میشی");
        else if (rssi >= -80)
            showToast("7   خیلی دوری");
        else
            showToast("8   خیلی پرتی");
    }
    //endregion BLE CallBacks

    //region Declare BLE Methods
    private void handleLockBleConnection() {
        if (isUserLoggedIn())//TODO check if device connection status is false, user can connect direct
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else {
            if (isConnectedToBleDevice)
                disconnectDevice();
            else
                connectToDevice();
        }
    }

    public boolean disconnectDevice() {
        if (isConnectedToBleDevice && this.mDeviceViewModel != null) {
            this.mDeviceViewModel.disconnect();
            new Handler().postDelayed(() -> {
                updateViewData(true);
            }, 1500);
            return false;
        } else
            return true;
    }

    private void handleConnectedClients() {
        if (isConnectedToBleDevice)
            handleDialogListOfConnectedClientsToDevice();
        else if (mConnectedClientsListDialog != null) {
            mConnectedClientsListDialog.dismiss();
            mConnectedClientsListDialog = null;
        }
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity(), this);
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(mDevice.getBleDeviceMacAddress());

        if (tempDevice != null) {
            openProgressDialog(getActivity(), null, "Pairing ...");
            LockPageFragment.this.mDeviceViewModel.connect(this, new ScannedDeviceModel(tempDevice));
        } else if (getScanPermission(this))
            scanDevices();
    }

    private void scanDevices() {
        openProgressDialog(getActivity(), null, "Pairing ...");

        if (!mBluetoothLEHelper.isScanning()) {
            mBluetoothLEHelper.setScanPeriod(1000);
            mBluetoothLEHelper.scanLeDevice(true);

            (new Handler()).postDelayed(() -> {
                for (int i = 0; i < TIMES_TO_SCAN_BLE_DEVICES; i++)
                    if (connectToSpecificBleDevice(getListOfScannedDevices()))
                        break;
            }, mBluetoothLEHelper.getScanPeriod());
        } else mBluetoothLEHelper.scanLeDevice(false);
    }

    private boolean connectToSpecificBleDevice(List<ScannedDeviceModel> listOfScannedDevices) {
        for (ScannedDeviceModel device : listOfScannedDevices) {
            if (device.getMacAddress().toLowerCase().equals(mDevice.getBleDeviceMacAddress().toLowerCase())) {
                this.mDeviceViewModel.connect(this, device);
                return true;
            }
        }

        return false;
    }

    private void sendLockCommand(boolean lockCommand) {
        if (!lockUnlockCommandInProcess) {
            lockUnlockCommandInProcess = true;
            new Handler().postDelayed(() -> lockUnlockCommandInProcess = false, 25000);
            enableLockCommandRingImageView(getActivity(), imgIsLockedLockPageRing, lockCommand ? 0 : 1);
            this.mDeviceViewModel.sendLockCommand(this, mDevice.getSerialNumber(), lockCommand);
        }
    }

    private List<ScannedDeviceModel> getListOfScannedDevices() {
        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        for (BluetoothLE device : mBluetoothLEHelper.getListDevices())
            mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        return mScannedDeviceModelList;
    }

    public Device getDevice() {
        return mDevice;
    }

    private void handleDialogListOfConnectedClientsToDevice() {
        if (mConnectedClientsListDialog != null) {
            mConnectedClientsListDialog.dismiss();
            mConnectedClientsListDialog = null;
        }

        mConnectedClientsListDialog = new Dialog(requireContext());
        mConnectedClientsListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mConnectedClientsListDialog.setContentView(R.layout.dialog_connected_devices);
        mConnectedClientsListDialog.setCancelable(false);

        if (mConnectedClientsAdapter == null) {
            mConnectedClientsAdapter = new ConnectedClientsAdapter(this, new ArrayList<>());
//                    Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        }

//        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());

        setTypeface((TextView) mConnectedClientsListDialog.findViewById(R.id.txv_title_dialog_add_member), "roboto_medium");

        ProgressBar prgDialogConnectedClients = mConnectedClientsListDialog.findViewById(R.id.prg_top_dialog_connected_device);
        RecyclerView rcvDialogConnectedClients = mConnectedClientsListDialog.findViewById(R.id.rcv_dialog_connected_devices);
        Button btnCancelDialogConnectedClients = mConnectedClientsListDialog.findViewById(R.id.btn_cancel_dialog_connected_devices);
        Button btnScanDialogConnectedClients = mConnectedClientsListDialog.findViewById(R.id.btn_scan_dialog_connected_devices);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedClientsAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            mConnectedClientsListDialog.dismiss();
            mConnectedClientsListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            prgDialogConnectedClients.setVisibility(VISIBLE);
//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            mDeviceViewModel.getConnectedClients(this);
        });

        mConnectedClientsListDialog.setOnDismissListener(dialog -> {
            if (mConnectedClientsListDialog != null) {
                mConnectedClientsListDialog.dismiss();
                mConnectedClientsListDialog = null;
            }
        });

//        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
        LockPageFragment.this.mDeviceViewModel.getConnectedClients(this);

        if (!mConnectedClientsListDialog.isShowing()) {
            mConnectedClientsListDialog.show();
            Objects.requireNonNull(mConnectedClientsListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(mConnectedClientsListDialog));
        }
    }

    private void handleDialogDisconnectClientFromDevice(ConnectedDeviceModel mConnectedDeviceModel) {
        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        disconnectClientDialog = new Dialog(requireContext());
        disconnectClientDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        disconnectClientDialog.setContentView(R.layout.dialog_disconnect_device);
        disconnectClientDialog.setCancelable(false);

        setTypeface((TextView) disconnectClientDialog.findViewById(R.id.txv_title_dialog_disconnect_device), "roboto_medium");

        Button btnCancelDialogDisconnectClient =
                disconnectClientDialog.findViewById(R.id.btn_cancel_dialog_disconnect_device);
        Button btnDisconnectDialogDisconnectClient =
                disconnectClientDialog.findViewById(R.id.btn_disconnect_dialog_disconnect_device);

        btnCancelDialogDisconnectClient.setOnClickListener(v -> {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        });

        btnDisconnectDialogDisconnectClient.setOnClickListener(v -> {
            openProgressDialog(getContext(), null, "Disconnect device ...");
            mDeviceViewModel.disconnectFromBleDevice(this, mConnectedDeviceModel);
        });

        disconnectClientDialog.show();
        Objects.requireNonNull(disconnectClientDialog.getWindow())
                .setAttributes(getDialogLayoutParams(disconnectClientDialog));
    }

    @Override
    public void onReadBCQDone() {
        lockUnlockCommandInProcess = false;
    }

    //endregion Declare BLE Methods

    //region Declare Classes & Interfaces
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Declare Classes & Interfaces

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setBleMoreInfoImage(imgMoreInfoLockPage, (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault);

        txvDeviceTypeLockPage.setText(mDevice.getDeviceType());

        setIsLockedImage(imgIsLockedLockPageCenter, imgIsLockedLockPageRing, imgIsLockedLockPage,
                (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault || !mDevice.getConfigStatus() ? 2 : (mDevice.getIsLocked() ? 1 : 0));

        setBatteryStatusImage((!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault, imgBatteryStatusLockPage, mDevice.getBatteryPercentage());
        setConnectedClientsStatusImage(
                imgConnectedClientsLockPage, (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault, mDevice.getConnectedClientsCount());

        imgManageMembersLockPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
        txvDeviceNameLockPage.setTextColor((!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));

        txvDeviceNameLockPage.setText(mDevice.getBleDeviceName());

        try {
            txvBriefStatusLockPage.setText(
                    (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ?
                            getString(R.string.fragment_text_view_data_not_synced) :
                            (!mDevice.getConfigStatus() ? getString(R.string.fragment_text_view_lock_not_config) :
                                    getLockBriefStatusText(mDevice.getIsLocked(), mDevice.getIsDoorClosed())));
            txvBriefStatusLockPage.setTextColor(
                    (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ? getColor(mContext, R.color.md_grey_500) :
                            (!mDevice.getConfigStatus() ? getColor(mContext, R.color.md_red_700) :
                                    getColor(mContext, getLockBriefStatusColor(mDevice.getIsLocked(), mDevice.getIsDoorClosed()))));
        } catch (Exception e) {
        }

        txvNewUpdateLockPage.setText(null);

        closeProgressDialog();

        if (isUserLoggedIn()) {
            if (!mDevice.getInternetStatus()) {
                rllValidDataStatusLockPage.setVisibility(VISIBLE);
                txvValidDataStatusLockPage.setText(
                        (mDevice.getUpdated() != null
                                ? String.format("Not connected to internet.\nLast update: %s", new Date(mDevice.getUpdated()))
                                : "Please login again to see updated data.")
                );
            } else {
                rllValidDataStatusLockPage.setVisibility(GONE);
                txvValidDataStatusLockPage.setText(null);
            }
        }
    }

    private void handleDialogLoseAccess() {
        loseAccessDialog = new Dialog(getActivity().getBaseContext());
        loseAccessDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loseAccessDialog.setContentView(R.layout.dialog_lose_access);
        loseAccessDialog.setCancelable(false);

        setTypeface((TextView) loseAccessDialog.findViewById(R.id.txv_title_dialog_lose_access), "roboto_medium");

        Button btnOkDialogLoseAccess = loseAccessDialog.findViewById(R.id.btn_ok_dialog_lose_access);

        btnOkDialogLoseAccess.setOnClickListener(v -> {
            loseAccessDialog.dismiss();
            loseAccessDialog = null;
            Objects.requireNonNull(getActivity()).finish();
        });

        loseAccessDialog.setOnDismissListener(dialog -> Objects.requireNonNull(getActivity()).finish());

        loseAccessDialog.show();
        Objects.requireNonNull(loseAccessDialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(loseAccessDialog));
    }

    private void handleDeviceMembers() {
        if (isUserLoggedIn()) {
            if (mDevice.getMemberAdminStatus())
                addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                        R.id.frl_device_activity, ManageMembersFragment.newInstance(mDevice));
            else
                showToast("Access Denied");
        } else
            showToast("This is not available in Local Mode");
    }

    private void closeAllDialogs() {
        if (mConnectedClientsListDialog != null) {
            mConnectedClientsListDialog.dismiss();
            mConnectedClientsListDialog = null;
        }

        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        if (!isUserLoggedIn())
            closeProgressDialog();
    }

    private void handleBleBufferStatus(Integer timeout) {
        if (timeout != null) {
            cancelSchedulerFreeBleBuffer();

            if (timeout != -1)
                //schedule a task to execute after timeout milliSeconds
                freeBleBuffer = Executors.newScheduledThreadPool(1).schedule(() -> {
                    mDeviceViewModel.setBleBufferStatus(true);
                }, timeout, TimeUnit.MILLISECONDS);
        }
    }

    private void cancelSchedulerFreeBleBuffer() {
        if (freeBleBuffer != null)
            freeBleBuffer.cancel(true);
    }

    private double calculateDistance(Double txPower, Double rssi) {
        double ratio = rssi / txPower;
        if (rssi == 0.0) { // Cannot determine accuracy, return -1.
            return -1.0;
        } else if (ratio < 1.0) { //default ratio
            return Math.pow(ratio, 10.0);
        }//rssi is greater than transmission strength
        return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
    }

    public void startRepeatingReadProximityTask() {
        if (mProximityHandler == null)
            mProximityHandler = new Handler();

        if (mProximityRunnable == null)
            mProximityRunnable = () -> {
                LockPageFragment.this.mDeviceViewModel.readRemoteRssi();
                mProximityHandler.postDelayed(mProximityRunnable, 2000);
            };

        mProximityRunnable.run();
    }

    public void stopRepeatingReadProximityTask() {
        if (mProximityHandler != null && mProximityRunnable != null)
            mProximityHandler.removeCallbacks(mProximityRunnable);
    }
    //endregion Declare Methods
}
