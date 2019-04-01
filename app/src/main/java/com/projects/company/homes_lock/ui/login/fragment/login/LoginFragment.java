package com.projects.company.homes_lock.ui.login.fragment.login;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.LoginViewModelFactory;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.ui.login.fragment.register.RegisterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.base.BaseApplication.activeUserEmail;
import static com.projects.company.homes_lock.base.BaseApplication.activeUserObjectId;
import static com.projects.company.homes_lock.base.BaseApplication.activeUserToken;
import static com.projects.company.homes_lock.base.BaseApplication.setUserLoginMode;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.openProgressDialog;
import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class LoginFragment extends BaseFragment
        implements
        ILoginFragment,
        View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private TextInputEditText tietEmailLoginFragment;
    private TextInputEditText tietPasswordLoginFragment;

    private TextView txvDirectConnectLoginFragment;
    private TextView txvSignUpLoginFragment;
    private TextView txvForgetPasswordLoginFragment;

    private RippleView rpvDirectConnectLoginFragment;
    private RippleView rpvSignUpLoginFragment;
    private RippleView rpvForgetPasswordLoginFragment;

    private Button btnLoginLoginFragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        tietEmailLoginFragment = view.findViewById(R.id.tiet_email_login_fragment);
        tietPasswordLoginFragment = view.findViewById(R.id.tiet_password_login_fragment);

        btnLoginLoginFragment = view.findViewById(R.id.btn_login_login_fragment);

        txvDirectConnectLoginFragment = view.findViewById(R.id.txv_direct_connect_login_fragment);
        txvSignUpLoginFragment = view.findViewById(R.id.txv_sign_up_login_fragment);
        txvForgetPasswordLoginFragment = view.findViewById(R.id.txv_forget_password_login_fragment);

        rpvDirectConnectLoginFragment = view.findViewById(R.id.rpv_direct_connect_login_fragment);
        rpvSignUpLoginFragment = view.findViewById(R.id.rpv_sign_up_login_fragment);
        rpvForgetPasswordLoginFragment = view.findViewById(R.id.rpv_forget_password_login_fragment);
        //endregion Initialize Views

        //region Setup Views
        btnLoginLoginFragment.setOnClickListener(this);

        txvDirectConnectLoginFragment.setOnClickListener(this);
        txvSignUpLoginFragment.setOnClickListener(this);
        txvForgetPasswordLoginFragment.setOnClickListener(this);

        rpvDirectConnectLoginFragment.setOnClickListener(this);
        rpvSignUpLoginFragment.setOnClickListener(this);
        rpvForgetPasswordLoginFragment.setOnClickListener(this);
        //endregion Setup Views

        //region Initialize Objects
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new LoginViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), this))
                .get(UserViewModel.class);

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        //endregion Initialize Objects
    }

    @Override
    public void onPause() {
        super.onPause();
        closeProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login_fragment:
                openProgressDialog(getContext(), null, "Login process ...");
                mUserViewModel.login(
                        Objects.requireNonNull(tietEmailLoginFragment.getText()).toString(),
                        Objects.requireNonNull(tietPasswordLoginFragment.getText()).toString());
//                this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
//                    notSavedDevices = new ArrayList<>();
//
//                    for (Device device : devices) {
//                        if (!device.isLockSavedInServer())
//                            notSavedDevices.add(device);
//                    }
//
//                    mUserViewModel.login(tietEmailLoginFragment.getText().toString(), tietPasswordLoginFragment.getText().toString());
//                });
                break;
            case R.id.txv_direct_connect_login_fragment:
            case R.id.rpv_direct_connect_login_fragment:
                setUserLoginMode(false);
                startActivity(new Intent(getActivity(), DeviceActivity.class));
                break;
            case R.id.txv_sign_up_login_fragment:
            case R.id.rpv_sign_up_login_fragment:
                addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_login_activity, new RegisterFragment());
                break;
            case R.id.txv_forget_password_login_fragment:
            case R.id.rpv_forget_password_login_fragment:
                addFragment((AppCompatActivity) Objects.requireNonNull(getActivity()), R.id.frg_login_activity, new ForgetPasswordFragment());
                break;
        }
    }
    //endregion Main CallBacks

    //region Login CallBacks
    @Override
    public void onLoginSuccessful(User user) {
        activeUserObjectId = user.getObjectId();
        activeUserToken = user.getUserToken();
        activeUserEmail = user.getEmail();

//        if (notSavedDevices.size() != 0)
//            saveLocalDevicesToServer();
//        else
        mUserViewModel.insertUser(user);
    }

    @Override
    public void onLoginFailed(FailureModel response) {
        Toast.makeText(getActivity(), response.getFailureMessage(), Toast.LENGTH_LONG).show();
        closeProgressDialog();
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
//        if (++savedDevicesIndex < notSavedDevices.size())
//            saveLocalDevicesToServer();
//        else
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
    public void onDataInsert(Object object) {
        if (object instanceof User) {
            clearViews();
            startActivity(new Intent(getActivity(), DeviceActivity.class));
            setUserLoginMode(true);
        }
    }
    //endregion Login CallBacks

    //region Declare Methods
    private void clearViews() {
        tietEmailLoginFragment.setText(null);
        tietPasswordLoginFragment.setText(null);
    }

    private void saveLocalDevicesToServer() {
        this.mDeviceViewModel.validateLockInOnlineDatabase(this, notSavedDevices.get(savedDevicesIndex).getSerialNumber());
    }
    //endregion Declare Methods
}
