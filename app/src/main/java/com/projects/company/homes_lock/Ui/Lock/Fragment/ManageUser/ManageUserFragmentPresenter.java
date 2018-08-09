package com.projects.company.homes_lock.Ui.Lock.Fragment.ManageUser;

/**
 * This is Presenter for {@link ManageUserFragment}
 */

public class ManageUserFragmentPresenter implements ManageUserFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ManageUserFragmentContract.mMvpView mManageUserFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    ManageUserFragmentPresenter(ManageUserFragmentContract.mMvpView _mManageUserFragmentView) {
        this.mManageUserFragmentView = _mManageUserFragmentView;
    }
}