package com.projects.company.homes_lock.utils.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.projects.company.homes_lock.R;

/**
 * This is Helper Class helps Views
 */

public class ViewHelper {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private static Context mContext;
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

    public static void setLockStatusImage(ImageView imageViewLock, byte[] value) {
        if (Boolean.parseBoolean(new String(value)))
            imageViewLock.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_lock_close));
        else
            imageViewLock.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_lock_open));

    }

    public static void setContext(Context mContext) {
        ViewHelper.mContext = mContext;
    }
    //endregion Declare Methods
}
