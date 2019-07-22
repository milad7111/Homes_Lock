package com.projects.company.homes_lock.ui.aboutus;

import android.os.Bundle;
import android.view.MenuItem;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

public class AboutUsActivity extends BaseActivity {

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //endregion Main Callbacks
}
