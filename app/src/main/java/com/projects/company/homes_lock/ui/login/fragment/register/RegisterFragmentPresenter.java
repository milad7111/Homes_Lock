package com.projects.company.homes_lock.ui.login.fragment.register;

/**
 * This is Presenter for {@link RegisterFragment}
 */

public class RegisterFragmentPresenter implements RegisterFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private RegisterFragmentContract.mMvpView mRegisterFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    RegisterFragmentPresenter(RegisterFragmentContract.mMvpView _mRegisterFragmentView) {
        this.mRegisterFragmentView = _mRegisterFragmentView;
    }
}