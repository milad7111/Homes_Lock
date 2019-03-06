package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.device.fragment.managemembers.IManageMembersFragment;

public class ManageMembersViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private IManageMembersFragment mIManageMembersFragment;

    public ManageMembersViewModelFactory(Application mApplication, IManageMembersFragment mIManageMembersFragment) {
        this.mApplication = mApplication;
        this.mIManageMembersFragment = mIManageMembersFragment;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mIManageMembersFragment);
    }
}