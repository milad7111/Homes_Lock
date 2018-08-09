package com.projects.company.homes_lock.Ui.Login.Activity;

/**
 * This is Presenter for {@link LoginActivity}
 */

public class LoginActivityPresenter implements LoginActivityContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LoginActivityContract.mMvpView mLoginActivityView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    LoginActivityPresenter(LoginActivityContract.mMvpView _mLoginActivityView) {
        this.mLoginActivityView = _mLoginActivityView;
    }
}