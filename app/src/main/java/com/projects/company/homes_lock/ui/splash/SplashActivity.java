package com.projects.company.homes_lock.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.models.viewmodels.SplashViewModel;
import com.projects.company.homes_lock.ui.login.activity.LoginActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startActivity(new Intent(this, LoginActivity.class));
        }

        finish();

        return true;
    }
}
