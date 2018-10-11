package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.projects.company.homes_lock.repositories.local.LocalRepository;

public class SplashViewModel extends AndroidViewModel {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LocalRepository mLocalRepository;
    //endregion Declare Objects

    public SplashViewModel(Application application) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLocalRepository = new LocalRepository(application);
        //endregion Initialize Objects
    }

    //region SharePreferences
    public boolean isFirstTimeLaunchApp() {
        return mLocalRepository.isFirstTimeLaunchApp();
    }
    //endregion SharePreferences
}