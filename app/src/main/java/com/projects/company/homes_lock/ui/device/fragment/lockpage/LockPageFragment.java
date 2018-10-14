package com.projects.company.homes_lock.ui.device.fragment.lockpage;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModelFactory;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_UUID_SERVICE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_UUID_SERVICE_CHARACTERISTIC_LOCK_STATUS;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockPageFragment extends Fragment
        implements ILockPageFragment, IBleScanListener, View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    ImageView imgMainLockPage;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
    //endregion Declare Objects

    public LockPageFragment() {
        // Required empty public constructor
    }

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mDeviceViewModel = ViewModelProviders.of(
                LockPageFragment.this,
                new DeviceViewModelFactory(getActivity().getApplication(), LockPageFragment.this))
                .get(DeviceViewModel.class);
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
        imgMainLockPage = view.findViewById(R.id.img_main_lock_page);
        //endregion Initialize Views

        //region Setup Views
        imgMainLockPage.setOnClickListener(this);

//        mDeviceViewModel.getADevice("fsafasfasfasf");
        this.mDeviceViewModel.getADevice("fsafasfasfasf").observe(this, new Observer<Device>() {
            @Override
            public void onChanged(@Nullable final Device device) {
                if (imgMainLockPage != null)
                    ViewHelper.setLockStatusImage(imgMainLockPage, device.getLockStatus());
            }
        });

        mDeviceViewModel.setNotifyForCharacteristic(
                ((LockActivity) getActivity()).getBluetoothGatt(),
                LOCK_UUID_SERVICE,
                LOCK_UUID_SERVICE_CHARACTERISTIC_LOCK_STATUS,
                true,
                "fsafasfasfasf");

        mDeviceViewModel.readCharacteristic(
                ((LockActivity) getActivity()).getBluetoothGatt(),
                LOCK_UUID_SERVICE,
                LOCK_UUID_SERVICE_CHARACTERISTIC_LOCK_STATUS);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_main_lock_page:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //endregion Main CallBacks

    //region BLE CallBacks
    @Override
    public void onDataReceived(Object response) {
    }

    @Override
    public void onDataSent() {
    }

    @Override
    public void onFindBleCompleted(List response) {
    }

    @Override
    public void onFindBleFault(Object response) {
    }

    @Override
    public void setReceiver(BroadcastReceiver mBroadcastReceiver) {
    }

    @Override
    public void onClickBleDevice(ScannedDeviceModel mScannedDeviceModel) {
    }

    @Override
    public void setBluetoothGatt(BluetoothGatt mBluetoothGatt) {
    }
    //endregion BLE CallBacks

    //region Declare Methods
    //endregion Declare Methods
}
