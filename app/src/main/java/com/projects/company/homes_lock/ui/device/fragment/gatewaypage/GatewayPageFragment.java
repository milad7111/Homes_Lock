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
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedClientsModel;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.SettingFragment;
import com.projects.company.homes_lock.utils.ble.ConnectedClientsAdapter;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.WifiNetworksAdapter;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
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
import static com.projects.company.homes_lock.utils.helper.DataHelper.getGatewayBriefStatusColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getGatewayBriefStatusText;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.getDialogLayoutParams;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBleMoreInfoImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setConnectedDevicesStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setFragment;
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
    private ImageView imgInternetStatusGatewayPage;
    private ImageView imgConnectionStatusGatewayPage;
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

    private Dialog deviceWifiNetworkListDialog;
    private Dialog deviceWifiNetworkDialog;
    private Dialog connectedClientsToDeviceListDialog;
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
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mDeviceViewModel.isConnected().observe(this, isConnected -> {
            if (isConnected != null) {
                isConnectedToBleDevice = isConnected;
                setBleConnectionStatusImage(imgBleGatewayPage, isConnected);

                if (!isConnected) {
                    updateViewData(true);

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
        return inflater.inflate(R.layout.fragment_gateway_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgInternetStatusGatewayPage = view.findViewById(R.id.img_connected_devices_gateway_page);
        imgConnectionStatusGatewayPage = view.findViewById(R.id.img_connection_status_gateway_page);
        imgBleGatewayPage = view.findViewById(R.id.img_ble_gateway_page);
        imgManageMembersGatewayPage = view.findViewById(R.id.img_manage_members_gateway_page);
        imgMoreInfoGatewayPage = view.findViewById(R.id.img_more_info_gateway_page);

        txvDeviceNameGatewayPage = view.findViewById(R.id.txv_device_name_gateway_page);
        txvNewUpdateGatewayPage = view.findViewById(R.id.txv_new_update_gateway_page);
        txvBriefStatusGatewayPage = view.findViewById(R.id.txv_brief_status_gateway_page);
        //endregion Initialize Views

        //region Setup Views
        imgInternetStatusGatewayPage.setOnClickListener(this);
        imgConnectionStatusGatewayPage.setOnClickListener(this);
        imgBleGatewayPage.setOnClickListener(this);
        imgManageMembersGatewayPage.setOnClickListener(this);
        imgMoreInfoGatewayPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        handleProgressDialog(null, null, null, false);

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

        mDeviceViewModel.disconnect();
        if (mBluetoothLEHelper != null)
            mBluetoothLEHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_connected_devices_gateway_page:
                if (isConnectedToBleDevice)
                    handleConnectGatewayToAroundDevices();
                break;
            case R.id.img_connection_status_gateway_page:
                if (isConnectedToBleDevice)
                    handleGatewayInternetConnection();
                break;
            case R.id.img_ble_gateway_page:
                handleDeviceBleConnection();
                break;
            case R.id.img_manage_members_gateway_page:
                handleDeviceMembers();
                break;
            case R.id.img_more_info_gateway_page:
                if (isUserLoggedIn() || isConnectedToBleDevice)
                    setFragment(
                            (AppCompatActivity) Objects.requireNonNull(getActivity()),
                            R.id.frg_lock_activity,
                            SettingFragment.newInstance(mDevice.getObjectId(), mDeviceViewModel));
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
    public void onAdapterItemClick(BaseModel network) {
        WifiNetworksModel tempNetwork = (WifiNetworksModel) network;
        handleDialogSetDeviceWifiNetwork(tempNetwork);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }

    @Override
    public void onGetAvailableWifiNetworksCountAroundDevice(int count) {
        wifiNetworksCount = count;
        mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this, 0);
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
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onSetDeviceWifiNetworkFailed() {
        DialogHelper.handleProgressDialog(null, null, null, false);
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        mDevice = response;
        updateViewData(false);
        this.mDeviceViewModel.updateDevice(response);
    }

    @Override
    public void onSendRequestGetAvailableWifiSuccessful() {
        Log.d(getTag(), "Get Available Wifi Networks Around Device Sent Successful! Wait ...");
    }
    //endregion BLE CallBacks

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setBleMoreInfoImage(imgMoreInfoGatewayPage, setDefault);
        setConnectedDevicesStatusImage(imgInternetStatusGatewayPage, mDevice.getConnectedDevicesCount(), setDefault);

        setGatewayInternetConnectionStatusImage(
                imgConnectionStatusGatewayPage, setDefault, mDevice.getWifiStatus(), mDevice.getInternetStatus(), mDevice.getWifiStrength());

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

        handleProgressDialog(null, null, null, false);
    }

    private void handleGatewayInternetConnection() {
        if (isUserLoggedIn())
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else if (isConnectedToBleDevice)
            if (mDevice.getWifiStatus()) {
                DialogHelper.handleProgressDialog(getContext(), null, "Disconnect Internet Connection ...", true);
                this.mDeviceViewModel.disconnectGatewayWifiNetwork(this);
            } else
                handleDialogListOfAvailableWifiNetworksAroundDevice();
    }

    private void addFoundNetworkToList(WifiNetworksModel wifiNetworksModel) {
        if (!findNetworkInList(wifiNetworksModel.getSSID()))
            this.mWifiNetworkList.add(wifiNetworksModel);
    }

    private boolean findNetworkInList(String mSSID) {
        for (WifiNetworksModel network : mWifiNetworkList)
            if (network.getSSID().equals(mSSID))
                return true;

        return false;
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
            setFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_lock_activity, ManageMembersFragment.newInstance(mDevice));
        else
            Toast.makeText(getActivity(), "This is not available in Local Mode", Toast.LENGTH_LONG).show();
    }
    //endregion Declare Methods

    //region Declare BLE Methods
    private void handleDeviceBleConnection() {
        if (isUserLoggedIn())//TODO check if device connection status is false, user can connect direct
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else {
            if (isConnectedToBleDevice)
                this.mDeviceViewModel.disconnect();
            else
                connectToDevice();
        }
    }

    private void handleConnectedDevices() {
        if (isConnectedToBleDevice)
            handleDialogListOfConnectedClientsToDevice();
        else if (connectedClientsToDeviceListDialog != null) {
            connectedClientsToDeviceListDialog.dismiss();
            connectedClientsToDeviceListDialog = null;
        }
    }

    private void handleDialogListOfConnectedClientsToDevice() {
        if (connectedClientsToDeviceListDialog != null) {
            connectedClientsToDeviceListDialog.dismiss();
            connectedClientsToDeviceListDialog = null;
        }

        connectedClientsToDeviceListDialog = new Dialog(requireContext());
        connectedClientsToDeviceListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectedClientsToDeviceListDialog.setContentView(R.layout.dialog_connected_clients);

        if (mConnectedClientsAdapter == null) {
            mConnectedClientsAdapter = new ConnectedClientsAdapter(this,
                    Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
        }

        mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));

        RecyclerView rcvDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.rcv_dialog_connected_clients);
        Button btnCancelDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.btn_cancel_dialog_connected_clients);
        Button btnScanDialogConnectedClients = connectedClientsToDeviceListDialog.findViewById(R.id.btn_scan_dialog_connected_clients);

        rcvDialogConnectedClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvDialogConnectedClients.setItemAnimator(new DefaultItemAnimator());
        rcvDialogConnectedClients.setAdapter(mConnectedClientsAdapter);

        btnCancelDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
            connectedClientsToDeviceListDialog.dismiss();
            connectedClientsToDeviceListDialog = null;
        });

        btnScanDialogConnectedClients.setOnClickListener(v -> {
            mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
//            mDeviceViewModel.getConnectedClientsToDevice(this);//TODO
        });

        connectedClientsToDeviceListDialog.setOnDismissListener(dialog -> {
            if (connectedClientsToDeviceListDialog != null) {
                connectedClientsToDeviceListDialog.dismiss();
                connectedClientsToDeviceListDialog = null;
            }
        });

        mConnectedClientsAdapter.setConnectedDevices(Collections.singletonList(new ConnectedClientsModel(SEARCHING_SCAN_MODE)));
//        mDeviceViewModel.getConnectedClientsToDevice(this);//TODO

        if (!connectedClientsToDeviceListDialog.isShowing()) {
            connectedClientsToDeviceListDialog.show();
            Objects.requireNonNull(connectedClientsToDeviceListDialog.getWindow())
                    .setAttributes(getDialogLayoutParams(connectedClientsToDeviceListDialog));
        }
    }

    private void handleConnectGatewayToAroundDevices() {
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(mDevice.getBleDeviceMacAddress());

        if (tempDevice != null) {
            handleProgressDialog(getActivity(), null, "Pairing ...", true);
            this.mDeviceViewModel.connect(this, new ScannedDeviceModel(tempDevice));
        } else if (getScanPermission(this))
            scanDevices();
    }

    private void scanDevices() {
        handleProgressDialog(getActivity(), null, "Pairing ...", true);

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

            if (mWifiNetworksAdapter == null)
                mWifiNetworksAdapter = new WifiNetworksAdapter(this, Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));

            Spinner spnWifiTypeDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.spn_wifi_type_dialog_device_wifi_network_connect);
            TextInputEditText tietWifiPasswordDialogDeviceWifiNetworkConnect =
                    deviceWifiNetworkDialog.findViewById(R.id.tiet_wifi_password_dialog_device_wifi_network_connect);

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
                handleProgressDialog(getContext(), null, "Set Internet Connection ...", true);
                GatewayPageFragment.this.mDeviceViewModel.setDeviceWifiNetwork(this, wifiNetwork);
            });

            GatewayPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksAroundDevice(this);
        } else {
            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
            mWifiNetworksAdapter.setAvailableNetworks(mWifiNetworkList);
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
    //endregion Declare Methods
}
