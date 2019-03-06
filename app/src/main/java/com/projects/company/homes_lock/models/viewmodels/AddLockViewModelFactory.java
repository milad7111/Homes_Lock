package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.device.fragment.adddevice.IAddDeviceFragment;

public class AddLockViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private IAddDeviceFragment mIAddDeviceFragment;

    public AddLockViewModelFactory(Application mApplication, IAddDeviceFragment mIAddDeviceFragment) {
        this.mApplication = mApplication;
        this.mIAddDeviceFragment = mIAddDeviceFragment;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mIAddDeviceFragment);
    }
}