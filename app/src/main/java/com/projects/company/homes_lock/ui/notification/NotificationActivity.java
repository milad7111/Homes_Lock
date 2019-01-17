package com.projects.company.homes_lock.ui.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }
}
