package com.projects.company.homes_lock.ui.device.fragment.addlock;


import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.viewmodels.AddLockViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;
import com.projects.company.homes_lock.utils.helper.BleHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;

import java.util.Collections;
import java.util.List;

import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.BleHelper.FINDING_BLE_DEVICES_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.FINDING_BLE_DEVICES_TIMEOUT_MODE;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class AddLockFragment extends BaseFragment
        implements
        IBleScanListener,
        IAddLockFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    Button btnAddNewLock;
    //endregion Declare Views

    //region Declare Variables
    public static String lockObjectId;
    public static String userLockObjectId;
    //endregion Declare Variables

    //region Declare Objects
    public static DeviceViewModel mDeviceViewModel;
    private UserViewModel mUserViewModel;
    private Dialog activeDialog;
    public static ScannedDeviceModel mDevice;
    //endregion Declare Objects

    //region Constructor
    public AddLockFragment() {
    }

    public static AddLockFragment newInstance() {
        return new AddLockFragment();
    }
    //endregion Constructor

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new AddLockViewModelFactory(getActivity().getApplication(), this))
                .get(UserViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_lock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        btnAddNewLock = view.findViewById(R.id.btn_add_new_lock);
        //endregion Initialize Views

        //region Setup Views
        btnAddNewLock.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_lock:
                addNewLock();
                break;
        }
    }
    //endregion Main CallBacks

    //region IAddLockFragment CallBacks
    @Override
    public void onFindLockInOnlineDataBase(String lockObjectId) {
        DialogHelper.handleProgressDialogMessage(getContext(), "Adding Lock ...");
        this.lockObjectId = lockObjectId;
        activeDialog = DialogHelper.handleAddLockOnlineDialog(this, true);
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        DialogHelper.handleProgressDialogMessage(getContext(), "Pairing User & Lock ...");
        userLockObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(userLockObjectId, lockObjectId);
    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful)
            mDeviceViewModel.addUserLockToUser(userLockObjectId);
    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean addUserLockToUserSuccessful) {
        DialogHelper.handleProgressDialogMessage(getContext(), "Reading data ...");

        if (activeDialog != null)
            activeDialog.dismiss();

        if (addUserLockToUserSuccessful)
            Toast.makeText(getContext(), "Lock added Successfully", Toast.LENGTH_SHORT).show();

        mUserViewModel.getUserWithObjectId(BaseApplication.activeUserObjectId);
    }

    @Override
    public void onGetUserSuccessful(User response) {
        DialogHelper.handleProgressDialogMessage(getContext(), "Saving data ...");

        BaseApplication.activeUserObjectId = response.getObjectId();
        mUserViewModel.insertUser(response);
    }

    @Override
    public void onDataInsert(Long id) {
        DialogHelper.handleProgressDialogMessage(getContext(), "Displaying data ...");

        if (id != -1)
            mDeviceViewModel.getAllLocalDevices().observe(this, new Observer<List<Device>>() {
                @Override
                public void onChanged(@Nullable final List<Device> devices) {
                    ((LockActivity) getActivity()).setViewPagerAdapter(new CustomDeviceAdapter(getActivity().getSupportFragmentManager(), devices));
                }
            });
    }
    //endregion IAddLockFragment CallBacks

    //region Ble Callbacks
    @Override
    public void onFindBleSuccess(List devices) {
        activeDialog = DialogHelper.handleShowListOfAvailableBleDevicesDialog(this, devices);
    }

    @Override
    public void onFindBleFault() {
        activeDialog = DialogHelper.handleShowListOfAvailableBleDevicesDialog(this, Collections.singletonList(new ScannedDeviceModel(FINDING_BLE_DEVICES_TIMEOUT_MODE)));
    }

    @Override
    public void onBleDeviceClick(ScannedDeviceModel device) {
        mDevice = device;

        if (activeDialog != null)
            activeDialog.dismiss();

        this.mDeviceViewModel.connect(this, device);
    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {
    }

    @Override
    public void onBonded(BluetoothDevice device) {
        activeDialog = DialogHelper.handleAddLockOfflineDialog(this);
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private boolean getUserLoginMode() {
        return BaseApplication.userLoginMode;
    }

    private void addNewLock() {
        if (getUserLoginMode())
            activeDialog = DialogHelper.handleAddLockOnlineDialog(this, false); // Means user Wrote username and password then clicked Login
        else {
            mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
            if (BleHelper.getScanPermission(this))
                activeDialog = DialogHelper.handleShowListOfAvailableBleDevicesDialog(this, Collections.singletonList(new ScannedDeviceModel(FINDING_BLE_DEVICES_SCAN_MODE))); // Means user clicked Direct Connect
        }
    }
    //endregion Declare Methods
}
