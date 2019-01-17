package com.projects.company.homes_lock.base;

/*
  This is Base Activity for All Activities
 */

import android.support.v7.app.AppCompatActivity;

import static com.projects.company.homes_lock.utils.helper.DialogHelper.handleProgressDialog;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleProgressDialog(null, null, null, false);
    }
}