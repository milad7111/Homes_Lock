package com.projects.company.homes_lock.ui.device.fragment.addlock;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.ViewHelper;

/**
 * This is Presenter for {@link AddLockFragment}
 */

public class AddLockFragmentPresenter implements AddLockFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private AddLockFragmentContract.mMvpView mAddLockFragmentView;
    private DeviceViewModel mDeviceViewModel;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    AddLockFragmentPresenter(AddLockFragmentContract.mMvpView _mAddLockFragmentView) {
        this.mAddLockFragmentView = _mAddLockFragmentView;
        this.mDeviceViewModel = ViewModelProviders.of((FragmentActivity) _mAddLockFragmentView).get(DeviceViewModel.class);
    }
}