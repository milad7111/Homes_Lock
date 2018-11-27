package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;

public class LoginViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        ILocalRepository {

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

    public void insertUser(User user) {
        mLocalRepository.insertUser(user, this);
    }
    //endregion Declare Methods

    //region Login Callbacks
    @Override
    public void onResponse(Object response) {
        if (response instanceof User)
            mILoginFragment.onLoginSuccessful(response);
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        mILoginFragment.onLoginFailed(response);
    }
    //endregion Login Callbacks

    //region LocalRepository Callbacks
    @Override
    public void onDataInsert(Long id) {
        Log.d(getClass().getName(), "Data inserted with objectId : " + id);
        mILoginFragment.onDataInsert(id);
    }
    //endregion LocalRepository Callbacks
}