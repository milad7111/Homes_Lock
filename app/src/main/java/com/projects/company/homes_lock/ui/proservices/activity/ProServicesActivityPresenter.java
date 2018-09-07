package com.projects.company.homes_lock.ui.proservices.activity;

/**
 * This is Presenter for {@link ProServicesActivity}
 */

public class ProServicesActivityPresenter implements ProServicesActivityContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ProServicesActivityContract.mMvpView mProServicesActivityView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    ProServicesActivityPresenter(ProServicesActivityContract.mMvpView _mProServicesActivityView) {
        this.mProServicesActivityView = _mProServicesActivityView;
    }
}