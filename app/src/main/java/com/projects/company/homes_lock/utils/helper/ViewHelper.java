package com.projects.company.homes_lock.utils.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * This is Helper Class helps Views
 */

public class ViewHelper {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    //region Declare Methods
    public static boolean isValidDevice() {
        return true;
    }

    public static void setFragment(AppCompatActivity parent, int containerId, Fragment fragment) {
        FragmentManager mFragmentManager = parent.getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mTransaction.add(containerId, fragment, fragment.getTag());
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }
    //endregion Declare Methods
}
