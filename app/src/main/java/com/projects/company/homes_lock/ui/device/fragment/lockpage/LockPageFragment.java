package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseModel;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.setting.SettingFragment;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.ble.WifiNetworksAdapter;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v4.content.ContextCompat.getColor;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getSecurityAlarmColor;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getSecurityAlarmText;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setBatteryStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setConnectionStatusImage;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setFragment;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.setLockStatusImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPageFragment extends Fragment
        implements
        IBleScanListener,
        ILockPageFragment,
        View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    ImageView imgLockStatusLockPage;
    ImageView imgBatteryStatusLockPage;
    ImageView imgConnectionStatusLockPage;
    ImageView imgBleLockPage;
    ImageView imgManageMembersLockPage;
    ImageView imgMoreInfoLockPage;

    ImageView imgTemperatureCelsiusLockPage;
    ImageView imgWaterPercentLockPage;

    TextView txvLockNameLockPage;
    TextView txvNewUpdateLockPage;
    TextView txvSecurityAlarmLockPage;
    TextView txvTemperatureLockPage;
    TextView txvHumidityLockPage;
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
    private static Device mDevice;
    private WifiNetworksAdapter mWifiNetworksAdapter;
    private static Dialog deviceWifiNetworkListDialog;
    private static Dialog deviceWifiNetworkDialog;
    //endregion Declare Objects

    //region Constructor
    public LockPageFragment() {
    }

    public static LockPageFragment newInstance(Device device) {
        LockPageFragment fragment = new LockPageFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM, new Gson().toJson(device));
        fragment.setArguments(args);

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

        mDevice = getArguments() != null ?
                (Device) convertJsonToObject(getArguments().getString(ARG_PARAM), Device.class.getName())
                : null;
        //endregion Initialize Objects

        readAllLockInfo();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgLockStatusLockPage = view.findViewById(R.id.img_lock_status_lock_page);
        imgBatteryStatusLockPage = view.findViewById(R.id.img_battery_status_lock_page);
        imgConnectionStatusLockPage = view.findViewById(R.id.img_connection_status_lock_page);
        imgBleLockPage = view.findViewById(R.id.img_ble_lock_page);
        imgManageMembersLockPage = view.findViewById(R.id.img_manage_members_lock_page);
        imgMoreInfoLockPage = view.findViewById(R.id.img_more_info_lock_page);

        imgTemperatureCelsiusLockPage = view.findViewById(R.id.img_temperature_celsius_lock_page);
        imgWaterPercentLockPage = view.findViewById(R.id.img_water_percent_lock_page);

        txvLockNameLockPage = view.findViewById(R.id.txv_lock_name_lock_page);
        txvNewUpdateLockPage = view.findViewById(R.id.txv_new_update_lock_page);
        txvSecurityAlarmLockPage = view.findViewById(R.id.txv_security_alarm_lock_page);
        txvTemperatureLockPage = view.findViewById(R.id.txv_temperature_lock_page);
        txvHumidityLockPage = view.findViewById(R.id.txv_humidity_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgLockStatusLockPage.setOnClickListener(this);
        imgConnectionStatusLockPage.setOnClickListener(this);
        imgBleLockPage.setOnClickListener(this);
        imgManageMembersLockPage.setOnClickListener(this);
        imgMoreInfoLockPage.setOnClickListener(this);
        //endregion Setup Views

        //region init
        ViewHelper.setContext(getContext());
        handleProgressDialog(null, null, null, false);

        updateViewData(!isUserLoggedIn());

        if (isUserLoggedIn())
            this.mDeviceViewModel.setListenerForDevice(this, mDevice);

        this.mDeviceViewModel.initMQTT(getActivity());
        //endregion init
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_lock_status_lock_page:
                sendLockCommand(!mDevice.getLockStatus());
                break;
            case R.id.img_connection_status_lock_page:
                handleLockInternetConnection();
                break;
            case R.id.img_ble_lock_page:
                handleLockBleConnection();
                break;
            case R.id.img_manage_members_lock_page:
                handleLockMembers();
                break;
            case R.id.img_more_info_lock_page:
                setFragment(
                        (AppCompatActivity) getActivity(),
                        R.id.frg_lock_activity,
                        SettingFragment.newInstance(mDevice));
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
    }

    @Override
    public void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel) {
        addFoundNetworkToList(wifiNetworksModel);

        if (mWifiNetworkList.size() == wifiNetworksCount)
            handleDialogListOfAvailableWifiNetworksAroundDevice();
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
        closeDialogHandleDeviceWifiNetwork();
    }

    @Override
    public void onGetUpdatedDevice(Device response) {
        mDevice = response;
        updateViewData(false);
        this.mDeviceViewModel.updateDevice(response);
    }
    //endregion BLE CallBacks

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setLockStatusImage(imgLockStatusLockPage,
                setDefault ? 2 : (mDevice.getLockStatus() ? 1 : 0));

        setBatteryStatusImage(setDefault, imgBatteryStatusLockPage, mDevice.getBatteryPercentage());
        setConnectionStatusImage(
                imgConnectionStatusLockPage, setDefault, mDevice.getWifiStatus(), mDevice.getInternetStatus(), mDevice.getWifiStrength());

        imgManageMembersLockPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
        txvLockNameLockPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));
        txvLockNameLockPage.setText(mDevice.getBleDeviceName());

        txvSecurityAlarmLockPage.setText(
                setDefault ?
                        "Data Not Synced" :
                        getSecurityAlarmText(mDevice.getLockStatus(), mDevice.getDoorStatus()));
        txvSecurityAlarmLockPage.setTextColor(
                setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, getSecurityAlarmColor(mDevice.getLockStatus(), mDevice.getDoorStatus())));

        imgTemperatureCelsiusLockPage.setImageResource(
                setDefault ? R.drawable.ic_invalid_temperature_celsius : R.drawable.ic_valid_temperature_celsius);
        txvTemperatureLockPage.setText(mDevice.getTemperature().toString());
        txvTemperatureLockPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));

        imgWaterPercentLockPage.setImageResource(
                setDefault ? R.drawable.ic_invalid_water_percent : R.drawable.ic_valid_water_percent);
        txvHumidityLockPage.setText(mDevice.getHumidity().toString());
        txvHumidityLockPage.setTextColor(setDefault ? getColor(mContext, R.color.md_grey_500) : getColor(mContext, R.color.md_white_1000));

        txvNewUpdateLockPage.setText(null);

        handleProgressDialog(null, null, null, false);
    }

    public void getDeviceInfo() {
        this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, new Observer<Device>() {
            @Override
            public void onChanged(@Nullable Device device) {
                mDevice = device;
                updateViewData(false);
            }
        });
    }

    private void handleLockInternetConnection() {
        if (isUserLoggedIn())
            Toast.makeText(getActivity(), "This is not available in Login Mode", Toast.LENGTH_LONG).show();
        else if (isConnectedToBleDevice)
            if (mDevice.getWifiStatus())
                this.mDeviceViewModel.disconnectDeviceWifiNetwork();
            else
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
        deviceWifiNetworkDialog.dismiss();
        deviceWifiNetworkDialog = null;
        deviceWifiNetworkListDialog.dismiss();
        deviceWifiNetworkListDialog = null;
    }

    private void handleLockMembers() {
        if (isUserLoggedIn())
            setFragment((AppCompatActivity) getActivity(), R.id.frg_lock_activity, ManageMembersFragment.newInstance(mDevice));
        else
            Toast.makeText(getActivity(), "This is not available in Local Mode", Toast.LENGTH_LONG).show();
    }

    private void readAllLockInfo() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mDevice = LockPageFragment.this.mDeviceViewModel.getUserLockInfo(mDevice.getObjectId());
            }
        }.start();
    }
    //endregion Declare Methods

    //region Declare BLE Methods
    private void handleLockBleConnection() {
        if (isConnectedToBleDevice)
            this.mDeviceViewModel.disconnect();
        else
            connectToDevice();
    }

    private void connectToDevice() {
        mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());

        BluetoothDevice tempDevice = mBluetoothLEHelper.checkBondedDevices(mDevice.getBleDeviceMacAddress());

        if (tempDevice != null) {
            handleProgressDialog(getActivity(), null, "Pairing ...", true);

            this.mDeviceViewModel.connect(this, new ScannedDeviceModel(tempDevice));
            this.mDeviceViewModel.isConnected().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isConnected) {
                    isConnectedToBleDevice = isConnected;
                    ViewHelper.setBleConnectionStatusImage(imgBleLockPage, isConnected);
                    initBleInfo();
                }
            });
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
                this.mDeviceViewModel.isConnected().observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean isConnected) {
                        isConnectedToBleDevice = isConnected;
                        ViewHelper.setBleConnectionStatusImage(imgBleLockPage, isConnected);
                        initBleInfo();
                    }
                });
                return true;
            }
        }

        return false;
    }

    private void sendLockCommand(boolean lockCommand) {
        this.mDeviceViewModel.sendLockCommand(lockCommand);
    }

    private void initBleInfo() {
        this.mDeviceViewModel.isSupported().observe(this, new Observer<Boolean>() {
            @Nullable
            private Boolean isSupported;

            @Override
            public void onChanged(@Nullable Boolean isSupported) {
                if (isSupported != null)
                    if (isSupported)
                        getDeviceInfo();
                    else
                        updateViewData(true);
            }
        });
    }

    private List<ScannedDeviceModel> getListOfScannedDevices() {
        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        for (BluetoothLE device : mBluetoothLEHelper.getListDevices())
            mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        return mScannedDeviceModelList;
    }

    public static Device getDevice() {
        return mDevice;
    }

    private void handleDialogListOfAvailableWifiNetworksAroundDevice() {
        if (deviceWifiNetworkListDialog == null) {
            deviceWifiNetworkListDialog = new Dialog(getActivity());
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

            btnCancelDialogAvailableNetworks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
                    deviceWifiNetworkListDialog.dismiss();
                    deviceWifiNetworkListDialog = null;
                }
            });

            btnScanDialogAvailableNetworks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
                    LockPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksCountAroundDevice(getParentFragment());
                }
            });

            LockPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksCountAroundDevice(this);
        } else {
            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
            mWifiNetworksAdapter.setAvailableNetworks(mWifiNetworkList);
        }

        if (!deviceWifiNetworkListDialog.isShowing())
            deviceWifiNetworkListDialog.show();

        deviceWifiNetworkListDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(deviceWifiNetworkListDialog));
    }

    private void handleDialogSetDeviceWifiNetwork(WifiNetworksModel wifiNetwork) {
        if (deviceWifiNetworkDialog == null) {
            deviceWifiNetworkDialog = new Dialog(getActivity());
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

            btnCancelDialogDeviceWifiNetworkConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceWifiNetworkDialog.dismiss();
                    deviceWifiNetworkDialog = null;
                }
            });

            btnConnectDialogDeviceWifiNetworkConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifiNetwork.setPassword(tietWifiPasswordDialogDeviceWifiNetworkConnect.getText().toString());
                    wifiNetwork.setAuthenticateType(spnWifiTypeDialogDeviceWifiNetworkConnect.getSelectedItemPosition());
                    LockPageFragment.this.mDeviceViewModel.setDeviceWifiNetwork(getParentFragment(), wifiNetwork);
                }
            });

            LockPageFragment.this.mDeviceViewModel.getAvailableWifiNetworksCountAroundDevice(this);
        } else {
            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
            mWifiNetworksAdapter.setAvailableNetworks(mWifiNetworkList);
        }

        if (!deviceWifiNetworkDialog.isShowing())
            deviceWifiNetworkDialog.show();

        deviceWifiNetworkDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(deviceWifiNetworkDialog));
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
