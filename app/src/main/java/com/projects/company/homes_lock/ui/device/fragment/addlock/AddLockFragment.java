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

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.base.BaseModel;
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
import com.projects.company.homes_lock.utils.helper.DialogHelper;

import java.util.Collections;
import java.util.List;

import static com.projects.company.homes_lock.base.BaseApplication.isUserLoggedIn;
import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.getScanPermission;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleDialogAddLockOnline;

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
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(2, 8)),
                true);

        this.lockObjectId = lockObjectId;
        activeDialog = handleDialogAddLockOnline(this, true);
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(3, 8)),
                true);

        userLockObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(userLockObjectId, lockObjectId);
    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(4, 8)),
                    true);
            mDeviceViewModel.addUserLockToUser(userLockObjectId);
        }
    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean addUserLockToUserSuccessful) {
        if (addUserLockToUserSuccessful) {
            DialogHelper.handleProgressDialog(
                    getContext(),
                    null,
                    String.format("Adding Lock ... %d %%", getRandomPercentNumber(5, 8)),
                    true);

            if (activeDialog != null)
                activeDialog.dismiss();

            mUserViewModel.getUserWithObjectId(BaseApplication.activeUserObjectId);
        }
    }

    @Override
    public void onGetUserSuccessful(User response) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(6, 8)),
                true);

        BaseApplication.activeUserObjectId = response.getObjectId();
        mUserViewModel.insertUser(response);
    }

    @Override
    public void onDataInsert(Long id) {
        DialogHelper.handleProgressDialog(
                getContext(),
                null,
                String.format("Adding Lock ... %d %%", getRandomPercentNumber(7, 8)),
                true);

        if (id != -1)
            mDeviceViewModel.getAllLocalDevices().observe(this, new Observer<List<Device>>() {
                @Override
                public void onChanged(@Nullable final List<Device> devices) {
                    DialogHelper.handleProgressDialog(
                            getContext(),
                            null,
                            String.format("Adding Lock ... %d %%", getRandomPercentNumber(8, 8)),
                            true);

                    ((LockActivity) getActivity()).setViewPagerAdapter(new CustomDeviceAdapter(getActivity().getSupportFragmentManager(), devices));
                }
            });
    }
    //endregion IAddLockFragment CallBacks

    //region Ble Callbacks
    @Override
    public void onFindBleSuccess(List devices) {
        activeDialog = DialogHelper.handleDialogListOfAvailableBleDevices(this, devices);
    }

    @Override
    public void onFindBleFault() {
        activeDialog = DialogHelper.handleDialogListOfAvailableBleDevices(this, Collections.singletonList(new ScannedDeviceModel(SEARCHING_TIMEOUT_MODE)));
    }

    @Override
    public void onAdapterItemClick(BaseModel device) {
        mDevice = (ScannedDeviceModel) device;

        if (activeDialog != null)
            activeDialog.dismiss();

        this.mDeviceViewModel.connect(this, mDevice);
    }

    @Override
    public void onBonded(BluetoothDevice device) {
        activeDialog = DialogHelper.handleDialogAddLockOffline(this);
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private void addNewLock() {
        if (isUserLoggedIn())
            activeDialog = handleDialogAddLockOnline(this, false); // Means user Wrote username and password then clicked Login
        else {
            mBluetoothLEHelper = new CustomBluetoothLEHelper(getActivity());
            if (getScanPermission(this))
                activeDialog = DialogHelper.handleDialogListOfAvailableBleDevices(this, Collections.singletonList(new ScannedDeviceModel(SEARCHING_SCAN_MODE))); // Means user clicked Direct Connect
        }
    }
    //endregion Declare Methods
}
