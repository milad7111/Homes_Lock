package com.projects.company.homes_lock.ui.login.fragment.login;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.request.UserDeviceModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.models.viewmodels.UserViewModel;
import com.projects.company.homes_lock.models.viewmodels.UserViewModelFactory;
import com.projects.company.homes_lock.ui.device.activity.DeviceActivity;
import com.projects.company.homes_lock.ui.login.fragment.register.RegisterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.base.BaseApplication.activeUserEmail;
import static com.projects.company.homes_lock.base.BaseApplication.activeUserObjectId;
import static com.projects.company.homes_lock.base.BaseApplication.activeUserToken;
import static com.projects.company.homes_lock.base.BaseApplication.logUserCrashesInFabric;
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

    private String deviceObjectId = "";
    private String userDeviceObjectId = "";
    private String deviceSerialNumber = "";

    private List<String> wrongSerialNumberDevices;
    //endregion Declare Variables

    //region Declare Arrays & Lists
    //endregion Declare Arrays & Lists

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

        //region Initialize Objects
        this.mUserViewModel = ViewModelProviders.of(
                this,
                new UserViewModelFactory(Objects.requireNonNull(getActivity()).getApplication(), this))
                .get(UserViewModel.class);

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        this.mDeviceViewModel.getAllLocalDevices().observe(this, devices -> {
            notSavedDevices = new ArrayList<>();

            for (Device device : devices)
                if (!device.isDeviceSavedInServer())
                    notSavedDevices.add(device);
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();

        wrongSerialNumberDevices = new ArrayList<>();
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
        logUserCrashesInFabric();
        logAuthEvent();

        for (Device localDevice : notSavedDevices) {
            for (UserLock userLock : user.getRelatedUserLocks()) {
                if (userLock.getRelatedDevice().getSerialNumber().equals(localDevice.getSerialNumber())) {
                    notSavedDevices.remove(localDevice);
                    break;
                }
            }
        }

        if (notSavedDevices.size() != 0) {
            openProgressDialog(getContext(), "Login process", "Syncing database");
            saveNotSavedLocalDevicesToServer();
        } else
            mUserViewModel.insertUser(user);
    }

    @Override
    public void onLoginFailed(FailureModel response) {
        closeProgressDialog();
        showToast(response.getFailureMessage());
    }

    @Override
    public void onFindLockInOnlineDataBaseSuccessful(String lockObjectId) {
        this.deviceObjectId = lockObjectId;
        this.mDeviceViewModel.insertOnlineUserDevice(this,
                new UserDeviceModel(
                        notSavedDevices.get(savedDevicesIndex).getBleDeviceName(),
                        true,
                        notSavedDevices.get(savedDevicesIndex).isFavoriteStatus()
                ));
    }

    @Override
    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {
        wrongSerialNumberDevices.add("Device "
                + (wrongSerialNumberDevices.size() + 1) + " with serial number : "
                + this.deviceSerialNumber
                + " not exist in server");

        handleNextSync();
    }

    @Override
    public void onInsertUserLockSuccessful(UserLock userLock) {
        this.userDeviceObjectId = userLock.getObjectId();
        mDeviceViewModel.addLockToUserLock(this, this.userDeviceObjectId, this.deviceObjectId);
    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {
    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean addLockToUserLockSuccessful) {
        if (addLockToUserLockSuccessful)
            mDeviceViewModel.addUserLockToUser(this, activeUserObjectId, this.userDeviceObjectId);
        else
            onAddLockToUserLockFailed(new ResponseBodyFailureModel("add lock to user lock failed."));
    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {
    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean response) {
        if (!this.deviceSerialNumber.isEmpty()) {
            this.mDeviceViewModel.enablePushNotification(this.deviceSerialNumber);
            this.deviceSerialNumber = "";
        }

        handleNextSync();
    }

    @Override
    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {
    }

    private void handleNextSync() {
        if (++savedDevicesIndex < notSavedDevices.size())
            saveNotSavedLocalDevicesToServer();
        else {
            notSavedDevices.clear();
            mUserViewModel.getUserWithObjectId(activeUserObjectId);
        }
    }


    @Override
    public void onGetUserSuccessful(User user) {
        mUserViewModel.insertUser(user);
    }

    @Override
    public void onGetUserFailed(FailureModel response) {
    }

    @Override
    public void onDeviceRegistrationForPushNotificationSuccessful(String registrationId) {
        showToast(String.format("device registration done: %s", registrationId));
    }

    @Override
    public void onDeviceRegistrationForPushNotificationFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onDataInsert(Object object) {
        closeProgressDialog();
        if (object instanceof User) {
            Intent deviceActivity = new Intent(getActivity(), DeviceActivity.class);

            if (wrongSerialNumberDevices.size() != 0) {
                StringBuilder message = new StringBuilder();

                for (String serialNumber : wrongSerialNumberDevices)
                    message.append(serialNumber).append("\n");

                deviceActivity.putExtra("notSyncedDevicesMessage", message.toString());
            }

            startActivity(deviceActivity);
            setUserLoginMode(true);
            clearViews();
        }
    }
    //endregion Login CallBacks

    //region Declare Methods
    public void logAuthEvent() {
        Answers.getInstance().logCustom(new CustomEvent("Auth")
                .putCustomAttribute("Login", activeUserEmail));
    }

    private void clearViews() {
        tietEmailLoginFragment.setText(null);
        tietPasswordLoginFragment.setText(null);
    }

    private void saveNotSavedLocalDevicesToServer() {
        this.deviceSerialNumber = notSavedDevices.get(savedDevicesIndex).getSerialNumber();
        this.mDeviceViewModel.validateLockInOnlineDatabase(this, notSavedDevices.get(savedDevicesIndex).getSerialNumber());
    }

    private void showToast(String message) {
        Objects.requireNonNull(LoginFragment.this.getActivity()).runOnUiThread(() ->
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
    //endregion Declare Methods
}
