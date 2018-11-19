package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;
import com.projects.company.homes_lock.utils.helper.DataHelper;

public class LoginViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ILoginFragment mILoginFragment;
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    //endregion Declare Objects

    public LoginViewModel(Application application, ILoginFragment mILoginFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mILoginFragment = mILoginFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    //region Declare Methods
    public void login(String email, String password) {
        mNetworkRepository.login(this, new LoginModel(email, password));
    }

    private static void saveLoginDataLocal() {
    }
    //endregion Declare Methods

    //region Login Callbacks
    @Override
    public void onResponse(Object response) {
        if (DataHelper.isInstanceOfList(response, User.class.getName())) {
            User tempUser = (User) response;
//            mLocalRepository.insertUser(tempUser.);
            mILoginFragment.onLoginSuccessful(response);
        }
    }

    @Override
    public void onFailure(Object response) {
        mILoginFragment.onLoginFailed(response);
    }
    //endregion Login Callbacks

    //region SharePreferences
    //endregion SharePreferences
}