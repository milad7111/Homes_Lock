package com.projects.company.homes_lock.ui.login.activity;

import android.os.Bundle;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.ui.login.fragment.login.LoginFragment;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

public class LoginActivity extends BaseActivity implements ILoginActivity {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
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
        //endregion Initialize Objects

        //region Setup Views
        //endregion Setup Views

        ViewHelper.setFragment(this, R.id.frg_login_activity, new LoginFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewHelper.setContext(LoginActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //region Declare Methods
    //endregion Declare Methods
}
