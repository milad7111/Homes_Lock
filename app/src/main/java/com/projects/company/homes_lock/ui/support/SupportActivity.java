package com.projects.company.homes_lock.ui.support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

public class SupportActivity extends BaseActivity {

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //endregion Main Callbacks
}
