package com.projects.company.homes_lock.ui.device.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.ui.device.fragment.adddevice.AddDeviceFragment;
import com.projects.company.homes_lock.ui.device.fragment.gatewaypage.GatewayPageFragment;
import com.projects.company.homes_lock.ui.device.fragment.lockpage.LockPageFragment;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class CustomDeviceAdapter extends SmartFragmentStatePagerAdapter {

    //region Declare List & Arrays
    private List<Device> mDeviceList;
    private List<Fragment> mFragments;
    //endregion Declare List & Arrays

    //region Constructor
    public CustomDeviceAdapter(FragmentManager fragmentManager, List<Device> mDeviceList) {
        super(fragmentManager);
        this.mDeviceList = mDeviceList;
        this.mFragments = new ArrayList<>();
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public int getCount() {
        return mDeviceList.size() + 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (position != getCount() - 1) {
            if (mDeviceList.get(position).getDeviceType().equals("LOCK")) {
                mFragments.add(LockPageFragment.newInstance(mDeviceList.get(position)));
                return mFragments.get(mFragments.size() - 1);
            } else if (mDeviceList.get(position).getDeviceType().equals("GTWY")) {
                mFragments.add(GatewayPageFragment.newInstance(mDeviceList.get(position)));
                return mFragments.get(mFragments.size() - 1);
            }

            throw new InputMismatchException();
        } else {
            mFragments.add(AddDeviceFragment.newInstance());
            return mFragments.get(mFragments.size() - 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

    private boolean isDevicePage(int position) {
        return position != (getCount() - 1);
    }

    private String getDeviceType(int position) {
        return mDeviceList.get(position).getDeviceType();
    }

    boolean checkDisconnectStatus(int position) {
        if (isDevicePage(position)) {
            switch (getDeviceType(position)) {
                case "LOCK":
                    return (((LockPageFragment) mFragments.get(position)).disconnectDevice());
                case "GTWY":
                    return (((GatewayPageFragment) mFragments.get(position)).disconnectDevice());
            }
        }

        return true;
    }
    //endregion Main Callbacks

    //region Declare Methods
    void setDevices(List<Device> devices) {
        this.mDeviceList = devices;
    }
    //region Declare Methods
}