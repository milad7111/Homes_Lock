package com.projects.company.homes_lock.ui.proservices.activity;

import android.os.Bundle;

import com.projects.company.homes_lock.base.BaseActivity;
import com.projects.company.homes_lock.R;

public class ProServicesActivity extends BaseActivity implements ProServicesActivityContract.mMvpView {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Views
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    ProServicesActivityPresenter mProServicesActivityPresenter;
    //endregion Declare Objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_services);

        //region Initialize Views
        //endregion Initialize Views

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mProServicesActivityPresenter = new ProServicesActivityPresenter(ProServicesActivity.this);
        //endregion Initialize Objects

        //region Setup Views
        //endregion Setup Views
    }

    //region Declare Methods
    //endregion Declare Methods
}
