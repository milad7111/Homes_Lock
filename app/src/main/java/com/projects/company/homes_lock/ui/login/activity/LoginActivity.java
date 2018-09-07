package com.projects.company.homes_lock.ui.login.activity;

import android.os.Bundle;

import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.R;

public class LoginActivity extends BaseActivity implements LoginActivityContract.mMvpView {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    LoginActivityPresenter mLoginActivityPresenter;
    //endregion Declare Objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region Initialize Views
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mLoginActivityPresenter = new LoginActivityPresenter(LoginActivity.this);
        //endregion Initialize Objects

        //region Setup Views
        //endregion Setup Views
    }

    //region Declare Methods
    //endregion Declare Methods
}
