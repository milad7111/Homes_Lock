package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothGattCharacteristic;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModelFactory;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.List;
import java.util.UUID;

import static com.projects.company.homes_lock.utils.helper.BleHelper.CHARACTERISTIC_UUID_TX;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPageFragment extends Fragment
        implements ILockPageFragment, IBleScanListener, View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    ImageView imgLockStatusLockPage;
    ImageView imgBatteryStatusLockPage;
    ImageView imgConnectionStatusLockPage;
    ImageView imgBleLockPage;

    TextView txvLockNameLockPage;
    TextView txvNewUpdateLockPage;
    TextView txvSecurityAlarmLockPage;
    TextView txvTemperatureLockPage;
    TextView txvHumidityLockPage;
    //endregion Declare Views

    //region Declare Variables
    private boolean bleDeviceConnectionStatus;
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    private Device mDevice;
    //endregion Declare Objects

    public LockPageFragment() {
        // Required empty public constructor
    }

    public static LockPageFragment newInstance(Device device) {
        LockPageFragment fragment = new LockPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, new Gson().toJson(device));
        fragment.setArguments(args);
        return fragment;
    }

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mDeviceViewModel = ViewModelProviders.of(
                this,
                new DeviceViewModelFactory(getActivity().getApplication(), this))
                .get(DeviceViewModel.class);

        mDevice = getArguments() != null ?
                (Device) DataHelper.convertJsonToObject(getArguments().getString(ARG_PARAM), Device.class.getName()) : null;
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lock_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        imgLockStatusLockPage = view.findViewById(R.id.img_lock_status_lock_page);
        imgBatteryStatusLockPage = view.findViewById(R.id.img_battery_status_lock_page);
        imgConnectionStatusLockPage = view.findViewById(R.id.img_connection_status_lock_page);
        imgBleLockPage = view.findViewById(R.id.img_ble_lock_page);

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
        //endregion Setup Views

        //region init
        this.mDeviceViewModel.isConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isConnected) {
                ViewHelper.setBleConnectionStatusImage(imgBleLockPage, isConnected);

                bleDeviceConnectionStatus = isConnected;

                if (isConnected)
                    initBleInfo();
            }
        });

        updateDataInView();
        getDeviceInfo();
        //endregion init
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_lock_status_lock_page:
                break;
            case R.id.img_connection_status_lock_page:
                Log.e("------>  ", mDeviceViewModel.isConnected().getValue() + "");
                mDeviceViewModel.readCharacteristic(BleHelper.CHARACTERISTIC_UUID_TX);
                break;
            case R.id.img_ble_lock_page:
                if (!bleDeviceConnectionStatus)
                    mDeviceViewModel.findBleDeviceWithMacAddressAndConnect(getActivity(), getContext(), mDevice.getBleDeviceMacAddress());
                else
                    mDeviceViewModel.disconnect();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    //endregion Main CallBacks

    //region BLE CallBacks
    @Override
    public void onDataReceived(Object response) {
    }

    @Override
    public void onDataSent(Object value) {
    }

    @Override
    public void onFindBleCompleted(List response) {
    }

    @Override
    public void onFindBleFault(Object response) {
    }

    @Override
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
    }
    //endregion BLE CallBacks

    //region Declare Methods
    private void updateDataInView() {
        ViewHelper.setLockStatusImage(imgLockStatusLockPage, mDevice.getLockStatus());
        ViewHelper.setBatteryStatusImage(imgBatteryStatusLockPage, mDevice.getBatteryStatus());
        ViewHelper.setConnectionStatusImage(imgConnectionStatusLockPage, mDevice.getWifiStatus(), mDevice.getInternetStatus(), mDevice.getWifiStrength());

        txvLockNameLockPage.setText(mDevice.getBleDeviceName());
        txvSecurityAlarmLockPage.setText(DataHelper.getSecurityAlarmText(mDevice.getLockStatus(), mDevice.getDoorStatus()));
        txvSecurityAlarmLockPage.setTextColor(ContextCompat.getColor(getActivity(), DataHelper.getSecurityAlarmColor(mDevice.getLockStatus(), mDevice.getDoorStatus())));

        txvTemperatureLockPage.setText(mDevice.getTemperature().toString());
        txvHumidityLockPage.setText(mDevice.getHumidity().toString());

        txvNewUpdateLockPage.setText(null);
    }

    public void getDeviceInfo() {
        this.mDeviceViewModel.getDeviceInfo(mDevice.getObjectId()).observe(this, new Observer<Device>() {
            @Override
            public void onChanged(@Nullable Device device) {
                mDevice = device;
                updateDataInView();
            }
        });
    }
    //endregion Declare Methods

    //region Declare BLE Methods


    private void initBleInfo() {
        this.mDeviceViewModel.isDeviceReady().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isDeviceReady) {
                if (isDeviceReady) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);

                                //Enable Notification On TX Characteristic
                                mDeviceViewModel.changeNotifyForCharacteristic(BleHelper.CHARACTERISTIC_UUID_TX, true);

                                //Read LockStatus and DoorStatus and ConnectionStatus
                                mDeviceViewModel.writeCharacteristic(BleHelper.CHARACTERISTIC_UUID_RX,
                                        BleHelper.createCommand(new byte[]{0x01}, new byte[]{}));
                                mDeviceViewModel.readCharacteristic(BleHelper.CHARACTERISTIC_UUID_TX);

                                //Read Temperature and Humidity
                                mDeviceViewModel.writeCharacteristic(BleHelper.CHARACTERISTIC_UUID_RX,
                                        BleHelper.createCommand(new byte[]{0x05}, new byte[]{}));
                                mDeviceViewModel.readCharacteristic(BleHelper.CHARACTERISTIC_UUID_TX);

                            } catch (Exception e) {
                            }
                        }
                    }.start();
                }
            }
        });
    }
    //endregion Declare BLE Methods
}
