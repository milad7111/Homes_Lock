package com.projects.company.homes_lock.Ui.ProServices.Fragment.GrantProServices;

/**
 * This is Presenter for {@link GrantProServicesFragment}
 */

public class GrantProServicesFragmentPresenter implements GrantProServicesFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private GrantProServicesFragmentContract.mMvpView mGrantProServicesFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    GrantProServicesFragmentPresenter(GrantProServicesFragmentContract.mMvpView _mGrantProServicesFragmentView) {
        this.mGrantProServicesFragmentView = _mGrantProServicesFragmentView;
    }
}