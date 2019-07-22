package com.projects.company.homes_lock.ui.profile;

import android.os.Bundle;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

public class ProfileActivity extends BaseActivity {

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //endregion Main Callbacks
}
