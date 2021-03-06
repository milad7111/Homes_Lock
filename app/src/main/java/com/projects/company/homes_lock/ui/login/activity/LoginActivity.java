package com.projects.company.homes_lock.ui.login.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.crashlytics.android.answers.Answers;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment;
import com.projects.company.homes_lock.ui.login.fragment.register.RegisterFragment;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

import static com.projects.company.homes_lock.utils.helper.ViewHelper.addFragment;

public class LoginActivity extends BaseActivity implements ILoginActivity {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region Initialize Views
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        //endregion Initialize Objects

        //region Setup Views
        //endregion Setup Views

        ViewHelper.setContext(LoginActivity.this);
        addFragment(this, R.id.frg_login_activity, new LoginFragment());
        Fabric.with(this, new Answers());
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.frg_login_activity);

        if (mFragment instanceof LoginFragment)
            finishAffinity();
        else if (mFragment instanceof RegisterFragment)
            addFragment(this, R.id.frg_login_activity, new LoginFragment());
        else
            super.onBackPressed();
    }
    //endregion Main Callbacks

    //region Declare Methods
    //endregion Declare Methods
}
