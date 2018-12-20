package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.addlock.IAddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.IManageMembersFragment;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;
import com.projects.company.homes_lock.ui.login.fragment.register.IRegisterFragment;
import com.projects.company.homes_lock.utils.helper.ValidationHelper;

import java.util.List;

import static com.projects.company.homes_lock.utils.helper.DataHelper.isInstanceOfList;

public class UserViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        ILocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ILoginFragment mILoginFragment;
    private IRegisterFragment mIRegisterFragment;
    private IAddLockFragment mIAddLockFragment;
    private IManageMembersFragment mIManageMembersFragment;
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    //endregion Declare Objects

    public UserViewModel(Application application, ILoginFragment mILoginFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mILoginFragment = mILoginFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    public UserViewModel(Application application, IRegisterFragment mIRegisterFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIRegisterFragment = mIRegisterFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    public UserViewModel(Application application, IAddLockFragment mIAddLockFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIAddLockFragment = mIAddLockFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    public UserViewModel(Application application, IManageMembersFragment mIManageMembersFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIManageMembersFragment = mIManageMembersFragment;
        this.mLocalRepository = new LocalRepository(application);//TODO if not need delete this line
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    //region Declare Online Methods
    public void getUserWithObjectId(String userObjectId) {
        mNetworkRepository.getUserWithObjectId(this, userObjectId);
    }
    //endregion Declare Online Methods

    //region Declare Methods
    public void login(String email, String password) {
        mNetworkRepository.login(this, new LoginModel(email, password));
    }

    public void register(RegisterModel mRegisterModel, String mConfirmPassword) {
        if (ValidationHelper.validateEquality(mRegisterModel.getPassword(), mConfirmPassword))
            mNetworkRepository.register(this, mRegisterModel);
    }

    public void insertUser(User user) {
        mLocalRepository.insertUser(user, this);
    }

    public void getLockUsersByLockObjectId(String lockObjectId) {
        mNetworkRepository.getLockUsersByLockObjectId(this, lockObjectId);
    }
    //endregion Declare Methods

    //region Network Callbacks
    @Override
    public void onResponse(Object response) {
        if (isInstanceOfList(response, User.class.getName()))
            if (mIManageMembersFragment != null)
                mIManageMembersFragment.onGetUserLockData((List<User>) response);
        if (response instanceof User)
            if (mILoginFragment != null)
                mILoginFragment.onLoginSuccessful(response);
            else if (mIRegisterFragment != null)
                mIRegisterFragment.onRegisterSuccessful(response);
            else if (mIAddLockFragment != null)
                mIAddLockFragment.onGetUserSuccessful((User) response);
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        if (mILoginFragment != null)
            mILoginFragment.onLoginFailed(response);
        else if (mIRegisterFragment != null)
            mIRegisterFragment.onRegisterFailed(response);
    }

    @Override
    public void onListNetworkListenerFailure(FailureModel response) {
        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
    }
    //endregion Network Callbacks

    //region ILocalRepository Callbacks
    @Override
    public void onDataInsert(Long id) {
        Log.d(getClass().getName(), "Data inserted with Id : " + id);

        if (mILoginFragment != null)
            mILoginFragment.onDataInsert(id);
        else if (mIAddLockFragment != null)
            mIAddLockFragment.onDataInsert(id);
    }

    @Override
    public void onClearAllData() {
    }
    //endregion ILocalRepository Callbacks
}