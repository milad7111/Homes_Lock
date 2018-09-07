package com.projects.company.homes_lock.ui.login.fragment.login;

/**
 * This is Presenter for {@link LoginFragment}
 */

public class LoginFragmentPresenter implements LoginFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LoginFragmentContract.mMvpView mLoginFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    LoginFragmentPresenter(LoginFragmentContract.mMvpView _mLoginFragmentView) {
        this.mLoginFragmentView = _mLoginFragmentView;
    }
}