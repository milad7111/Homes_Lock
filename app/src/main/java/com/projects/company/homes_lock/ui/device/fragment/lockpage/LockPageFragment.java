package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.utils.ble.ConnectedDevicesAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.support.v4.content.ContextCompat.getColor;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getLockBriefStatusText;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.getDialogLayoutParams;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBatteryStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleMoreInfoImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setConnectedDevicesStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setIsLockedImage;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class LockPageFragment extends BaseFragment
        implements
        IBleScanListener,
        ILockPageFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    ImageView imgIsLockedLockPage;
    ImageView imgBatteryStatusLockPage;
    ImageView imgConnectedDevicesLockPage;
    ImageView imgBleLockPage;
    ImageView imgManageMembersLockPage;
    ImageView imgMoreInfoLockPage;

    TextView txvDeviceNameLockPage;
    TextView txvNewUpdateLockPage;
    TextView txvBriefStatusLockPage;
    TextView txvDeviceTypeLockPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean isConnectedToBleDevice = false;
    //endregion Declare Variables

    //region Declare Arrays & Lists
    //endregion Declare Arrays & Lists

    //region Declare Objects
    private Context mContext;
    private DeviceViewModel mDeviceViewModel;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private Device mDevice;
    private ConnectedDevicesAdapter mConnectedDevicesAdapter;

    private Dialog connectedDevicesListDialog;
    private Dialog disconnectClientDialog;
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

                if (!isConnected) {
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
                    this.mDeviceViewModel.getDeviceDataFromBleDevice();
                    this.mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice();
                    this.mDeviceViewModel.getLockSpecifiedSettingInfoFromBleDevice();
                    updateViewData(false);
                } else {
                    updateViewData(true);

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
        });
        this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, device -> {
            mDevice = device;
            updateViewData(!isConnectedToBleDevice);
        });
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
        imgBatteryStatusLockPage = view.findViewById(R.id.img_battery_status_lock_page);
        imgConnectedDevicesLockPage = view.findViewById(R.id.img_connected_devices_lock_page);
        imgBleLockPage = view.findViewById(R.id.img_ble_lock_page);
        imgManageMembersLockPage = view.findViewById(R.id.img_manage_members_lock_page);
        imgMoreInfoLockPage = view.findViewById(R.id.img_more_info_lock_page);

        txvDeviceNameLockPage = view.findViewById(R.id.txv_device_name_lock_page);
        txvNewUpdateLockPage = view.findViewById(R.id.txv_new_update_lock_page);
        txvBriefStatusLockPage = view.findViewById(R.id.txv_brief_status_lock_page);
        txvDeviceTypeLockPage = view.findViewById(R.id.txv_device_type_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgIsLockedLockPage.setOnClickListener(this);
        imgConnectedDevicesLockPage.setOnClickListener(this);
        imgBleLockPage.setOnClickListener(this);
        imgManageMembersLockPage.setOnClickListener(this);
        imgMoreInfoLockPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        closeProgressDialog();

        updateViewData(!isUserLoggedIn());

        if (isUserLoggedIn()) {
            this.mDeviceViewModel.setListenerForDevice(this, mDevice);
            this.mDeviceViewModel.initMQTT(getActivity(), mDevice.getObjectId());
        }
        //endregion init
    }

    @Override
    public void onPause() {
        super.onPause();

        this.mDeviceViewModel.disconnectMQTT();
        this.mDeviceViewModel.removeListenerForDevice(mDevice);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.mDeviceViewModel.disconnect();
        this.mDeviceViewModel.disconnectMQTT();
        this.mDeviceViewModel.removeListenerForDevice(mDevice);

        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_lock_status_lock_page:
                if (isUserLoggedIn() || isConnectedToBleDevice)
                    sendLockCommand(!mDevice.getIsLocked());
                break;
            case R.id.img_connected_devices_lock_page:
                if (isConnectedToBleDevice)
                    handleConnectedDevices();
                break;
            case R.id.img_ble_lock_page:
                handleLockBleConnection();
                break;
            case R.id.img_manage_members_lock_page:
                handleDeviceMembers();
                break;
            case R.id.img_more_info_lock_page:
                if (isUserLoggedIn() || isConnectedToBleDevice) {
                    DeviceSettingFragment mDeviceSettingFragment = DeviceSettingFragment.newInstance(mDevice.getObjectId(), mDeviceViewModel);
                    mDeviceSettingFragment.haveUnPairPermission().observe(this, unPairPermission -> {
                        if (this.mBluetoothLEHelper != null)
                            this.mBluetoothLEHelper.unPairDevice(mDevice.getBleDeviceMacAddress());
                    });

                    addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                            R.id.frg_lock_activity, mDeviceSettingFragment);
                }
                break;
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
        mDevice = response;
        updateViewData(false);
        this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onSendLockCommandSuccessful(String command) {
        Log.d(getTag(), String.format("Command : %s , received by device successfully.", command));
    }

    @Override
    public void onSendLockCommandFailed(String command) {
        Log.d(getTag(), String.format("Command : %s , failed.", command));
    }

    @Override
    public void onGetNewConnectedDevice(ConnectedDeviceModel connectedDeviceModel) {
        if (connectedDeviceModel.isClient()) {
            connectedDeviceModel.setIndex(mConnectedDevicesAdapter.getItemCount());
            mConnectedDevicesAdapter.addConnectedDevice(connectedDeviceModel);
        }
    }

    @Override
    public void onDeviceDisconnectedSuccessfully() {
        closeProgressDialog();

        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        if (connectedDevicesListDialog != null) {
            if (!connectedDevicesListDialog.isShowing())
                connectedDevicesListDialog.show();

            mConnectedDevicesAdapter.setConnectedDevices(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            this.mDeviceViewModel.getConnectedDevices(this);
        } else
            handleConnectedDevices();
    }
    //endregion BLE CallBacks

    //region Declare BLE Methods
    private void handleLockBleConnection() {
        if (isConnectedToBleDevice)
            this.mDeviceViewModel.disconnect();
        else
            connectToDevice();
    }

    private void handleConnectedDevices() {
        if (isConnectedToBleDevice)
            handleDialogListOfConnectedClientsToDevice();
        else if (connectedDevicesListDialog != null) {
            connectedDevicesListDialog.dismiss();
            connectedDevicesListDialog = null;
        }
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
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
            Handler mHandler = new Handler();
            mBluetoothLEHelper.scanLeDevice(true);

            mHandler.postDelayed(() -> {
                for (int i = 0; i < TIMES_TO_SCAN_BLE_DEVICES; i++)
                    if (connectToSpecificBleDevice(getListOfScannedDevices()))
                        break;
            }, mBluetoothLEHelper.getScanPeriod());
        }
    }

    private boolean connectToSpecificBleDevice(List<ScannedDeviceModel> listOfScannedDevices) {
        for (ScannedDeviceModel device : listOfScannedDevices) {
            if (device.getMacAddress().equals(mDevice.getBleDeviceMacAddress())) {
                this.mDeviceViewModel.connect(this, device);
                return true;
            }
        }

        return false;
    }

    private void sendLockCommand(boolean lockCommand) {
        this.mDeviceViewModel.sendLockCommand(this, lockCommand);
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
        if (connectedDevicesListDialog != null) {
            connectedDevicesListDialog.dismiss();
            connectedDevicesListDialog = null;
        }

        connectedDevicesListDialog = new Dialog(requireContext());
        connectedDevicesListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectedDevicesListDialog.setContentView(R.layout.dialog_connected_devices);

        if (mConnectedDevicesAdapter == null) {
            mConnectedDevicesAdapter = new ConnectedDevicesAdapter(this,
                    Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        }

        mConnectedDevicesAdapter.setConnectedDevices(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));

        RecyclerView rcvDialogConnectedClients = connectedDevicesListDialog.findViewById(R.id.rcv_dialog_connected_devices);
        Button btnCancelDialogConnectedClients = connectedDevicesListDialog.findViewById(R.id.btn_cancel_dialog_connected_devices);
        Button btnScanDialogConnectedClients = connectedDevicesListDialog.findViewById(R.id.btn_scan_dialog_connected_devices);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedDevicesAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
            mConnectedDevicesAdapter.setConnectedDevices(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            connectedDevicesListDialog.dismiss();
            connectedDevicesListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            mConnectedDevicesAdapter.setConnectedDevices(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mDeviceViewModel.getConnectedDevices(this);
        });

        connectedDevicesListDialog.setOnDismissListener(dialog -> {
            if (connectedDevicesListDialog != null) {
                connectedDevicesListDialog.dismiss();
                connectedDevicesListDialog = null;
            }
        });

        mConnectedDevicesAdapter.setConnectedDevices(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        mDeviceViewModel.getConnectedDevices(this);

        if (!connectedDevicesListDialog.isShowing()) {
            connectedDevicesListDialog.show();
            Objects.requireNonNull(connectedDevicesListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(connectedDevicesListDialog));
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
    //endregion Declare BLE Methods

    //region Declare Classes & Interfaces
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Declare Classes & Interfaces

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setBleMoreInfoImage(imgMoreInfoLockPage, setDefault);

        txvDeviceTypeLockPage.setText(mDevice.getDeviceType());

        setIsLockedImage(imgIsLockedLockPage,
                setDefault ? 2 : (mDevice.getIsLocked() ? 1 : 0));

        setBatteryStatusImage(setDefault, imgBatteryStatusLockPage, mDevice.getBatteryPercentage());
        setConnectedDevicesStatusImage(
                imgConnectedDevicesLockPage, setDefault, mDevice.getConnectedDevicesCount());

        imgManageMembersLockPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
        txvDeviceNameLockPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));
        txvDeviceNameLockPage.setText(mDevice.getBleDeviceName());

        txvBriefStatusLockPage.setText(
                setDefault ?
                        getString(R.string.fragment_text_view_data_not_synced) :
                        getLockBriefStatusText(mDevice.getIsLocked(), mDevice.getIsDoorClosed()));
        txvBriefStatusLockPage.setTextColor(
                setDefault ? getColor(mContext, R.color.md_grey_500) :
                        getColor(mContext, getLockBriefStatusColor(mDevice.getIsLocked(), mDevice.getIsDoorClosed())));

        txvNewUpdateLockPage.setText(null);

        closeProgressDialog();
    }

    private void handleDeviceMembers() {
        if (isUserLoggedIn())
            addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()),
                    R.id.frg_lock_activity, ManageMembersFragment.newInstance(mDevice));
        else
            Toast.makeText(getActivity(), "This is not available in Local Mode", Toast.LENGTH_LONG).show();
    }

    private void closeAllDialogs() {
        if (connectedDevicesListDialog != null) {
            connectedDevicesListDialog.dismiss();
            connectedDevicesListDialog = null;
        }

        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        if (!isUserLoggedIn())
            closeProgressDialog();
    }
    //endregion Declare Methods
}
