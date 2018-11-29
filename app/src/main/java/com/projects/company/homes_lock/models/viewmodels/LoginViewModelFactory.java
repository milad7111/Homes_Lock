package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application mApplication;
    private ILoginFragment mILoginFragment;

    public LoginViewModelFactory(Application mApplication, ILoginFragment mILoginFragment) {
        this.mApplication = mApplication;
        this.mILoginFragment = mILoginFragment;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mILoginFragment);
    }
}