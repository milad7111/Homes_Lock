package com.projects.company.homes_lock.ui.device.fragment.gatewaypage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiStateModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.utils.ble.AvailableBleDevicesAdapter;
import com.projects.company.homes_lock.utils.ble.ConnectedClientsAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.WifiNetworksAdapter;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.support.v4.content.ContextCompat.getColor;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.WIFI_STATE_CONNECTED;
import static com.projects.company.homes_lock.utils.helper.BleHelper.WIFI_STATE_CONNECTING;
import static com.projects.company.homes_lock.utils.helper.BleHelper.WIFI_STATE_DISCONNECTED;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.BleHelper.isMyPhone;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getGatewayBriefStatusColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getGatewayBriefStatusText;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.getDialogLayoutParams;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setAvailableBleDevicesStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleMoreInfoImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setConnectedClientsStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setGatewayInternetConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setTypeface;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class GatewayPageFragment extends BaseFragment
        implements
        IBleScanListener,
        IGatewayPageFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private ImageView imgAvailableBleDevicesGatewayPage;
    private ImageView imgAvailableBleDevicesGatewayPageRing;
    private ImageView imgAvailableBleDevicesGatewayPageCenter;
    private ImageView imgConnectionStatusGatewayPage;
    private ImageView imgConnectedClientsGatewayPage;
    private ImageView imgBleGatewayPage;
    private ImageView imgManageMembersGatewayPage;
    private ImageView imgMoreInfoGatewayPage;

    private TextView txvDeviceNameGatewayPage;
    private TextView txvNewUpdateGatewayPage;
    private TextView txvBriefStatusGatewayPage;
    private TextView txvValidDataStatusGatewayPage;

    private RelativeLayout rllValidDataStatusGatewayPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean isConnectedToBleDevice;
    private boolean setConnectionStatusPermission = true;
    private boolean waitForWST = false;
    //endregion Declare Variables

    //region Declare Objects
    private Context mContext;
    private DeviceViewModel mDeviceViewModel;
    private Device mDevice;
    private WifiNetworksAdapter mWifiNetworksAdapter;
    private CustomBluetoothLEHelper mBluetoothLEHelper;
    private ConnectedClientsAdapter mConnectedClientsAdapter;
    private AvailableBleDevicesAdapter mAvailableBleDevicesAdapter;

    private Dialog deviceWifiNetworkListDialog;
    private Dialog deviceWifiNetworkDialog;
    private Dialog connectedClientsListDialog;
    private Dialog availableBleDevicesListDialog;
    private Dialog connectToServerDialog;
    private Dialog disconnectDeviceDialog;

    private ScheduledFuture freeBleBuffer;
    private ScheduledFuture<?> freeWifiPool;
    //endregion Declare Objects

    //region Constructor
    public GatewayPageFragment() {
    }

    public static GatewayPageFragment newInstance(Device device) {
        GatewayPageFragment fragment = new GatewayPageFragment();
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
        GatewayPageFragment.this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        GatewayPageFragment.this.mDeviceViewModel.isConnected().observe(this, isConnected -> {
            cancelSchedulerFreeWifiPool();

            if (isConnected != null) {
                isConnectedToBleDevice = isConnected;
                setBleConnectionStatusImage(imgBleGatewayPage, isConnected);

                if (!isUserLoggedIn() && !isConnected) {
                    updateViewData(true);
                    closeAllDialogs();

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
            }
        });
        GatewayPageFragment.this.mDeviceViewModel.isSupported().observe(this, isSupported -> {
            cancelSchedulerFreeWifiPool();

            if (isSupported != null)
                if (isSupported) {
                    GatewayPageFragment.this.mDeviceViewModel.getGatewaySpecifiedInfoFromBleDevice(this);
                    GatewayPageFragment.this.mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice(this);
                    updateViewData(false);
                } else if (!isUserLoggedIn()) {
                    setConnectionStatusPermission = true;
                    handleWifiProgressAnimation(false);

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
        });
        GatewayPageFragment.this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, device -> {
            mDevice = device;

            if (device == null) {
                mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
                    ((DeviceActivity) getActivity()).setViewPagerAdapter(
                            new CustomDeviceAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), devices));
                });
            } else
                updateViewData(!isUserLoggedIn() && !isConnectedToBleDevice);
        });
        GatewayPageFragment.this.mDeviceViewModel.getBleTimeOut().observe(this, this::handleBleBufferStatus);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gateway_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgAvailableBleDevicesGatewayPage = view.findViewById(R.id.img_available_ble_devices_gateway_page);
        imgAvailableBleDevicesGatewayPageRing = view.findViewById(R.id.img_available_ble_devices_gateway_page_ring);
        imgAvailableBleDevicesGatewayPageCenter = view.findViewById(R.id.img_available_ble_devices_gateway_page_center);
        imgConnectionStatusGatewayPage = view.findViewById(R.id.img_connection_status_gateway_page);
        imgConnectedClientsGatewayPage = view.findViewById(R.id.img_connected_devices_gateway_page);
        imgBleGatewayPage = view.findViewById(R.id.img_ble_gateway_page);
        imgManageMembersGatewayPage = view.findViewById(R.id.img_manage_members_gateway_page);
        imgMoreInfoGatewayPage = view.findViewById(R.id.img_more_info_gateway_page);

        txvDeviceNameGatewayPage = view.findViewById(R.id.txv_device_name_gateway_page);
        txvNewUpdateGatewayPage = view.findViewById(R.id.txv_new_update_gateway_page);
        txvBriefStatusGatewayPage = view.findViewById(R.id.txv_brief_status_gateway_page);
        txvValidDataStatusGatewayPage = view.findViewById(R.id.txv_valid_data_status_gateway_page);

        rllValidDataStatusGatewayPage = view.findViewById(R.id.rll_valid_data_status_gateway_page);
        //endregion Initialize Views

        //region Setup Views
        imgAvailableBleDevicesGatewayPage.setOnClickListener(this);
        imgConnectionStatusGatewayPage.setOnClickListener(this);
        imgConnectedClientsGatewayPage.setOnClickListener(this);
        imgBleGatewayPage.setOnClickListener(this);
        imgManageMembersGatewayPage.setOnClickListener(this);
        imgMoreInfoGatewayPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        closeProgressDialog();

        updateViewData(!isUserLoggedIn());

        if (isUserLoggedIn()) {
            this.mDeviceViewModel.setListenerForDevice(this, mDevice);
            this.mDeviceViewModel.initMQTT(getActivity(), mDevice.getSerialNumber());
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
            GatewayPageFragment.this.mDeviceViewModel.disconnectMQTT();
            GatewayPageFragment.this.mDeviceViewModel.removeListenerForDevice(this, mDevice);
        }

        disconnectDevice();

        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_available_ble_devices_gateway_page:
                if (isConnectedToBleDevice)
                    handleAvailableBleDevices();
                else if (isUserLoggedIn())
                    showToast(getConnectedServersMessage(mDevice.getConnectedServersCount()));
                break;
            case R.id.img_connection_status_gateway_page:
                handleGatewayInternetConnection();
                break;
            case R.id.img_connected_devices_gateway_page:
                handleConnectedClients();
                break;
            case R.id.img_ble_gateway_page:
                handleDeviceBleConnection();
                break;
            case R.id.img_manage_members_gateway_page:
                handleDeviceMembers();
                break;
            case R.id.img_more_info_gateway_page:
                if (isUserLoggedIn() || isConnectedToBleDevice)
                    addFragment(
                            (AppCompatActivity) Objects.requireNonNull(getActivity()),
                            R.id.frl_device_activity,
                            DeviceSettingFragment.newInstance(mDevice, "GTWY", GatewayPageFragment.this.mDeviceViewModel));
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
    public void onAdapterItemClick(BaseModel object) {
        if (object instanceof WifiNetworksModel) {
            deviceWifiNetworkListDialog.dismiss();
            deviceWifiNetworkListDialog = null;
            handleDialogSetDeviceWifiNetwork((WifiNetworksModel) object);
        } else if (object instanceof ConnectedDeviceModel)
            handleDialogDisconnectClientFromDevice((ConnectedDeviceModel) object);
        else if (object instanceof AvailableBleDeviceModel)
            handleDialogConnectionAvailableDevices((AvailableBleDeviceModel) object);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }

    @Override
    public void onFindNewNetworkAroundDevice(WifiNetworksModel mWifiNetworksModel) {
        getActivity().runOnUiThread(() -> {
            if (deviceWifiNetworkListDialog != null)
                deviceWifiNetworkListDialog.findViewById(R.id.prg_top_dialog_available_networks).setVisibility(INVISIBLE);
        });

        mWifiNetworksAdapter.addWifiNetwork(mWifiNetworksModel);
    }

    @Override
    public void onSetDeviceWifiNetworkSuccessful() {
        Timber.d("We're working on it.");
        closeProgressDialog();
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onCancelDeviceWifiNetworkSuccessful() {
        closeProgressDialog();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleCancelWifiStateInGateWay();
            }
        });
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        mDevice = response;
        updateViewData(false);
        GatewayPageFragment.this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onGetNewAvailableBleDevice(AvailableBleDeviceModel availableBleDeviceModel) {
        mAvailableBleDevicesAdapter.addAvailableBleDevice(availableBleDeviceModel);
    }

    @Override
    public void onWriteServerMacAddressForGateWaySuccessful(AvailableBleDeviceModel availableBleDeviceModel) {
        GatewayPageFragment.this.mDeviceViewModel.setServerPasswordForGateWay(this, availableBleDeviceModel);
    }

    @Override
    public void onWriteServerPasswordForGateWaySuccessful() {
        GatewayPageFragment.this.mDeviceViewModel.connectGateWayToServer(this);
    }

    @Override
    public void onConnectCommandSentToGateWaySuccessful() {
        if (availableBleDevicesListDialog != null) {
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        }

        if (connectToServerDialog != null) {
            connectToServerDialog.dismiss();
            connectToServerDialog = null;
        }

        if (disconnectDeviceDialog != null) {
            disconnectDeviceDialog.dismiss();
            disconnectDeviceDialog = null;
        }

        showToast("Command CONNECT sent successfully, wait ...");
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

        if (disconnectDeviceDialog != null) {
            disconnectDeviceDialog.dismiss();
            disconnectDeviceDialog = null;
        }

        if (connectedClientsListDialog != null) {
            if (!connectedClientsListDialog.isShowing())
                connectedClientsListDialog.show();

//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            GatewayPageFragment.this.mDeviceViewModel.getConnectedClients(this);
        } else
            handleConnectedClients();
    }

    @Override
    public void onConnectToServerSuccessful(boolean isrState, boolean isqState) {
        if (isrState && isqState)
            showToast("Connect to server successfully.");
    }

    @Override
    public void onWifiStatusChange(boolean iswStatus) {
        if (iswStatus) setConnectionStatusPermission = true;

        cancelSchedulerFreeWifiPool();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleWifiProgressAnimation(false);

                if (deviceWifiNetworkListDialog != null) {
                    deviceWifiNetworkListDialog.dismiss();
                    deviceWifiNetworkListDialog = null;
                    setConnectionStatusPermission = true;
                    updateViewData(false);
                }
            }
        });
    }

    @Override
    public void onGetAvailableBleDevicesEnd() {
        getActivity().runOnUiThread(() -> {
            if (availableBleDevicesListDialog != null)
                availableBleDevicesListDialog.findViewById(R.id.prg_top_dialog_available_ble_devices).setVisibility(INVISIBLE);
        });
    }

    @Override
    public void onGetConnectedDevicesEnd() {
        getActivity().runOnUiThread(() -> {
            if (connectedClientsListDialog != null)
                connectedClientsListDialog.findViewById(R.id.prg_top_dialog_connected_device).setVisibility(INVISIBLE);
        });
    }

    @Override
    public void onInternetStatusChange(boolean isiState) {
        if (isiState) setConnectionStatusPermission = true;

        cancelSchedulerFreeWifiPool();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleWifiProgressAnimation(false);

                if (deviceWifiNetworkListDialog != null) {
                    deviceWifiNetworkListDialog.dismiss();
                    deviceWifiNetworkListDialog = null;
                    setConnectionStatusPermission = true;
                    updateViewData(false);
                }
            }
        });
    }

    @Override
    public void onConnectToMqttServerSuccessful(boolean isrState, boolean isqState) {
        if (isrState && isqState) showToast("Connect to server successfully.");

        cancelSchedulerFreeWifiPool();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleWifiProgressAnimation(false);

                if (deviceWifiNetworkListDialog != null) {
                    deviceWifiNetworkListDialog.dismiss();
                    deviceWifiNetworkListDialog = null;
                    setConnectionStatusPermission = true;
                    updateViewData(false);
                }
            }
        });
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        showToast(rssi + "");
    }
    //endregion BLE CallBacks

    //region IGatewayPageFragment CallBacks
    @Override
    public void onReadWifiState(WifiStateModel wifiState) {
        if (waitForWST)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Timber.i("RecieveWST: %s", wifiState.getState());
                    setConnectionStatusPermission = false;
                    handleWifiProgressAnimation(true);

                    switch (wifiState.getState()) {
                        case WIFI_STATE_DISCONNECTED:
                            cancelSchedulerFreeWifiPool();
                            handleDialogListOfAvailableWifiNetworksAroundDevice(wifiState);
                            break;
                        case WIFI_STATE_CONNECTING:
                            handleDialogListOfAvailableWifiNetworksAroundDevice(wifiState);
                            break;
                        case WIFI_STATE_CONNECTED:
                            handleDialogListOfAvailableWifiNetworksAroundDevice(wifiState);
                            break;
                    }
                }
            });
        waitForWST = false;
    }
    //endregion IGatewayPageFragment CallBacks

    //region Declare BLE Methods
    private void handleDeviceBleConnection() {
        if (isUserLoggedIn())
            showToast("This is not available in Login Mode");
        else {
            if (isConnectedToBleDevice)
                disconnectDevice();
            else
                connectToDevice();
        }
    }

    private void handleConnectedClients() {
        if (isConnectedToBleDevice)
            handleDialogListOfConnectedClients();
        else if (isUserLoggedIn())
            showToast(getConnectedClientsMessage(mDevice.getConnectedClientsCount()));
        else if (connectedClientsListDialog != null) {
            connectedClientsListDialog.dismiss();
            connectedClientsListDialog = null;
        }
    }

    private void handleAvailableBleDevices() {
        if (isConnectedToBleDevice)
            handleDialogListOfAvailableBleDevices();
        else if (availableBleDevicesListDialog != null) {
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        }
    }

    private void handleDialogListOfConnectedClients() {
        if (connectedClientsListDialog != null) {
            connectedClientsListDialog.dismiss();
            connectedClientsListDialog = null;
        }

        connectedClientsListDialog = new Dialog(requireContext());
        connectedClientsListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectedClientsListDialog.setContentView(R.layout.dialog_connected_devices);
        connectedClientsListDialog.setCancelable(false);

        setTypeface((TextView) connectedClientsListDialog.findViewById(R.id.txv_title_dialog_connected_devices), "roboto_medium");

        if (mConnectedClientsAdapter == null) {
            mConnectedClientsAdapter = new ConnectedClientsAdapter(this, new ArrayList<>());
//                    Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        }

//        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());

        ProgressBar prgDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.prg_top_dialog_connected_device);
        RecyclerView rcvDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.rcv_dialog_connected_devices);
        Button btnCancelDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.btn_cancel_dialog_connected_devices);
        Button btnScanDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.btn_scan_dialog_connected_devices);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedClientsAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            connectedClientsListDialog.dismiss();
            connectedClientsListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            prgDialogConnectedClients.setVisibility(VISIBLE);
//            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
            GatewayPageFragment.this.mDeviceViewModel.getConnectedClients(this);
        });

        connectedClientsListDialog.setOnDismissListener(dialog -> {
            if (connectedClientsListDialog != null) {
                connectedClientsListDialog.dismiss();
                connectedClientsListDialog = null;
            }
        });

//        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        mConnectedClientsAdapter.setConnectedClients(new ArrayList<>());
        GatewayPageFragment.this.mDeviceViewModel.getConnectedClients(this);

        if (!connectedClientsListDialog.isShowing()) {
            connectedClientsListDialog.show();
            Objects.requireNonNull(connectedClientsListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(connectedClientsListDialog));
        }
    }

    private void handleDialogListOfAvailableBleDevices() {
        if (availableBleDevicesListDialog != null) {
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        }

        availableBleDevicesListDialog = new Dialog(requireContext());
        availableBleDevicesListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        availableBleDevicesListDialog.setContentView(R.layout.dialog_available_ble_devices);
        availableBleDevicesListDialog.setCancelable(false);

        setTypeface((TextView) availableBleDevicesListDialog.findViewById(R.id.txv_title_dialog_available_ble_devices), "roboto_medium");

        if (mAvailableBleDevicesAdapter == null)
            mAvailableBleDevicesAdapter = new AvailableBleDevicesAdapter(this, new ArrayList<>());

        ProgressBar prgTopDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.prg_top_dialog_available_ble_devices);
        RecyclerView rcvDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.rcv_dialog_available_ble_devices);
        Button btnCancelDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.btn_cancel_dialog_available_ble_devices);
        Button btnScanDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.btn_scan_dialog_available_ble_devices);

        prgTopDialogAvailableBleDevices.setVisibility(VISIBLE);
        mAvailableBleDevicesAdapter.setAvailableBleDevices(new ArrayList<>());

        rcvDialogAvailableBleDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogAvailableBleDevices.setItemAnimator(new DefaultItemAnimator());
        rcvDialogAvailableBleDevices.setAdapter(mAvailableBleDevicesAdapter);

        btnCancelDialogAvailableBleDevices.setOnClickListener(v -> {
            prgTopDialogAvailableBleDevices.setVisibility(VISIBLE);
//            mAvailableBleDevicesAdapter.setAvailableBleDevices(Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));
            mAvailableBleDevicesAdapter.setAvailableBleDevices(new ArrayList<>());
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        });

        btnScanDialogAvailableBleDevices.setOnClickListener(v -> {
            prgTopDialogAvailableBleDevices.setVisibility(VISIBLE);
            mAvailableBleDevicesAdapter.setAvailableBleDevices(new ArrayList<>());
            GatewayPageFragment.this.mDeviceViewModel.getAvailableBleDevices(this);
        });

        availableBleDevicesListDialog.setOnDismissListener(dialog -> {
            if (availableBleDevicesListDialog != null) {
                availableBleDevicesListDialog.dismiss();
                availableBleDevicesListDialog = null;
            }
        });

        GatewayPageFragment.this.mDeviceViewModel.getAvailableBleDevices(this);

        if (!availableBleDevicesListDialog.isShowing()) {
            availableBleDevicesListDialog.show();
            Objects.requireNonNull(availableBleDevicesListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(availableBleDevicesListDialog));
        }
    }

    private void handleDialogDisconnectClientFromDevice(ConnectedDeviceModel mConnectedDeviceModel) {
        if (disconnectDeviceDialog != null) {
            disconnectDeviceDialog.dismiss();
            disconnectDeviceDialog = null;
        }

        disconnectDeviceDialog = new Dialog(requireContext());
        disconnectDeviceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        disconnectDeviceDialog.setContentView(R.layout.dialog_disconnect_device);
        disconnectDeviceDialog.setCancelable(false);

        setTypeface((TextView) disconnectDeviceDialog.findViewById(R.id.txv_title_dialog_disconnect_device), "roboto_medium");

        Button btnCancelDialogDisconnectClient =
                disconnectDeviceDialog.findViewById(R.id.btn_cancel_dialog_disconnect_device);
        Button btnDisconnectDialogDisconnectClient =
                disconnectDeviceDialog.findViewById(R.id.btn_disconnect_dialog_disconnect_device);

        btnCancelDialogDisconnectClient.setOnClickListener(v -> {
            disconnectDeviceDialog.dismiss();
            disconnectDeviceDialog = null;
        });

        btnDisconnectDialogDisconnectClient.setOnClickListener(v -> {
            openProgressDialog(getContext(), null, "Disconnect device ...");
            GatewayPageFragment.this.mDeviceViewModel.disconnectFromBleDevice(this, mConnectedDeviceModel);
        });

        disconnectDeviceDialog.show();
        Objects.requireNonNull(disconnectDeviceDialog.getWindow()).setAttributes(getDialogLayoutParams(disconnectDeviceDialog));
    }

    private void handleDialogConnectionAvailableDevices(AvailableBleDeviceModel availableBleDeviceModel) {
        if (availableBleDeviceModel.getConnectionStatus()) {
            if (disconnectDeviceDialog != null) {
                disconnectDeviceDialog.dismiss();
                disconnectDeviceDialog = null;
            }

            disconnectDeviceDialog = new Dialog(requireContext());
            disconnectDeviceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            disconnectDeviceDialog.setContentView(R.layout.dialog_disconnect_device);
            disconnectDeviceDialog.setCancelable(false);

            setTypeface((TextView) disconnectDeviceDialog.findViewById(R.id.txv_title_dialog_disconnect_device), "roboto_medium");

            Button btnCancelDialogDisconnectClient =
                    disconnectDeviceDialog.findViewById(R.id.btn_cancel_dialog_disconnect_device);
            Button btnDisconnectDialogDisconnectClient =
                    disconnectDeviceDialog.findViewById(R.id.btn_disconnect_dialog_disconnect_device);

            btnCancelDialogDisconnectClient.setOnClickListener(v -> {
                disconnectDeviceDialog.dismiss();
                disconnectDeviceDialog = null;
            });

            btnDisconnectDialogDisconnectClient.setOnClickListener(v -> {
                openProgressDialog(getContext(), null, "Disconnect ...");
                GatewayPageFragment.this.mDeviceViewModel.disconnectGateWayFromServer(this);
            });

            disconnectDeviceDialog.show();
            Objects.requireNonNull(disconnectDeviceDialog.getWindow()).setAttributes(getDialogLayoutParams(disconnectDeviceDialog));
        } else {
            if (connectToServerDialog != null) {
                connectToServerDialog.dismiss();
                connectToServerDialog = null;
            }

            connectToServerDialog = new Dialog(requireContext());
            connectToServerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            connectToServerDialog.setContentView(R.layout.dialog_connect_device);
            connectToServerDialog.setCancelable(false);

            setTypeface((TextView) connectToServerDialog.findViewById(R.id.txv_title_dialog_connect_to_server), "roboto_medium");

            TextInputEditText tietSecurityCodeDialogConnectToServer =
                    connectToServerDialog.findViewById(R.id.tiet_security_code_dialog_connect_to_server);

            Button btnCancelDialogConnectToServer =
                    connectToServerDialog.findViewById(R.id.btn_cancel_dialog_connect_to_server);
            Button btnConnectDialogConnectToServer =
                    connectToServerDialog.findViewById(R.id.btn_connect_dialog_connect_to_server);

            if (availableBleDeviceModel.isSaved()) {
                tietSecurityCodeDialogConnectToServer.setEnabled(false);
                tietSecurityCodeDialogConnectToServer.setText("000000");
            } else {
                tietSecurityCodeDialogConnectToServer.setEnabled(true);
                tietSecurityCodeDialogConnectToServer.setText(null);
                tietSecurityCodeDialogConnectToServer.requestFocus();
            }

            btnCancelDialogConnectToServer.setOnClickListener(v -> {
                connectToServerDialog.dismiss();
                connectToServerDialog = null;
            });

            btnConnectDialogConnectToServer.setOnClickListener(v -> {
                openProgressDialog(getContext(), null, "Connect to server ...");

                if (availableBleDeviceModel.isSaved())
                    GatewayPageFragment.this.mDeviceViewModel.connectGateWayToServer(this);
                else {
                    availableBleDeviceModel.setPassword(Integer.valueOf(Objects.requireNonNull(tietSecurityCodeDialogConnectToServer.getText()).toString()));
                    GatewayPageFragment.this.mDeviceViewModel.setServerMacAddressForGateWay(this, availableBleDeviceModel);
                }
            });

            connectToServerDialog.show();
            Objects.requireNonNull(connectToServerDialog.getWindow()).setAttributes(getDialogLayoutParams(connectToServerDialog));
        }
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity(), null);
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(mDevice.getBleDeviceMacAddress());

        if (tempDevice != null) {
            openProgressDialog(getActivity(), null, "Pairing ...");
            GatewayPageFragment.this.mDeviceViewModel.connect(this, new ScannedDeviceModel(tempDevice));
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
        if (mDevice.getBleDeviceMacAddress() != null)
            for (ScannedDeviceModel device : listOfScannedDevices) {
                if (device.getMacAddress().toLowerCase().equals(mDevice.getBleDeviceMacAddress().toLowerCase())) {
                    GatewayPageFragment.this.mDeviceViewModel.connect(this, device);
                    return true;
                }
            }
        else
            Timber.e("mDevice.getBleDeviceMacAddress() is null");

        return false;
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

    private void handleDialogListOfAvailableWifiNetworksAroundDevice(WifiStateModel wifiState) {
        if (deviceWifiNetworkListDialog == null) {
            deviceWifiNetworkListDialog = new Dialog(requireContext());
            deviceWifiNetworkListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceWifiNetworkListDialog.setContentView(R.layout.dialog_available_networks);
            deviceWifiNetworkListDialog.setCancelable(false);

            setTypeface((TextView) deviceWifiNetworkListDialog.findViewById(R.id.txv_title_dialog_available_networks), "roboto_medium");
        } else if (!deviceWifiNetworkListDialog.isShowing())
            deviceWifiNetworkListDialog.show();

        mWifiNetworksAdapter = new WifiNetworksAdapter(this, new ArrayList<>());

        ProgressBar prgTopDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.prg_top_dialog_available_networks);
        RecyclerView rcvDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.rcv_dialog_available_networks);
        Button btnCancelDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.btn_cancel_dialog_available_networks);
        Button btnScanDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.btn_scan_dialog_available_networks);

        ConstraintLayout ctlCurrentWifiNetwork = deviceWifiNetworkListDialog.findViewById(R.id.ctl_current_wifi_network);
        TextView txvCurrentWifiSsidName = deviceWifiNetworkListDialog.findViewById(R.id.txv_current_wifi_ssid_name);
        TextView txvCurrentWifiState = deviceWifiNetworkListDialog.findViewById(R.id.txv_current_wifi_state);
        Button btnCurrentWifiStateChange = deviceWifiNetworkListDialog.findViewById(R.id.btn_current_wifi_state_change);

        rcvDialogAvailableNetworks.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogAvailableNetworks.setItemAnimator(new DefaultItemAnimator());
        rcvDialogAvailableNetworks.setAdapter(mWifiNetworksAdapter);

        btnCancelDialogAvailableNetworks.setOnClickListener(v -> {
            cancelSchedulerFreeWifiPool();
            prgTopDialogAvailableNetworks.setVisibility(VISIBLE);
            mWifiNetworksAdapter.setAvailableNetworks(new ArrayList<>());

            if (deviceWifiNetworkListDialog != null) {
                deviceWifiNetworkListDialog.dismiss();
                deviceWifiNetworkListDialog = null;
            }

            setConnectionStatusPermission = true;
            handleWifiProgressAnimation(false);
        });

        btnScanDialogAvailableNetworks.setOnClickListener(v -> {
            prgTopDialogAvailableNetworks.setVisibility(VISIBLE);
            mWifiNetworksAdapter.setAvailableNetworks(new ArrayList<>());
//            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
            GatewayPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this);
        });

        btnCurrentWifiStateChange.setOnClickListener(v -> {
            prgTopDialogAvailableNetworks.setVisibility(VISIBLE);
            mDeviceViewModel.disconnectGatewayWifiNetwork(this);
            txvCurrentWifiState.setText("DISCONNECTING");
            btnCurrentWifiStateChange.setEnabled(false);
            runSchedulersToReadWifiState();
        });

        switch (wifiState.getState()) {
            case WIFI_STATE_DISCONNECTED:
                ctlCurrentWifiNetwork.setVisibility(GONE);
                ctlCurrentWifiNetwork.invalidate();
                prgTopDialogAvailableNetworks.setVisibility(VISIBLE);
                btnScanDialogAvailableNetworks.setEnabled(true);
                GatewayPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this);
                break;
            case WIFI_STATE_CONNECTING:
                btnScanDialogAvailableNetworks.setEnabled(false);
                txvCurrentWifiSsidName.setText(wifiState.getSSID());
                btnCurrentWifiStateChange.setText("CANCEL");

                if (txvCurrentWifiState.getText() != "DISCONNECTING")
                    txvCurrentWifiState.setText("CONNECTING");

                ctlCurrentWifiNetwork.setVisibility(VISIBLE);
                ctlCurrentWifiNetwork.invalidate();
                break;
            case WIFI_STATE_CONNECTED:
                btnScanDialogAvailableNetworks.setEnabled(false);
                txvCurrentWifiSsidName.setText(wifiState.getSSID());
                btnCurrentWifiStateChange.setText("DISCONNECT");

                if (txvCurrentWifiState.getText() != "DISCONNECTING")
                    txvCurrentWifiState.setText("CONNECTED");

                ctlCurrentWifiNetwork.setVisibility(VISIBLE);
                ctlCurrentWifiNetwork.invalidate();
                break;
        }

        if (!deviceWifiNetworkListDialog.isShowing()) {
            deviceWifiNetworkListDialog.show();
            Objects.requireNonNull(deviceWifiNetworkListDialog.getWindow()).setAttributes(getDialogLayoutParams(deviceWifiNetworkListDialog));
        }
    }

    private void runSchedulersToReadWifiState() {
        cancelSchedulerFreeBleBuffer();

        // schedule a task to execute after timeout milliSeconds
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        freeWifiPool = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                waitForWST = true;
                mDeviceViewModel.readWifiState(GatewayPageFragment.this);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void cancelSchedulerFreeWifiPool() {
        if (freeWifiPool != null) freeWifiPool.cancel(true);
    }

    private void handleDialogSetDeviceWifiNetwork(WifiNetworksModel wifiNetwork) {
        if (deviceWifiNetworkDialog == null) {
            deviceWifiNetworkDialog = new Dialog(Objects.requireNonNull(getContext()));
            deviceWifiNetworkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceWifiNetworkDialog.setContentView(R.layout.dialog_device_wifi_network_connect);
            deviceWifiNetworkDialog.setCancelable(false);

            setTypeface((TextView) deviceWifiNetworkDialog.findViewById(R.id.txv_title_dialog_gateway_wifi_network_connect), "roboto_medium");

            TextView txvTitleDialogGatewayWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.txv_title_dialog_gateway_wifi_network_connect);

            txvTitleDialogGatewayWifiNetworkConnect.setText(
                    String.format(getString(R.string.dialog_title_gateway_wifi_network_connect), wifiNetwork.getSSID()));

            TextInputEditText tietWifiPasswordDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.tiet_wifi_password_dialog_gateway_wifi_network_connect);
            Spinner spnWifiTypeDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.spn_wifi_type_dialog_gateway_wifi_network_connect);

            Button btnCancelDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.btn_cancel_dialog_gateway_wifi_network_connect);
            Button btnConnectDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.btn_connect_dialog_gateway_wifi_network_connect);

            btnCancelDialogDeviceWifiNetworkConnect.setOnClickListener(v -> {
                deviceWifiNetworkDialog.dismiss();
                deviceWifiNetworkDialog = null;
            });

            btnConnectDialogDeviceWifiNetworkConnect.setOnClickListener(v -> {
                wifiNetwork.setPassword(Objects.requireNonNull(tietWifiPasswordDialogDeviceWifiNetworkConnect.getText()).toString());
                wifiNetwork.setAuthenticateType(spnWifiTypeDialogDeviceWifiNetworkConnect.getSelectedItemPosition());
                openProgressDialog(getContext(), null, "Set Internet Connection ...");
                GatewayPageFragment.this.mDeviceViewModel.setGatewayWifiNetwork(this, wifiNetwork);
            });

            deviceWifiNetworkDialog.setOnDismissListener(dialog -> {
                tietWifiPasswordDialogDeviceWifiNetworkConnect.setText(null);
                spnWifiTypeDialogDeviceWifiNetworkConnect.setSelection(0, false);
            });
        }

        if (!deviceWifiNetworkDialog.isShowing())
            deviceWifiNetworkDialog.show();

        Objects.requireNonNull(deviceWifiNetworkDialog.getWindow()).setAttributes(getDialogLayoutParams(deviceWifiNetworkDialog));
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
    //endregion Declare BLE Methods

    //region Declare Classes & Interfaces
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

    }
    //endregion Declare Classes & Interfaces

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        try {
            setBleMoreInfoImage(imgMoreInfoGatewayPage, (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault);
            setAvailableBleDevicesStatusImage(imgAvailableBleDevicesGatewayPage, imgAvailableBleDevicesGatewayPageRing,
                    imgAvailableBleDevicesGatewayPageCenter, mDevice.getConnectedServersCount(), (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault);

            if (setConnectionStatusPermission)
                setGatewayInternetConnectionStatusImage(
                        imgConnectionStatusGatewayPage, (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault, mDevice.getWifiStatus(), mDevice.getInternetStatus(), mDevice.getWifiStrength());

            setConnectedClientsStatusImage(
                    imgConnectedClientsGatewayPage, (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault, mDevice.getConnectedClientsCount());

            imgManageMembersGatewayPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
            txvDeviceNameGatewayPage.setTextColor((!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));
            txvDeviceNameGatewayPage.setText(mDevice.getBleDeviceName());

            txvBriefStatusGatewayPage.setText(
                    (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ?
                            getString(R.string.fragment_text_view_data_not_synced) :
                            getGatewayBriefStatusText(mDevice.getInternetStatus(), mDevice.getWifiStatus()));
            txvBriefStatusGatewayPage.setTextColor(
                    (!isConnectedToBleDevice && !isUserLoggedIn()) || setDefault ? getColor(mContext, R.color.md_grey_500) :
                            getColor(mContext, getGatewayBriefStatusColor(mDevice.getInternetStatus(), mDevice.getWifiStatus())));

            txvNewUpdateGatewayPage.setText(null);

            closeProgressDialog();

            if (isUserLoggedIn()) {
                if (!mDevice.getInternetStatus()) {
                    rllValidDataStatusGatewayPage.setVisibility(VISIBLE);
                    txvValidDataStatusGatewayPage.setText("Not connected to internet.\nLast update: " + new Date(mDevice.getUpdated()));
                } else {
                    rllValidDataStatusGatewayPage.setVisibility(GONE);
                    txvValidDataStatusGatewayPage.setText(null);
                }
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
        }
    }

    private void handleGatewayInternetConnection() {
        if (isUserLoggedIn())
            showToast("This is not available in Login Mode");
        else if (isConnectedToBleDevice) {
            setConnectionStatusPermission = false;
            handleWifiProgressAnimation(true);
            waitForWST = true;
            GatewayPageFragment.this.mDeviceViewModel.readWifiState(this);
        }
//            if (mDevice.getWifiStatus()) {
//                openProgressDialog(getContext(), null, "Disconnect Internet Connection ...");
//                GatewayPageFragment.this.mDeviceViewModel.disconnectGatewayWifiNetwork(this);
//            } else {
//                setConnectionStatusPermission = false;
//                handleWifiProgressAnimation(true);
//                handleDialogListOfAvailableWifiNetworksAroundDevice();
//            }
    }

    private void closeDialogHandleDeviceWifiNetwork() {
        if (deviceWifiNetworkDialog != null) {
            deviceWifiNetworkDialog.dismiss();
            deviceWifiNetworkDialog = null;
//            deviceWifiNetworkListDialog.dismiss();
//            deviceWifiNetworkListDialog = null;
        }
    }

    private void handleDeviceMembers() {
        if (isUserLoggedIn())
            if (mDevice.getMemberAdminStatus())
                addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frl_device_activity, ManageMembersFragment.newInstance(mDevice));
            else
                showToast("Access Denied");
        else
            showToast("This is not available in Local Mode");
    }

    private void closeAllDialogs() {
        if (deviceWifiNetworkListDialog != null) {
            deviceWifiNetworkListDialog.dismiss();
            deviceWifiNetworkListDialog = null;
        }

        if (deviceWifiNetworkDialog != null) {
            deviceWifiNetworkDialog.dismiss();
            deviceWifiNetworkDialog = null;
        }

        if (connectedClientsListDialog != null) {
            connectedClientsListDialog.dismiss();
            connectedClientsListDialog = null;
        }

        if (availableBleDevicesListDialog != null) {
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        }

        if (disconnectDeviceDialog != null) {
            disconnectDeviceDialog.dismiss();
            disconnectDeviceDialog = null;
        }

        if (!isUserLoggedIn())
            closeProgressDialog();
    }

    @SuppressLint("DefaultLocale")
    private String getConnectedClientsMessage(int connectedClientsCount) {
        switch (connectedClientsCount) {
            case 0:
                return "No device is connect to Gateway";
            case 1:
                return "One device is connected to Gateway";
            default:
                return String.format("%d devices are connected to Gateway", mDevice.getConnectedClientsCount());
        }
    }

    @SuppressLint("DefaultLocale")
    private String getConnectedServersMessage(int connectedServersCount) {
        switch (connectedServersCount) {
            case 0:
                return "Gateway isn't connect to any device";
            case 1:
                return "Gateway is connected to one device";
            default:
                return String.format("Gateway is connected to %d devices", connectedServersCount);
        }
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

    private void handleWifiProgressAnimation(Boolean status) {
        //imgConnectionStatusGatewayPage.setEnabled(!status);

        if (status) {
            imgConnectionStatusGatewayPage.setImageResource(R.drawable.wifi_progress);
            ((AnimationDrawable) imgConnectionStatusGatewayPage.getDrawable()).start();
        } else {
            if (imgConnectionStatusGatewayPage.getDrawable() instanceof AnimationDrawable)
                ((AnimationDrawable) imgConnectionStatusGatewayPage.getDrawable()).stop();
            updateViewData(false);
        }
    }

    private void handleCancelWifiStateInGateWay() {
        if (deviceWifiNetworkListDialog != null) {
            deviceWifiNetworkListDialog.findViewById(R.id.ctl_current_wifi_network).setVisibility(GONE);
            deviceWifiNetworkListDialog.findViewById(R.id.ctl_current_wifi_network).invalidate();
            deviceWifiNetworkListDialog.findViewById(R.id.prg_top_dialog_available_networks).setVisibility(GONE);
        }
    }
    //endregion Declare Methods
}