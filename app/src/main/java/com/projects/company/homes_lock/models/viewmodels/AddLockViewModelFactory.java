package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.device.fragment.addlock.IAddLockFragment;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;

public class AddLockViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private IAddLockFragment mIAddLockFragment;

    public AddLockViewModelFactory(Application mApplication, IAddLockFragment mIAddLockFragment) {
        this.mApplication = mApplication;
        this.mIAddLockFragment = mIAddLockFragment;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mIAddLockFragment);
    }
}