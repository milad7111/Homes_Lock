package com.projects.company.homes_lock.ui.proservices.activity;

import android.os.Bundle;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

public class ProServicesActivity extends BaseActivity implements IProServicesActivity {

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_services);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //endregion Main Callbacks
}
