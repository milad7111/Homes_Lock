package com.projects.company.homes_lock.Ui.ProServices.Fragment.ProServicesList;

/**
 * This is Presenter for {@link ProServicesListFragment}
 */

public class ProServicesListFragmentPresenter implements ProServicesListFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private ProServicesListFragmentContract.mMvpView mProServicesListFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    ProServicesListFragmentPresenter(ProServicesListFragmentContract.mMvpView _mProServicesListFragmentView) {
        this.mProServicesListFragmentView = _mProServicesListFragmentView;
    }
}