package com.projects.company.homes_lock.ui.login.fragment.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseFragment;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

import java.util.Objects;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ForgetPasswordFragment extends BaseFragment implements ILoginFragment, View.OnClickListener {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    private Button btnCancelDialogForgetPassword;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    //region Constructor
    public ForgetPasswordFragment() {
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        btnCancelDialogForgetPassword = view.findViewById(R.id.btn_cancel_dialog_forget_password);
        //endregion Initialize Views

        //region Setup Views
        btnCancelDialogForgetPassword.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_dialog_forget_password:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                break;
        }
    }
    //endregion Main Callbacks

    //region ILoginFragment
    @Override
    public void onLoginSuccessful(User user) {

    }

    @Override
    public void onLoginFailed(FailureModel response) {

    }

    @Override
    public void onDataInsert(Object id) {

    }

    @Override
    public void onFindLockInOnlineDataBaseSuccessful(String response) {

    }

    @Override
    public void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onInsertUserLockSuccessful(UserLock response) {

    }

    @Override
    public void onInsertUserLockFailed(FailureModel response) {

    }

    @Override
    public void onAddLockToUserLockSuccessful(Boolean response) {

    }

    @Override
    public void onAddLockToUserLockFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onAddUserLockToUserSuccessful(Boolean response) {

    }

    @Override
    public void onAddUserLockToUserFailed(ResponseBodyFailureModel response) {

    }

    @Override
    public void onGetUserSuccessful(User response) {

    }

    @Override
    public void onGetUserFailed(FailureModel response) {

    }

    @Override
    public void onDeviceRegistrationForPushNotificationSuccessful(String registrationId) {

    }

    @Override
    public void onDeviceRegistrationForPushNotificationFailed(ResponseBodyFailureModel response) {

    }
    //endregion ILoginFragment

    //region Declare Methods
    //endregion Declare Methods
}
