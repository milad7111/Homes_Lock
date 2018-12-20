package com.projects.company.homes_lock.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.request.LoginModel;
import com.projects.company.homes_lock.models.datamodels.request.RegisterModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.repositories.local.ILocalRepository;
import com.projects.company.homes_lock.repositories.local.LocalRepository;
import com.projects.company.homes_lock.repositories.remote.NetworkListener;
import com.projects.company.homes_lock.repositories.remote.NetworkRepository;
import com.projects.company.homes_lock.ui.device.fragment.addlock.IAddLockFragment;
import com.projects.company.homes_lock.ui.device.fragment.managemembers.IManageMembersFragment;
import com.projects.company.homes_lock.ui.login.fragment.login.ILoginFragment;
import com.projects.company.homes_lock.ui.login.fragment.register.IRegisterFragment;
import com.projects.company.homes_lock.utils.helper.ValidationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;

import static com.projects.company.homes_lock.utils.helper.DataHelper.isInstanceOfList;

public class UserViewModel extends AndroidViewModel
        implements
        NetworkListener.SingleNetworkListener,
        NetworkListener.ListNetworkListener,
        ILocalRepository {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    private String requestType = "";
    //endregion Declare Variables

    //region Declare Objects
    private ILoginFragment mILoginFragment;
    private IRegisterFragment mIRegisterFragment;
    private IAddLockFragment mIAddLockFragment;
    private IManageMembersFragment mIManageMembersFragment;
    private LocalRepository mLocalRepository;
    private NetworkRepository mNetworkRepository;
    //endregion Declare Objects

    //region Constructor
    UserViewModel(Application application, ILoginFragment mILoginFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mILoginFragment = mILoginFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    UserViewModel(Application application, IRegisterFragment mIRegisterFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIRegisterFragment = mIRegisterFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    UserViewModel(Application application, IAddLockFragment mIAddLockFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIAddLockFragment = mIAddLockFragment;
        this.mLocalRepository = new LocalRepository(application);
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }

    UserViewModel(Application application, IManageMembersFragment mIManageMembersFragment) {
        super(application);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        this.mIManageMembersFragment = mIManageMembersFragment;
        this.mLocalRepository = new LocalRepository(application);//TODO if not need delete this line
        this.mNetworkRepository = new NetworkRepository();
        //endregion Initialize Objects
    }
    //endregion Constructor

    //region Network Callbacks
    @Override
    public void onResponse(Object response) {
        if (isInstanceOfList(response, User.class.getName())) {
            if (mIManageMembersFragment != null)
                mIManageMembersFragment.onGetUserLockDataSuccessful((List<User>) response);
        } else if (response instanceof User) {
            if (mILoginFragment != null)
                mILoginFragment.onLoginSuccessful(response);
            else if (mIRegisterFragment != null)
                mIRegisterFragment.onRegisterSuccessful(response);
            else if (mIAddLockFragment != null)
                mIAddLockFragment.onGetUserSuccessful((User) response);
        } else if (response instanceof ResponseBody) {
            switch (getRequestType()) {
                case "removeLockMember":
                    if (mIManageMembersFragment != null) {
                        try {
                            mIManageMembersFragment.onRemoveMemberSuccessful((
                                    new JSONObject(((ResponseBody) response).source().toString()
                                            .replace("[text=", "")
                                            .replace("]", "")
                                            .replace("\"", "")))
                                    .getLong("deletionTime"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onSingleNetworkListenerFailure(Object response) {
        if (getRequestType().equals("login") && mILoginFragment != null)
            mILoginFragment.onLoginFailed((FailureModel) response);
        else if (getRequestType().equals("register") && mIRegisterFragment != null)
            mIRegisterFragment.onRegisterFailed((FailureModel) response);
        else if (getRequestType().equals("getUserWithObjectId") && mIManageMembersFragment != null)
            mIAddLockFragment.onGetUserFailed((FailureModel) response);
        else if (getRequestType().equals("removeLockMember") && mIManageMembersFragment != null)
            mIManageMembersFragment.onRemoveMemberFailed((ResponseBodyFailureModel) response);
    }

    @Override
    public void onListNetworkListenerFailure(FailureModel response) {
        if (getRequestType().equals("getLockUsersByLockObjectId") && mIManageMembersFragment != null)
            mIManageMembersFragment.onGetUserLockDataFailed(response);
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

    //region Declare Online Methods
    public void login(String email, String password) {
        setRequestType("login");
        mNetworkRepository.login(this, new LoginModel(email, password));
    }

    public void register(RegisterModel mRegisterModel, String mConfirmPassword) {
        if (ValidationHelper.validateEquality(mRegisterModel.getPassword(), mConfirmPassword)) {
            setRequestType("register");
            mNetworkRepository.register(this, mRegisterModel);
        } else if (mIRegisterFragment != null)
            mIRegisterFragment.onRegisterFailed(new FailureModel("Confirm password not match."));
    }

    public void getUserWithObjectId(String userObjectId) {
        setRequestType("getUserWithObjectId");
        mNetworkRepository.getUserWithObjectId(this, userObjectId);
    }

    public void getLockUsersByLockObjectId(String lockObjectId) {
        setRequestType("getLockUsersByLockObjectId");
        mNetworkRepository.getLockUsersByLockObjectId(this, lockObjectId);
    }

    public void removeLockMember(String userLockObjectId) {
        setRequestType("removeLockMember");
        mNetworkRepository.removeDeviceForOneMember(this, userLockObjectId);
    }
    //endregion Declare Online Methods

    //region Declare Local Methods
    public void insertUser(User user) {
        mLocalRepository.insertUser(user, this);
    }
    //endregion Declare Local Methods

    //region Declare Methods
    private void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private String getRequestType() {
        return requestType;
    }
    //endregion Declare Methods
}