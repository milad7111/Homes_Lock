package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.ManageMembersFragment;
import com.projects.company.homes_lock.ui.device.fragment.upgrade.MoreInfoFragment;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.support.v4.content.ContextCompat.getColor;
import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.BleHelper.FINDING_BLE_DEVICES_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.TIMES_TO_SCAN_BLE_DEVICES;
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
    boolean isConnectedToBleDevice;
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    private static Device mDevice;
    private Dialog activeDialog;
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
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);

        mDevice = getArguments() != null ?
                (Device) DataHelper.convertJsonToObject(getArguments().getString(ARG_PARAM), Device.class.getName())
                : null;
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
        txvLockNameLockPage.setText(mDevice.getBleDeviceName());
        imgManageMembersLockPage.setImageResource(isUserLoggedIn() ? R.drawable.ic_manage_members_enable : R.drawable.ic_manage_members_disable);
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
                changeConnectionState();
                break;
            case R.id.img_manage_members_lock_page:
                setFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_lock_activity, new ManageMembersFragment());
                break;
            case R.id.img_more_info_lock_page:
                setFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_lock_activity, new MoreInfoFragment());
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
    public void onBleDeviceClick(ScannedDeviceModel device) {
    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }
    //endregion BLE CallBacks

    //region Declare Methods
    private void updateViewData(boolean setDefault) {
        setLockStatusImage(imgLockStatusLockPage,
                setDefault ? 2 : (mDevice.getLockStatus() ? 1 : 0));
        setBatteryStatusImage(imgBatteryStatusLockPage,
                setDefault ? 0 : mDevice.getBatteryPercentage());
        setConnectionStatusImage(imgConnectionStatusLockPage,
                setDefault ? 0 : (mDevice.getWifiStatus() ? 2 : 1), mDevice.getInternetStatus(), mDevice.getWifiStrength());

        if (setDefault)
            imgTemperatureCelsiusLockPage.setImageDrawable(null);
        else
            imgTemperatureCelsiusLockPage.setImageResource(R.drawable.ic_temperature_celsius);

        imgWaterPercentLockPage.setImageDrawable(setDefault ? null : getResources().getDrawable(R.drawable.water_percent));

        txvLockNameLockPage.setText(mDevice.getBleDeviceName());

        txvSecurityAlarmLockPage.setText(
                setDefault ?
                        "Data Not Synced" :
                        getSecurityAlarmText(mDevice.getLockStatus(), mDevice.getDoorStatus()));
        txvSecurityAlarmLockPage.setTextColor(
                setDefault ?
                        getColor(getActivity(), R.color.md_grey_500) :
                        getColor(getActivity(), getSecurityAlarmColor(mDevice.getLockStatus(), mDevice.getDoorStatus())));

        txvTemperatureLockPage.setText(
                setDefault ? "" : mDevice.getTemperature().toString());
        txvHumidityLockPage.setText(
                setDefault ? "" : mDevice.getHumidity().toString());

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
        else
            activeDialog = DialogHelper.handleDialogListOfAvailableBleDevices(this, Collections.singletonList(new ScannedDeviceModel(FINDING_BLE_DEVICES_SCAN_MODE)));
    }
    //endregion Declare Methods

    //region Declare BLE Methods
    private void changeConnectionState() {
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
        } else if (BleHelper.getScanPermission(this))
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
        mDeviceViewModel.sendLockCommand(lockCommand);
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
    //endregion Declare BLE Methods
}
