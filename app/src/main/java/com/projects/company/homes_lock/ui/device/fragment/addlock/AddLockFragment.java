package com.projects.company.homes_lock.ui.device.fragment.addlock;


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
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.helper.DialogHelper;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class AddLockFragment extends BaseFragment implements IAddLockFragment, View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    Button btnAddNewLock;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private DeviceViewModel mDeviceViewModel;
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

    //region Ble Callbacks
    @Override
    public void onBleDeviceClick() {
    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {
    }

    @Override
    public void onBonded(BluetoothDevice device) {
    }
    //endregion Ble Callbacks

    //region Declare Methods
    private boolean getUserLoginMode() {
        return BaseApplication.userLoginMode;
    }

    private void addNewLock() {
        if (getUserLoginMode())
            DialogHelper.handleAddNewLockDialogOnline(getActivity()); // Means user Wrote username and password then clicked Login
        else
            (new DialogHelper()).handleAddNewLockDialogOffline(this); // Means user clicked Direct Connect
    }
    //endregion Declare Methods
}
