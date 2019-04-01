package com.projects.company.homes_lock.ui.device.fragment.gatewaypage;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.devicesetting.DeviceSettingFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.utils.ble.AvailableBleDevicesAdapter;
import com.projects.company.homes_lock.utils.ble.ConnectedClientsAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.WifiNetworksAdapter;
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
    private ImageView imgConnectionStatusGatewayPage;
    private ImageView imgConnectedClientsGatewayPage;
    private ImageView imgBleGatewayPage;
    private ImageView imgManageMembersGatewayPage;
    private ImageView imgMoreInfoGatewayPage;

    private TextView txvDeviceNameGatewayPage;
    private TextView txvNewUpdateGatewayPage;
    private TextView txvBriefStatusGatewayPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean isConnectedToBleDevice;
    private int wifiNetworksCount = 0;
    //endregion Declare Variables

    //region Declare Arrays & Lists
    private List<WifiNetworksModel> mWifiNetworkList = new ArrayList<>();
    //endregion Declare Arrays & Lists

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
    private Dialog disconnectClientDialog;
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
            if (isSupported != null)
                if (isSupported) {
                    GatewayPageFragment.this.mDeviceViewModel.getDeviceCommonSettingInfoFromBleDevice();
                    GatewayPageFragment.this.mDeviceViewModel.getGatewaySpecifiedInfoFromBleDevice();
                    updateViewData(false);
                } else if (!isUserLoggedIn()) {
                    updateViewData(true);

                    if (mBluetoothLEHelper != null)
                        mBluetoothLEHelper.disconnect();
                }
        });
        GatewayPageFragment.this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, device -> {
            mDevice = device;
            updateViewData(!isUserLoggedIn() && !isConnectedToBleDevice);
        });
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
        imgConnectionStatusGatewayPage = view.findViewById(R.id.img_connection_status_gateway_page);
        imgConnectedClientsGatewayPage = view.findViewById(R.id.img_connected_devices_gateway_page);
        imgBleGatewayPage = view.findViewById(R.id.img_ble_gateway_page);
        imgManageMembersGatewayPage = view.findViewById(R.id.img_manage_members_gateway_page);
        imgMoreInfoGatewayPage = view.findViewById(R.id.img_more_info_gateway_page);

        txvDeviceNameGatewayPage = view.findViewById(R.id.txv_device_name_gateway_page);
        txvNewUpdateGatewayPage = view.findViewById(R.id.txv_new_update_gateway_page);
        txvBriefStatusGatewayPage = view.findViewById(R.id.txv_brief_status_gateway_page);
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

//        if (isUserLoggedIn()) {
//            this.mDeviceViewModel.setListenerForDevice(this, mDevice);
//            this.mDeviceViewModel.initMQTT(getActivity(), mDevice.getObjectId());
//        }
        //endregion init
    }

    @Override
    public void onPause() {
        super.onPause();

//        if (BaseApplication.isUserLoggedIn())
//            this.mDeviceViewModel.disconnectMQTT();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        GatewayPageFragment.this.mDeviceViewModel.disconnect();
        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_available_ble_devices_gateway_page:
                if (isConnectedToBleDevice)
                    handleAvailableBleDevices();
                break;
            case R.id.img_connection_status_gateway_page:
                if (isConnectedToBleDevice)
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
                            R.id.frg_lock_activity,
                            DeviceSettingFragment.newInstance(mDevice, "GATEWAY", GatewayPageFragment.this.mDeviceViewModel));
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
        if (object instanceof WifiNetworksModel)
            handleDialogSetDeviceWifiNetwork((WifiNetworksModel) object);
        else if (object instanceof ConnectedDeviceModel)
            handleDialogDisconnectClientFromDevice((ConnectedDeviceModel) object);
        else if (object instanceof AvailableBleDeviceModel)
            handleDialogConnectionAvailableDevices((AvailableBleDeviceModel) object);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }

    @Override
    public void onFindNewNetworkAroundDevice(WifiNetworksModel mWifiNetworksModel) {
        mWifiNetworksAdapter.addWifiNetwork(mWifiNetworksModel);
//        addFoundNetworkToList(wifiNetworksModel);
//
//        if (wifiNetworksModel.getIndex() != wifiNetworksCount - 1)
//            mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this, wifiNetworksModel.getIndex() + 1);
//
//        handleDialogListOfAvailableWifiNetworksAroundDevice();
    }

    @Override
    public void onSetDeviceWifiNetworkSSIDSuccessful() {
    }

    @Override
    public void onSetDeviceWifiNetworkSSIDFailed() {
    }

    @Override
    public void onSetDeviceWifiNetworkPasswordSuccessful() {
    }

    @Override
    public void onSetDeviceWifiNetworkPasswordFailed() {
    }

    @Override
    public void onSetDeviceWifiNetworkAuthenticationTypeSuccessful() {
    }

    @Override
    public void onSetDeviceWifiNetworkAuthenticationTypeFailed() {
    }

    @Override
    public void onSetDeviceWifiNetworkSuccessful() {
        Log.d("SetDeviceWifiSuccessful", "We're working on it.");
        closeProgressDialog();
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onSetDeviceWifiNetworkFailed() {
        closeProgressDialog();
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        mDevice = response;
        updateViewData(false);
        GatewayPageFragment.this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onSendRequestGetAvailableWifiSuccessful() {
        Log.d(getTag(), "Get Available Wifi Networks Around Device Sent Successful! Wait ...");
    }

    @Override
    public void onGetNewAvailableBleDevice(AvailableBleDeviceModel availableBleDeviceModel) {
        availableBleDeviceModel.setIndex(mAvailableBleDevicesAdapter.getItemCount());
        mAvailableBleDevicesAdapter.addAvailableBleDevice(availableBleDeviceModel);
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

        if (connectedClientsListDialog != null) {
            if (!connectedClientsListDialog.isShowing())
                connectedClientsListDialog.show();

            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            GatewayPageFragment.this.mDeviceViewModel.getConnectedClients(this);
        } else
            handleConnectedClients();
    }
    //endregion BLE CallBacks

    //region Declare BLE Methods
    private void handleDeviceBleConnection() {
        if (isUserLoggedIn())//TODO check if device connection status is false, user can connect direct
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else {
            if (isConnectedToBleDevice)
                GatewayPageFragment.this.mDeviceViewModel.disconnect();
            else
                connectToDevice();
        }
    }

    private void handleConnectedClients() {
        if (isConnectedToBleDevice)
            handleDialogListOfConnectedClients();
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

        if (mConnectedClientsAdapter == null) {
            mConnectedClientsAdapter = new ConnectedClientsAdapter(this,
                    Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
        }

        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));

        RecyclerView rcvDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.rcv_dialog_connected_devices);
        Button btnCancelDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.btn_cancel_dialog_connected_devices);
        Button btnScanDialogConnectedClients = connectedClientsListDialog.findViewById(R.id.btn_scan_dialog_connected_devices);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedClientsAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            connectedClientsListDialog.dismiss();
            connectedClientsListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
            GatewayPageFragment.this.mDeviceViewModel.getConnectedClients(this);
        });

        connectedClientsListDialog.setOnDismissListener(dialog -> {
            if (connectedClientsListDialog != null) {
                connectedClientsListDialog.dismiss();
                connectedClientsListDialog = null;
            }
        });

        mConnectedClientsAdapter.setConnectedClients(Collections.singletonList(new ConnectedDeviceModel(SEARCHING_SCAN_MODE)));
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

        if (mAvailableBleDevicesAdapter == null) {
            mAvailableBleDevicesAdapter = new AvailableBleDevicesAdapter(this,
                    Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));
        }

        mAvailableBleDevicesAdapter.setAvailableBleDevices(Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));

        RecyclerView rcvDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.rcv_dialog_available_ble_devices);
        Button btnCancelDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.btn_cancel_dialog_available_ble_devices);
        Button btnScanDialogAvailableBleDevices = availableBleDevicesListDialog.findViewById(R.id.btn_scan_dialog_available_ble_devices);

        rcvDialogAvailableBleDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogAvailableBleDevices.setItemAnimator(new DefaultItemAnimator());
        rcvDialogAvailableBleDevices.setAdapter(mAvailableBleDevicesAdapter);

        btnCancelDialogAvailableBleDevices.setOnClickListener(v -> {
            mAvailableBleDevicesAdapter.setAvailableBleDevices(Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));
            availableBleDevicesListDialog.dismiss();
            availableBleDevicesListDialog = null;
        });

        btnScanDialogAvailableBleDevices.setOnClickListener(v -> {
            mAvailableBleDevicesAdapter.setAvailableBleDevices(Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));
            GatewayPageFragment.this.mDeviceViewModel.getAvailableBleDevices(this);
        });

        availableBleDevicesListDialog.setOnDismissListener(dialog -> {
            if (availableBleDevicesListDialog != null) {
                availableBleDevicesListDialog.dismiss();
                availableBleDevicesListDialog = null;
            }
        });

        mAvailableBleDevicesAdapter.setAvailableBleDevices(Collections.singletonList(new AvailableBleDeviceModel(SEARCHING_SCAN_MODE)));
        GatewayPageFragment.this.mDeviceViewModel.getAvailableBleDevices(this);

        if (!availableBleDevicesListDialog.isShowing()) {
            availableBleDevicesListDialog.show();
            Objects.requireNonNull(availableBleDevicesListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(availableBleDevicesListDialog));
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
            GatewayPageFragment.this.mDeviceViewModel.disconnectFromBleDevice(this, mConnectedDeviceModel);
        });

        disconnectClientDialog.show();
        Objects.requireNonNull(disconnectClientDialog.getWindow()).setAttributes(getDialogLayoutParams(disconnectClientDialog));
    }

    private void handleDialogConnectionAvailableDevices(AvailableBleDeviceModel availableBleDeviceModel) {
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

        if (!availableBleDeviceModel.getConnectionStatus())
            btnDisconnectDialogDisconnectClient.setText(String.format("%s ...", getString(R.string.dialog_button_connect)));

        btnCancelDialogDisconnectClient.setOnClickListener(v -> {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        });

        btnDisconnectDialogDisconnectClient.setOnClickListener(v -> {
            openProgressDialog(getContext(), null, btnDisconnectDialogDisconnectClient.getText().toString());
            GatewayPageFragment.this.mDeviceViewModel.handleConnectionAvailableDevices(this, availableBleDeviceModel);
        });

        disconnectClientDialog.show();
        Objects.requireNonNull(disconnectClientDialog.getWindow()).setAttributes(getDialogLayoutParams(disconnectClientDialog));
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
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
        for (ScannedDeviceModel device : listOfScannedDevices) {
            if (device.getMacAddress().equals(mDevice.getBleDeviceMacAddress())) {
                GatewayPageFragment.this.mDeviceViewModel.connect(this, device);
                return true;
            }
        }

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

    private void handleDialogListOfAvailableWifiNetworksAroundDevice() {
        if (deviceWifiNetworkListDialog == null) {
            deviceWifiNetworkListDialog = new Dialog(requireContext());
            deviceWifiNetworkListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceWifiNetworkListDialog.setContentView(R.layout.dialog_available_networks);

            if (mWifiNetworksAdapter == null)
                mWifiNetworksAdapter = new WifiNetworksAdapter(this, Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));

            RecyclerView rcvDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.rcv_dialog_available_networks);
            Button btnCancelDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.btn_cancel_dialog_available_networks);
            Button btnScanDialogAvailableNetworks = deviceWifiNetworkListDialog.findViewById(R.id.btn_scan_dialog_available_networks);

            rcvDialogAvailableNetworks.setLayoutManager(new LinearLayoutManager(getContext()));
            rcvDialogAvailableNetworks.setItemAnimator(new DefaultItemAnimator());
            rcvDialogAvailableNetworks.setAdapter(mWifiNetworksAdapter);

            btnCancelDialogAvailableNetworks.setOnClickListener(v -> {
                mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
                deviceWifiNetworkListDialog.dismiss();
                deviceWifiNetworkListDialog = null;
            });

            btnScanDialogAvailableNetworks.setOnClickListener(v -> {
                mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
                GatewayPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this);
            });

            GatewayPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this);
        } else {
            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
            mWifiNetworksAdapter.setAvailableNetworks(mWifiNetworkList);
        }

        if (!deviceWifiNetworkListDialog.isShowing()) {
            deviceWifiNetworkListDialog.show();
            Objects.requireNonNull(deviceWifiNetworkListDialog.getWindow()).setAttributes(getDialogLayoutParams(deviceWifiNetworkListDialog));
        }
    }

    private void handleDialogSetDeviceWifiNetwork(WifiNetworksModel wifiNetwork) {
        if (deviceWifiNetworkDialog == null) {
            deviceWifiNetworkDialog = new Dialog(Objects.requireNonNull(getContext()));
            deviceWifiNetworkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceWifiNetworkDialog.setContentView(R.layout.dialog_device_wifi_network_connect);

            TextInputEditText tietWifiPasswordDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.tiet_wifi_password_dialog_device_wifi_network_connect);
            Spinner spnWifiTypeDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.spn_wifi_type_dialog_device_wifi_network_connect);

            Button btnCancelDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.btn_cancel_dialog_device_wifi_network_connect);
            Button btnConnectDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.btn_connect_dialog_device_wifi_network_connect);

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
    //endregion Declare BLE Methods

    //region Declare Classes & Interfaces
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Declare Classes & Interfaces

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setBleMoreInfoImage(imgMoreInfoGatewayPage, setDefault);
        setAvailableBleDevicesStatusImage(imgAvailableBleDevicesGatewayPage, mDevice.getConnectedServersCount(), setDefault);

        setGatewayInternetConnectionStatusImage(
                imgConnectionStatusGatewayPage, setDefault, mDevice.getWifiStatus(), mDevice.getInternetStatus(), mDevice.getWifiStrength());
        setConnectedClientsStatusImage(
                imgConnectedClientsGatewayPage, !isConnectedToBleDevice && setDefault, mDevice.getConnectedClientsCount());

        imgManageMembersGatewayPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
        txvDeviceNameGatewayPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));
        txvDeviceNameGatewayPage.setText(mDevice.getBleDeviceName());

        txvBriefStatusGatewayPage.setText(
                setDefault ?
                        getString(R.string.fragment_text_view_data_not_synced) :
                        getGatewayBriefStatusText(mDevice.getInternetStatus(), mDevice.getWifiStatus()));
        txvBriefStatusGatewayPage.setTextColor(
                setDefault ? getColor(mContext, R.color.md_grey_500) :
                        getColor(mContext, getGatewayBriefStatusColor(mDevice.getInternetStatus(), mDevice.getWifiStatus())));

        txvNewUpdateGatewayPage.setText(null);

        closeProgressDialog();
    }

    private void handleGatewayInternetConnection() {
        if (isUserLoggedIn())
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else if (isConnectedToBleDevice)
            if (mDevice.getWifiStatus()) {
                openProgressDialog(getContext(), null, "Disconnect Internet Connection ...");
                GatewayPageFragment.this.mDeviceViewModel.disconnectGatewayWifiNetwork(this);
            } else
                handleDialogListOfAvailableWifiNetworksAroundDevice();
    }

    private void closeDialogHandleDeviceWifiNetwork() {
        if (deviceWifiNetworkDialog != null) {
            deviceWifiNetworkDialog.dismiss();
            deviceWifiNetworkDialog = null;
            deviceWifiNetworkListDialog.dismiss();
            deviceWifiNetworkListDialog = null;
        }
    }

    private void handleDeviceMembers() {
        if (isUserLoggedIn())
            addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_lock_activity, ManageMembersFragment.newInstance(mDevice));
        else
            Toast.makeText(getActivity(), "This is not available in Local Mode", Toast.LENGTH_LONG).show();
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

        if (disconnectClientDialog != null) {
            disconnectClientDialog.dismiss();
            disconnectClientDialog = null;
        }

        if (!isUserLoggedIn())
            closeProgressDialog();
    }
    //endregion Declare Methods
}
