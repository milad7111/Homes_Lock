package com.projects.company.homes_lock.ui.login.fragment.login;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.LoginViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.ui.login.fragment.register.RegisterFragment;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static com.projects.company.homes_lock.base.BaseApplication.setUserLoginMode;
import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment
        implements
        ILoginFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private TextInputEditText tietEmail;
    private TextInputEditText tietPassword;

    private TextView txvDirectConnect;
    private TextView txvSignUp;
    private TextView txvForgetPassword;

    private Button btnLogin;
    //endregion Declare Views

    //region Declare Variables
    private int savedDevicesIndex = 0;

    private String lockObjectId = "";
    private String userLockObjectId = "";
    //endregion Declare Variables

    //region Declare Objects
    private UserViewModel mUserViewModel;
    private DeviceViewModel mDeviceViewModel;

    private List<Device> notSavedDevices;
    //endregion Declare Objects

    //region Constructor
    public LoginFragment() {
    }
    //endregion Constructor

    //region Main CallBacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        notSavedDevices = new ArrayList<>();
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        tietEmail = view.findViewById(R.id.tiet_email_login_fragment);
        tietPassword = view.findViewById(R.id.tiet_password_login_fragment);
        btnLogin = view.findViewById(R.id.btn_login_login_fragment);
        txvDirectConnect = view.findViewById(R.id.txv_direct_connect_login_fragment);
        txvSignUp = view.findViewById(R.id.txv_sign_up_login_fragment);
        txvForgetPassword = view.findViewById(R.id.txv_forget_password_login_fragment);
        //endregion Initialize Views

        //region Setup Views
        btnLogin.setOnClickListener(this);
        txvDirectConnect.setOnClickListener(this);
        txvSignUp.setOnClickListener(this);
        txvForgetPassword.setOnClickListener(this);
        //endregion Setup Views

        //region Initialize Objects
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new LoginViewModelFactory(getActivity().getApplication(), this))
                .get(UserViewModel.class);

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public void onPause() {
        super.onPause();
        handleProgressDialog(null, null, null, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login_fragment:
                handleProgressDialog(getContext(), null, "Login process ...", true);
                this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
                    notSavedDevices = new ArrayList<>();

                    for (Device device : devices) {
                        if (!device.isLockSavedInServer())
                            notSavedDevices.add(device);
                    }

                    mUserViewModel.login(tietEmail.getText().toString(), tietPassword.getText().toString());
                });
                break;
            case R.id.txv_direct_connect_login_fragment:
                setUserLoginMode(false);
                startActivity(new Intent(getActivity(), LockActivity.class));
                break;
            case R.id.txv_sign_up_login_fragment:
                ViewHelper.setFragment((AppCompatActivity) getActivity(), R.id.frg_login_activity, new RegisterFragment());
                break;
            case R.id.txv_forget_password_login_fragment:
                ViewHelper.setFragment((AppCompatActivity) getActivity(), R.id.frg_login_activity, new ForgetPasswordFragment());
                break;
        }
    }
    //endregion Main CallBacks

    //region Login CallBacks
    @Override
    public void onLoginSuccessful(User user) {
        BaseApplication.activeUserObjectId = user.getObjectId();
        BaseApplication.activeUserToken = user.getUserToken();
        BaseApplication.activeUserEmail = user.getEmail();

        if (notSavedDevices.size() != 0)
            saveLocalDevicesToServer();
        else
            mUserViewModel.insertUser(user);
    }

    @Override
    public void onLoginFailed(FailureModel response) {
//        Log.i(this.getClass().getSimpleName(), response.getFailureMessage());
        Toast.makeText(getActivity(), response.getFailureMessage(), Toast.LENGTH_LONG).show();
        handleProgressDialog(null, null, null, false);
    }

    @Override
    public void onFindLockInOnlineDataBaseSuccessful(String lockObjectId) {
        this.lockObjectId = lockObjectId;
        this.mDeviceViewModel.insertOnlineUserLock(
                new UserLockModel(
                        notSavedDevices.get(savedDevicesIndex).getBleDeviceName(),
                        true,
                        notSavedDevices.get(savedDevicesIndex).isFavoriteStatus()
                ));
    }

    @Override
    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        this.userLockObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(this.userLockObjectId, this.lockObjectId);
    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {

    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful)
            mDeviceViewModel.addUserLockToUser(BaseApplication.activeUserObjectId, this.userLockObjectId);
        else
            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean response) {
        if (++savedDevicesIndex < notSavedDevices.size())
            saveLocalDevicesToServer();
        else
            mUserViewModel.getUserWithObjectId(BaseApplication.activeUserObjectId);
    }

    @Override
    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onGetUserSuccessful(User user) {
        mUserViewModel.insertUser(user);
    }

    @Override
    public void onGetUserFailed(FailureModel response) {

    }

    @Override
    public void onDataInsert(Long id) {
        if (id != -1) {
            clearViews();
            startActivity(new Intent(getActivity(), LockActivity.class));
            setUserLoginMode(true);
        }
    }
    //endregion Login CallBacks

    //region Declare Methods
    private void clearViews() {
        tietEmail.setText(null);
        tietPassword.setText(null);
    }

    private void saveLocalDevicesToServer() {
        this.mDeviceViewModel.validateLockInOnlineDatabase(this, notSavedDevices.get(savedDevicesIndex).getSerialNumber());
    }
    //endregion Declare Methods
}
