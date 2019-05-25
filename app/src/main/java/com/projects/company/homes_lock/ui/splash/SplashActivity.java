package com.projects.company.homes_lock.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.models.viewmodels.SplashViewModel;
import com.projects.company.homes_lock.ui.login.activity.LoginActivity;

public class SplashActivity extends BaseActivity {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private SplashViewModel mSplashViewModel;
    //endregion Declare Objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mSplashViewModel = new SplashViewModel(this.getApplication());
        //endregion Initialize Objects
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
                startActivity(new Intent(this, LoginActivity.class));
//        }

        finish();

        return true;
    }
}
