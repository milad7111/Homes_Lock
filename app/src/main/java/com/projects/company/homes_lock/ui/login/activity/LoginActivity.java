package com.projects.company.homes_lock.ui.login.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment;
import com.projects.company.homes_lock.ui.login.fragment.register.RegisterFragment;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

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
        ViewHelper.setFragment(this, R.id.frg_login_activity, new LoginFragment());
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.frg_login_activity);

        if (mFragment instanceof LoginFragment)
            finishAffinity();
        else if (mFragment instanceof RegisterFragment)
            ViewHelper.setFragment(this, R.id.frg_login_activity, new LoginFragment());
        else
            super.onBackPressed();
    }
    //endregion Main Callbacks

    //region Declare Methods
    //endregion Declare Methods
}
