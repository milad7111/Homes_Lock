package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;
import com.projects.company.homes_lock.ui.login.fragment.register.IRegisterFragment;

public class RegisterViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private IRegisterFragment mIRegisterFragment;

    public RegisterViewModelFactory(Application mApplication, IRegisterFragment mIRegisterFragment) {
        this.mApplication = mApplication;
        this.mIRegisterFragment = mIRegisterFragment;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mIRegisterFragment);
    }
}