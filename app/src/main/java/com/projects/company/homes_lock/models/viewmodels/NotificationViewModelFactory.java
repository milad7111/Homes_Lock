package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.device.fragment.adddevice.IAddDeviceFragment;

public class NotificationViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;

    public NotificationViewModelFactory(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NotificationViewModel(mApplication);
    }
}