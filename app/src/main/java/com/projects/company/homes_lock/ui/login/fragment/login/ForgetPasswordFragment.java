package com.projects.company.homes_lock.ui.login.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment implements ILoginFragment {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_forget_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        //endregion Initialize Views

        //region Setup Views
        //endregion Setup Views
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
    //endregion ILoginFragment

    //region Declare Methods
    //endregion Declare Methods
}
