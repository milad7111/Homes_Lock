package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.utils.ble.IBleScanListener;

public class DeviceViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private IBleScanListener mIBleScanListener;

    public DeviceViewModelFactory(Application mApplication, IBleScanListener mIBleScanListener) {
        this.mApplication = mApplication;
        this.mIBleScanListener = mIBleScanListener;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new DeviceViewModel(mApplication, mIBleScanListener);
    }
}